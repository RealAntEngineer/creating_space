package com.rae.creatingspace.content.worldgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.lwjgl.system.NonnullDefault;


// Phacelle Noise function copyright (c) 2025 Rune Skovbo Johansen
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at https://mozilla.org/MPL/2.0/.
@NonnullDefault
public class PhacelleErosionNoise implements DensityFunction.SimpleFunction, INeedWorldSeed {

    private static final MapCodec<PhacelleErosionNoise> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(
                    DensityFunction.HOLDER_HELPER_CODEC.fieldOf("height_map").forGetter(i -> i.heightMap),
                    ErosionParams.CODEC.optionalFieldOf("erosion", new ErosionParams(0.65f, 0.22f, 0.5f, 1.5f)).forGetter(i ->
                            new ErosionParams(i.erosionScale, i.erosionStrength, i.erosionGullyWeight, i.erosionDetail)),
                    RoundingParams.CODEC.optionalFieldOf("rounding", new RoundingParams(0.1f, 0.0f, 0.1f, 2.0f)).forGetter(i ->
                            new RoundingParams(i.ridgeRounding, i.creaseRounding, i.roundingMultInitial, i.roundingMultPerOctave)),
                    OnsetParams.CODEC.optionalFieldOf("onset", new OnsetParams(0.7f, 1.25f, 2.8f, 1.5f)).forGetter(i ->
                            new OnsetParams(i.onsetInitial, i.onsetPerOctave, i.ridgeMapOnsetInitial, i.ridgeMapOnsetPerOctave)),
                    SlopeParams.CODEC.optionalFieldOf("slope", new SlopeParams(0.7f, 1.0f)).forGetter(i ->
                            new SlopeParams(i.assumedSlope, i.assumedSlopeMix)),
                    FractalParams.CODEC.optionalFieldOf("fractal", new FractalParams(5, 2.0f, 0.5f)).forGetter(i ->
                            new FractalParams(i.octaves, i.lacunarity, i.gain)),
                    NoiseParams.CODEC.optionalFieldOf("noise", new NoiseParams(0.7f, 0.5f)).forGetter(i ->
                            new NoiseParams(i.cellScale, i.normalization))
            ).apply(instance, (heightMap, erosion, rounding, onset, slope, fractal, noise) ->
                    new PhacelleErosionNoise(heightMap,
                            erosion.scale, erosion.strength, erosion.gullyWeight, erosion.detail,
                            rounding.ridge, rounding.crease, rounding.multInitial, rounding.multPerOctave,
                            onset.initial, onset.perOctave, onset.ridgeMapInitial, onset.ridgeMapPerOctave,
                            slope.assumed, slope.mix,
                            fractal.octaves, fractal.lacunarity, fractal.gain,
                            noise.cellScale, noise.normalization)));

    public static final KeyDispatchDataCodec<PhacelleErosionNoise> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

    private static final float           TAU = (float) (2.0 * Math.PI);
    private final        DensityFunction heightMap;
    // Erosion parameters
    private final        float           erosionScale;
    private final        float           erosionStrength;
    private final        float           erosionGullyWeight;
    private final        float           erosionDetail;
    private final        float           ridgeRounding;
    private final        float           creaseRounding;
    private final        float           roundingMultInitial;
    private final        float           roundingMultPerOctave;
    private final        float           onsetInitial;
    private final        float           onsetPerOctave;
    private final        float           ridgeMapOnsetInitial;
    private final        float           ridgeMapOnsetPerOctave;
    private final        float           assumedSlope;
    private final        float           assumedSlopeMix;
    private final        int             octaves;
    private final        float           lacunarity;
    private final        float           gain;
    private final        float           cellScale;
    private final        float           normalization;
    //the seed
    private              long            seed;

    /**
     * Creates an erosion noise function with custom parameters.
     */
    public PhacelleErosionNoise(
            DensityFunction heightMap,
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
        // Sample the base heightmap and compute approximate derivatives
        double height = heightMap.compute(context);

        int x = context.blockX(), y = context.blockY(), z = context.blockZ();
        //TODO, introduce a differentiable DensityFunction

        // Compute slope using finite differences (sampling adjacent blocks)
        double heightXPlus =
                (
                        heightMap.compute(new SinglePointContext(x + 1, y, z)) +
                        heightMap.compute(new SinglePointContext(x - 1, y, z))
                )/2;
        double heightZPlus =
                (
                        heightMap.compute(new SinglePointContext(x, y, z + 1)) +
                        heightMap.compute(new SinglePointContext(x, y, z - 1))
                )/2;

        float slopeX = (float) (heightXPlus - height);
        float slopeZ = (float) (heightZPlus - height);

        // Define fade target based on altitude
        // you need to look into the height_map min and max to scale it.
        // parameters to offset it ???

        //fade target is 1 if it's a pic and -1 if it's a crease.
        float fadeTarget = Mth.clamp((float) height, -1.0f, 1.0f);

        // Apply erosion
        ErosionResult result = applyErosion(
                x, z,
                slopeX,
                slopeZ,
                fadeTarget
        );

        return result.heightDelta; //there is a mix to do
    }

    @Override
    public double minValue() {
        return - erosionStrength * erosionScale * 2.0;
    }

    @Override
    public double maxValue() {
        return  erosionStrength * erosionScale * 2.0;
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
    private static float[] hash2D(int x, int z) {
        // Simple hash based on sine functions (similar to common shader hashes)
        int n = x * 374761393 + z * 668265263;
        n = (n ^ (n >> 13)) * 1274126177;
        n = n ^ (n >> 16);

        float fx = ((n & 0xFFFF) / 32768.0f) - 1.0f;

        n = n * 1664525 + 1013904223;
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
                        Codec.FLOAT.optionalFieldOf("scale", 0.15f).forGetter(ErosionParams::scale),
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
                        Codec.FLOAT.optionalFieldOf("initial", 0.7f).forGetter(OnsetParams::initial),
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