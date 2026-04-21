package com.rae.creatingspace.content.worldgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.lwjgl.system.NonnullDefault;


/**
 * Advanced terrain erosion filter based on stacked faded gullies.
 * Ported from GLSL shader by Rune Skovbo Johansen.
 * <p>
 * For more on the technique, see:
 * <a href="https://blog.runevision.com/2026/03/fast-and-gorgeous-erosion-filter.html">Fast and Gorgeous Erosion Filter</a>
 * <p>
 * This erosion technique creates realistic terrain features including:
 * <ul>
 * <li>Natural-looking gullies aligned with terrain slopes</li>
 * <li>Sharp ridges on peaks with smooth valleys</li>
 * <li>Multi-octave detail for fine erosion patterns</li>
 * </ul>
 * <p>
 * Original shader copyright (c) 2025 Rune Skovbo Johansen
 * <br>Licensed under Mozilla Public License, v. 2.0
 * <br><a href="https://mozilla.org/MPL/2.0/">https://mozilla.org/MPL/2.0/</a>
 */
@NonnullDefault
public class PhacelleErosionNoise implements DensityFunction.SimpleFunction, INeedWorldSeed {

    private static final MapCodec<PhacelleErosionNoise> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(
                    DensityFunction.HOLDER_HELPER_CODEC.fieldOf("height_map").forGetter(i -> i.heightMap),
                        Codec.FLOAT.optionalFieldOf("height_offset_value", 0f).forGetter(i -> i.heightOffsetValue),
                    Codec.FLOAT.optionalFieldOf("height_offset_blend", 1f).forGetter(i -> i.heightOffsetBlend),

                    ErosionParams.CODEC.optionalFieldOf("erosion", new ErosionParams(32f, 0.22f, 0.5f, 1.5f)).forGetter(i ->
                            new ErosionParams(i.erosionScale, i.erosionStrength, i.erosionGullyWeight, i.erosionDetail)),
                    RoundingParams.CODEC.optionalFieldOf("rounding", new RoundingParams(0.1f, 0.0f, 0.1f, 2.0f)).forGetter(i ->
                            new RoundingParams(i.ridgeRounding, i.creaseRounding, i.roundingMultInitial, i.roundingMultPerOctave)),
                    OnsetParams.CODEC.optionalFieldOf("onset", new OnsetParams(1.25f, 1.25f, 2.8f, 1.5f)).forGetter(i ->
                            new OnsetParams(i.onsetInitial, i.onsetPerOctave, i.ridgeMapOnsetInitial, i.ridgeMapOnsetPerOctave)),
                    SlopeParams.CODEC.optionalFieldOf("slope", new SlopeParams(0.7f, 1.0f)).forGetter(i ->
                            new SlopeParams(i.assumedSlope, i.assumedSlopeMix)),
                    FractalParams.CODEC.optionalFieldOf("fractal", new FractalParams(5, 2.0f, 0.5f)).forGetter(i ->
                            new FractalParams(i.octaves, i.lacunarity, i.gain)),
                    NoiseParams.CODEC.optionalFieldOf("noise", new NoiseParams(0.7f, 0.5f)).forGetter(i ->
                            new NoiseParams(i.cellScale, i.normalization))
            ).apply(instance, (heightMap,heightOffsetValue, heightOffsetBlend,  erosion, rounding, onset, slope, fractal, noise) ->
                    new PhacelleErosionNoise(heightMap, heightOffsetValue, heightOffsetBlend,
                            erosion.scale, erosion.strength, erosion.gullyWeight, erosion.detail,
                            rounding.ridge, rounding.crease, rounding.multInitial, rounding.multPerOctave,
                            onset.initial, onset.perOctave, onset.ridgeMapInitial, onset.ridgeMapPerOctave,
                            slope.assumed, slope.mix,
                            fractal.octaves, fractal.lacunarity, fractal.gain,
                            noise.cellScale, noise.normalization)));

    public static final KeyDispatchDataCodec<PhacelleErosionNoise> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

    private static final float           TAU = (float) (2.0 * Math.PI);
    /**
     * The base heightmap to apply erosion to
     */
    private final        DensityFunction heightMap;

    /**
     * The scale of the erosion effect, affecting it both horizontally and vertically.
     */
    private final float erosionScale;

    /**
     * The strength of the erosion effect, affecting the magnitude of all octaves,
     * and indirectly affecting the directions of the gullies as a result.
     */
    private final float erosionStrength;

    /**
     * The magnitude of the gullies as a weight value from 0 to 1.
     * A value of 0 can sharpen peaks and valleys but feature virtually no gullies.
     * A value of 1 produces full gullies but may leave peaks and valleys rounded.
     */
    private final float erosionGullyWeight;

    /**
     * The overall detail of the erosion. Lower values restrict the effect of higher
     * frequency gullies to steeper slopes.
     */
    private final float erosionDetail;

    /**
     * An offset value between -1 and 1, where -1 only lowers, while +1 only raises.
     * The offset is proportional to the erosion strength parameter.
     */
    private final float heightOffsetValue;

    /**
     * Degree (0-1) to which the offset value is replaced by the negated fade target.
     * This has the effect of only raising at valleys and only lowering at peaks.
     */
    private final float heightOffsetBlend;

    /**
     * Rounding of ridges (peaks). Higher values = more rounded ridges.
     */
    private final float ridgeRounding;

    /**
     * Rounding of creases (valleys). Higher values = more rounded valleys.
     */
    private final float creaseRounding;

    /**
     * Multiplier applied to the initial height function for rounding compensation.
     */
    private final float roundingMultInitial;

    /**
     * Multiplier applied to each subsequent gully octave after the first.
     */
    private final float roundingMultPerOctave;

    /**
     * Onset used on the initial height function.
     */
    private final float onsetInitial;

    /**
     * Onset used on each gully octave.
     */
    private final float onsetPerOctave;

    /**
     * RidgeMap-specific onset used on the initial height function.
     */
    private final float ridgeMapOnsetInitial;

    /**
     * RidgeMap-specific onset used on each gully octave.
     */
    private final float ridgeMapOnsetPerOctave;

    /**
     * An assumed slope value to override the actual slope.
     */
    private final float assumedSlope;

    /**
     * The amount (0-1) to override the actual slope. 0 = use actual, 1 = use assumed.
     */
    private final float assumedSlopeMix;

    /**
     * Number of octave layers to apply.
     */
    private final int octaves;

    /**
     * Controls the frequency (inverse horizontal scale) of each octave relative to the last.
     */
    private final float lacunarity;

    /**
     * Controls the magnitude (vertical scale) of each octave relative to the last.
     */
    private final float gain;

    /**
     * Controls the sizes of Voronoi-like cells relative to the overall erosion scale.
     */
    private final float cellScale;

    /**
     * The degree of normalization applied in the Phacelle noise, between 0 and 1.
     */
    private final float normalization;

    /**
     * The world seed used for randomization in the hash function.
     */
    private long seed;

    /**
     * Creates an erosion noise function with custom parameters.
     */
    public PhacelleErosionNoise(
            DensityFunction heightMap,
            float heightOffsetValue,
            float heightOffsetBlend,
            float erosionScale,
            float erosionStrength,
            float erosionGullyWeight,
            float erosionDetail,
            float ridgeRounding,
            float creaseRounding,
            float roundingMultInitial,
            float roundingMultPerOctave,
            float onsetInitial,
            float onsetPerOctave,
            float ridgeMapOnsetInitial,
            float ridgeMapOnsetPerOctave,
            float assumedSlope,
            float assumedSlopeMix,
            int octaves,
            float lacunarity,
            float gain,
            float cellScale,
            float normalization) {
        this.heightMap = heightMap;
        this.heightOffsetValue = heightOffsetValue;
        this.heightOffsetBlend = heightOffsetBlend;
        this.erosionScale = erosionScale;
        this.erosionStrength = erosionStrength;
        this.erosionGullyWeight = erosionGullyWeight;
        this.erosionDetail = erosionDetail;
        this.ridgeRounding = ridgeRounding;
        this.creaseRounding = creaseRounding;
        this.roundingMultInitial = roundingMultInitial;
        this.roundingMultPerOctave = roundingMultPerOctave;
        this.onsetInitial = onsetInitial;
        this.onsetPerOctave = onsetPerOctave;
        this.ridgeMapOnsetInitial = ridgeMapOnsetInitial;
        this.ridgeMapOnsetPerOctave = ridgeMapOnsetPerOctave;
        this.assumedSlope = assumedSlope;
        this.assumedSlopeMix = assumedSlopeMix;
        this.octaves = octaves;
        this.lacunarity = lacunarity;
        this.gain = gain;
        this.cellScale = cellScale;
        this.normalization = normalization;
    }

    @Override
    public double compute(FunctionContext context) {
        // Sample the base heightmap
        double height = heightMap.compute(context);

        int x = context.blockX(), y = context.blockY(), z = context.blockZ();

        // Compute slope using finite differences (central difference method)
        // This approximates the derivative by sampling adjacent blocks
        double heightXPlus  = heightMap.compute(new SinglePointContext(x + 1, y, z));
        double heightXMinus = heightMap.compute(new SinglePointContext(x - 1, y, z));
        double heightZPlus  = heightMap.compute(new SinglePointContext(x, y, z + 1));
        double heightZMinus = heightMap.compute(new SinglePointContext(x, y, z - 1));

        // Central difference formula: (f(x+h) - f(x-h)) / 2h, where h=1 block
        float slopeX = (float) ((heightXPlus - heightXMinus) * 0.5);
        float slopeZ = (float) ((heightZPlus - heightZMinus) * 0.5);

        // Define the erosion fade target based on the altitude of the pre-eroded terrain.
        // The fade target should strive to be -1 at valleys and 1 at peaks, but overshooting is ok.
        // Auto-resolve from heightmap min/max: map [minValue, maxValue] to [-1, 1]
        double minHeight    = heightMap.minValue();
        double maxHeight    = heightMap.maxValue();
        double heightRange  = maxHeight - minHeight;
        double heightCenter = (minHeight + maxHeight) * 0.5;

        // Normalize height relative to center, scaled to produce -1 at valleys, +1 at peaks
        // Division by 0.15 of the range provides good sensitivity (adjustable if needed)
        float fadeTarget = heightRange > 1e-10
                ? (float) ((height - heightCenter) / (heightRange * 0.6))
                : 0.0f;
        fadeTarget = Mth.clamp(fadeTarget, -1.0f, 1.0f);

        // Apply erosion filter
        // Returns: heightDelta (x), slopeDelta (yz), magnitude (w), and ridgeMap
        ErosionResult result = applyErosion(
                x, z,
                slopeX,
                slopeZ,
                fadeTarget
        );

        // Offset according to the height offset parameter by multiplying it with the magnitude.
        // Control over whether the erosion effect raises or lowers the terrain:
        //   heightOffsetValue: An offset value between -1 and 1, where -1 only lowers, +1 only raises.
        //                      The offset is proportional to the erosion strength parameter.
        //   heightOffsetBlend: Degree (0-1) to replace offset with -fadeTarget.
        //                      This has the effect of only raising at valleys and only lowering at peaks,
        //                      which largely preserves the minima and maxima of the terrain.
        float offsetValue = Mth.lerp(heightOffsetBlend, heightOffsetValue, -fadeTarget);
        float offset      = offsetValue * result.magnitude;

        // Return only the height delta + offset (base height will be added separately)
        return result.heightDelta + offset;
    }

    @Override
    public double minValue() {
        return -erosionStrength * erosionScale * 2.0;
    }

    @Override
    public double maxValue() {
        return erosionStrength * erosionScale * 2.0;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }

    /**
     * Apply the erosion filter to compute height and slope modifications.
     */
    private ErosionResult applyErosion(
            float x, float z, float initialSlopeX,
            float initialSlopeZ,
            float fadeTarget) {

        float strength = erosionStrength * erosionScale;
        fadeTarget = Mth.clamp(fadeTarget, -1.0f, 1.0f);

        float freq         = 1.0f / (erosionScale * cellScale);
        float slopeLength  = (float) Math.max(Math.sqrt(initialSlopeX * initialSlopeX + initialSlopeZ * initialSlopeZ), 1e-10);
        float magnitude    = 0.0f;
        float roundingMult = 1.0f;

        float roundingForInput = Mth.lerp(Mth.clamp(fadeTarget + 0.5f, 0, 1),
                creaseRounding, ridgeRounding) * roundingMultInitial;

        // Combined accumulating mask based on initial slope
        float combiMask = easeOut(smoothStart(slopeLength * onsetInitial, roundingForInput * onsetInitial));

        // Initialize ridge map
        float ridgeMapCombiMask  = easeOut(slopeLength * ridgeMapOnsetInitial);
        float ridgeMapFadeTarget = fadeTarget;

        // Determine gully slope direction
        float gullyNormX  = initialSlopeX / slopeLength;
        float gullyNormZ  = initialSlopeZ / slopeLength;
        float gullySlopeX = Mth.lerp(assumedSlopeMix, initialSlopeX, gullyNormX * assumedSlope);
        float gullySlopeZ = Mth.lerp(assumedSlopeMix, initialSlopeZ, gullyNormZ * assumedSlope);

        float heightDelta = 0.0f;
        float slopeDeltaX = 0.0f;
        float slopeDeltaZ = 0.0f;

        // Apply octaves
        for (int i = 0; i < octaves; i++) {
            // Normalize gully direction
            float gullyLen = (float) Math.sqrt(gullySlopeX * gullySlopeX + gullySlopeZ * gullySlopeZ);
            if (gullyLen < 1e-10f) gullyLen = 1e-10f;
            float normDirX = gullySlopeX / gullyLen;
            float normDirZ = gullySlopeZ / gullyLen;

            // Calculate Phacelle noise
            PhacelleResult phacelle = phacelleNoise(
                    x * freq, z * freq,
                    normDirX, normDirZ,
                    cellScale,
                    0.25f,
                    normalization
            );

            // Multiply derivatives with freq (since p was multiplied)
            // Negate since we use downward-pointing slopes
            phacelle.derivX *= -freq;
            phacelle.derivZ *= -freq;

            float sloping = Math.abs(phacelle.sineWave);

            // Add normalized slope to gully slope for next octave
            gullySlopeX += Math.signum(phacelle.sineWave) * phacelle.derivX * strength * erosionGullyWeight;
            gullySlopeZ += Math.signum(phacelle.sineWave) * phacelle.derivZ * strength * erosionGullyWeight;

            // Create gullies vector (height offset and derivatives)
            float gullyHeight = phacelle.cosineWave * erosionGullyWeight;
            float gullyDerivX = phacelle.sineWave * phacelle.derivX * erosionGullyWeight;
            float gullyDerivZ = phacelle.sineWave * phacelle.derivZ * erosionGullyWeight;

            // Fade gullies based on mask
            float fadedHeight = Mth.lerp(combiMask, fadeTarget, gullyHeight);
            float fadedDerivX = gullyDerivX * combiMask;
            float fadedDerivZ = gullyDerivZ * combiMask;

            // Apply to terrain
            heightDelta += fadedHeight * strength;
            slopeDeltaX += fadedDerivX * strength;
            slopeDeltaZ += fadedDerivZ * strength;
            magnitude += strength;

            // Update fade target
            fadeTarget = fadedHeight;

            // Update mask for next octave
            float roundingForOctave = Mth.lerp(Mth.clamp(phacelle.cosineWave + 0.5f, 0, 1),
                    creaseRounding, ridgeRounding) * roundingMult;
            float newMask = easeOut(smoothStart(sloping * onsetPerOctave, roundingForOctave * onsetPerOctave));
            combiMask = powInv(combiMask, erosionDetail) * newMask;

            // Update ridge map
            ridgeMapFadeTarget = Mth.lerp(ridgeMapCombiMask, ridgeMapFadeTarget, gullyHeight);
            float newRidgeMapMask = easeOut(sloping * ridgeMapOnsetPerOctave);
            ridgeMapCombiMask = ridgeMapCombiMask * newRidgeMapMask;

            // Prepare next octave
            strength *= gain;
            freq *= lacunarity;
            roundingMult *= roundingMultPerOctave;
        }

        float ridgeMap = ridgeMapFadeTarget * (1.0f - ridgeMapCombiMask);

        return new ErosionResult(heightDelta, slopeDeltaX, slopeDeltaZ, magnitude, ridgeMap);
    }

    private static float easeOut(float t) {
        float v = 1.0f - clamp01(t);
        return 1.0f - v * v;
    }

    private static float smoothStart(float t, float smoothing) {
        if (t >= smoothing)
            return t - 0.5f * smoothing;
        return 0.5f * t * t / smoothing;
    }

    /**
     * The Simple Phacelle Noise function produces a stripe pattern aligned with the input vector.
     * The name Phacelle is a portmanteau of phase and cell, since the function produces a phase by
     * interpolating cosine and sine waves from multiple cells.
     *
     * @param px            X coordinate
     * @param pz            Z coordinate
     * @param normDirX      Normalized X direction of stripes
     * @param normDirZ      Normalized Z direction of stripes
     * @param freq          Frequency of stripes (keep close to 1.0)
     * @param offset        Phase offset (0-1 for full cycle)
     * @param normalization Degree of normalization (0-1)
     * @return PhacelleResult containing cosine wave, sine wave, and derivatives
     * /p
     * Phacelle Noise function copyright (c) 2025 Rune Skovbo Johansen
     * This Source Code Form is subject to the terms of the Mozilla Public
     * License, v. 2.0. If a copy of the MPL was not distributed with this
     * file, You can obtain one at <a href="https://mozilla.org/MPL/2.0/">https://mozilla.org/MPL/2.0/</a>.
     */
    private PhacelleResult phacelleNoise(
            float px, float pz,
            float normDirX, float normDirZ,
            float freq, float offset, float normalization) {

        // Get orthogonal vector scaled by frequency
        float sideDirX = -normDirZ * freq * TAU;
        float sideDirZ = normDirX * freq * TAU;
        offset *= TAU;

        // Get integer and fractional parts
        int   pIntX  = Mth.floor(px);
        int   pIntZ  = Mth.floor(pz);
        float pFracX = px - pIntX;
        float pFracZ = pz - pIntZ;

        float phaseDirX = 0.0f;
        float phaseDirY = 0.0f;
        float weightSum = 0.0f;

        // Iterate over 4x4 cells, calculating a stripe pattern for each and blending between them.
        // pInt is the integer part of the current coordinate p, pFrac is the remainder.
        //
        // o   o   o   o
        //
        // o   o   o   o
        //       p
        // o   i   o   o
        //
        // o   o   o   o
        //
        // p: current coordinate    i: integer part of p    o: grid points for 4x4 cells
        //
        for (int i = -1; i <= 2; i++) {
            for (int j = -1; j <= 2; j++) {
                // Calculate cell point
                int gridPointX = pIntX + i;
                int gridPointZ = pIntZ + j;

                // Random offset for this cell (-0.5 to 0.5 on each axis)
                float[] randomOffset = hash2D(gridPointX, gridPointZ);
                randomOffset[0] *= 0.5f;
                randomOffset[1] *= 0.5f;

                // Vector from cell point to current point
                float vecX = pFracX - i - randomOffset[0];
                float vecZ = pFracZ - j - randomOffset[1];

                // Bell-shaped weight function
                float sqrDist = vecX * vecX + vecZ * vecZ;
                float weight  = (float) Math.exp(-sqrDist * 2.0) - 0.01111f;
                weight = Math.max(0.0f, weight);

                weightSum += weight;

                // The waveInput is a gradient which increases in value along sideDir. Its rate of
                // change is the freq times tau, due to the multiplier pre-applied to sideDir.
                float waveInput = vecX * sideDirX + vecZ * sideDirZ + offset;

                // Add this cell's cosine and sine wave contributions to the interpolated value.
                phaseDirX += (float) Math.cos(waveInput) * weight;
                phaseDirY += (float) Math.sin(waveInput) * weight;
            }
        }

        // Get the raw interpolated value.
        float interpolatedX = phaseDirX / weightSum;
        float interpolatedY = phaseDirY / weightSum;

        // Interpret the value as a vector whose length represents the magnitude of both waves.
        float magnitude = (float) Math.sqrt(interpolatedX * interpolatedX + interpolatedY * interpolatedY);

        // Apply a lower threshold to show small magnitudes we're going to fully normalize.
        magnitude = Math.max(1.0f - normalization, magnitude);

        float cosineWave = interpolatedX / magnitude;
        float sineWave   = interpolatedY / magnitude;

        // Return a record containing the normalized cosine and sine waves, as well as the direction
        // vector, which can be multiplied onto the sine to get the derivatives of the cosine.
        return new PhacelleResult(cosineWave, sineWave, sideDirX, sideDirZ);
    }

    private static float powInv(float t, float power) {
        return 1.0f - (float) Math.pow(1.0f - clamp01(t), power);
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    /**
     * Hash function for 2D coordinates.
     * Generates pseudo-random values in range [-1, 1] for both x and y.
     */
    private float[] hash2D(int x, int z) {
        // Incorporate the world seed for variation
        long n = (x * 374761393L + z * 668265263L + seed * 1013904223L);
        n = (n ^ (n >> 13)) * 1274126177L;
        n = n ^ (n >> 16);

        float fx = ((n & 0xFFFF) / 32768.0f) - 1.0f;

        n = n * 1664525L + 1013904223L;
        float fz = ((n & 0xFFFF) / 32768.0f) - 1.0f;

        return new float[]{fx, fz};
    }

    @Override
    public void fillArray(double[] p_208241_, ContextProvider p_208242_) {
        SimpleFunction.super.fillArray(p_208241_, p_208242_);
    }

    // ========== Utility Functions ==========

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return visitor.apply(
                new PhacelleErosionNoise(heightMap.mapAll(visitor),
                        heightOffsetValue, heightOffsetBlend,
                        erosionScale, erosionStrength, erosionGullyWeight, erosionDetail,
                        ridgeRounding, creaseRounding,
                        roundingMultInitial, roundingMultPerOctave, onsetInitial, onsetPerOctave, ridgeMapOnsetInitial, ridgeMapOnsetPerOctave,
                        assumedSlope, assumedSlopeMix, octaves, lacunarity, gain, cellScale, normalization)
        );
    }

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    private record ErosionParams(float scale, float strength, float gullyWeight, float detail) {
        private static final Codec<ErosionParams> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.optionalFieldOf("scale", 32f).forGetter(ErosionParams::scale),
                        Codec.FLOAT.optionalFieldOf("strength", 0.22f).forGetter(ErosionParams::strength),
                        Codec.FLOAT.optionalFieldOf("gully_weight", 0.5f).forGetter(ErosionParams::gullyWeight),
                        Codec.FLOAT.optionalFieldOf("detail", 1.5f).forGetter(ErosionParams::detail)
                ).apply(instance, ErosionParams::new));
    }

    private record RoundingParams(float ridge, float crease, float multInitial, float multPerOctave) {
        private static final Codec<RoundingParams> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.optionalFieldOf("ridge", 0.1f).forGetter(RoundingParams::ridge),
                        Codec.FLOAT.optionalFieldOf("crease", 0.0f).forGetter(RoundingParams::crease),
                        Codec.FLOAT.optionalFieldOf("mult_initial", 0.1f).forGetter(RoundingParams::multInitial),
                        Codec.FLOAT.optionalFieldOf("mult_per_octave", 2.0f).forGetter(RoundingParams::multPerOctave)
                ).apply(instance, RoundingParams::new));
    }

    private record OnsetParams(float initial, float perOctave, float ridgeMapInitial, float ridgeMapPerOctave) {
        private static final Codec<OnsetParams> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.optionalFieldOf("initial", 1.25f).forGetter(OnsetParams::initial),
                        Codec.FLOAT.optionalFieldOf("per_octave", 1.25f).forGetter(OnsetParams::perOctave),
                        Codec.FLOAT.optionalFieldOf("ridge_map_initial", 2.8f).forGetter(OnsetParams::ridgeMapInitial),
                        Codec.FLOAT.optionalFieldOf("ridge_map_per_octave", 1.5f).forGetter(OnsetParams::ridgeMapPerOctave)
                ).apply(instance, OnsetParams::new));
    }

    private record SlopeParams(float assumed, float mix) {
        private static final Codec<SlopeParams> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.optionalFieldOf("assumed", 0.7f).forGetter(SlopeParams::assumed),
                        Codec.FLOAT.optionalFieldOf("mix", 1.0f).forGetter(SlopeParams::mix)
                ).apply(instance, SlopeParams::new));
    }

    private record FractalParams(int octaves, float lacunarity, float gain) {
        private static final Codec<FractalParams> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.optionalFieldOf("octaves", 5).forGetter(FractalParams::octaves),
                        Codec.FLOAT.optionalFieldOf("lacunarity", 2.0f).forGetter(FractalParams::lacunarity),
                        Codec.FLOAT.optionalFieldOf("gain", 0.5f).forGetter(FractalParams::gain)
                ).apply(instance, FractalParams::new));
    }

    private record NoiseParams(float cellScale, float normalization) {
        private static final Codec<NoiseParams> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.optionalFieldOf("cell_scale", 0.7f).forGetter(NoiseParams::cellScale),
                        Codec.FLOAT.optionalFieldOf("normalization", 0.5f).forGetter(NoiseParams::normalization)
                ).apply(instance, NoiseParams::new));
    }

    private record HeightOffsetParams(float value, float blend) {
        private static final Codec<HeightOffsetParams> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.optionalFieldOf("value", 0.0f).forGetter(HeightOffsetParams::value),
                        Codec.FLOAT.optionalFieldOf("blend", 0.0f).forGetter(HeightOffsetParams::blend)
                ).apply(instance, HeightOffsetParams::new));
    }
    // ========== Helper Classes ==========

    private static class PhacelleResult {
        final float cosineWave;
        final float sineWave;
        float derivX;
        float derivZ;

        PhacelleResult(float cosineWave, float sineWave, float derivX, float derivZ) {
            this.cosineWave = cosineWave;
            this.sineWave = sineWave;
            this.derivX = derivX;
            this.derivZ = derivZ;
        }
    }

    private record ErosionResult(float heightDelta, float slopeDeltaX, float slopeDeltaZ, float magnitude,
                                 float ridgeMap) {
    }
}