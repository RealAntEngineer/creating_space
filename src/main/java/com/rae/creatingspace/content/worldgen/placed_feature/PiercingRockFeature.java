package com.rae.creatingspace.content.worldgen.placed_feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.Vec3;

public class PiercingRockFeature extends Feature<PiercingRockFeature.Configuration> {

    public PiercingRockFeature(Codec<Configuration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Configuration> context) {
        BlockPos      origin = context.origin();
        WorldGenLevel level  = context.level();
        RandomSource  random = context.random();
        Configuration config = context.config();

        // Apply vertical offset
        int      verticalOffset = config.verticalOffset.sample(random);
        BlockPos adjustedOrigin = origin.offset(0, verticalOffset, 0);

        // Generate spike parameters
        int totalLength = config.spikeLength.sample(random);
        int baseRadius  = config.baseRadius.sample(random);

        // Pick a random direction with configurable angle from vertical
        Vec3 direction = getRandomDirection(random, config.maxAngleFromVertical);

        boolean placedAny = false;

        // Place blocks along the spike direction. half step for aliasing issue
        for (float step = 0; step < totalLength; step += 0.5f) {
            // Calculate position along the spike
            Vec3 offset = direction.scale(step);
            BlockPos currentPos = adjustedOrigin.offset(
                    (int) Math.round(offset.x),
                    (int) Math.round(offset.y),
                    (int) Math.round(offset.z)
            );

            // Calculate width at this point using the width function
            float progress      = (float) step / (float) totalLength;
            int   currentRadius = calculateRadius(baseRadius, progress, config);

            // Place blocks in a circle perpendicular to the spike direction
            placedAny |= placeCircularSection(level, random, config, currentPos, direction, currentRadius, adjustedOrigin);
        }

        return placedAny;
    }

    /**
     * Generate a random direction vector with a maximum angle from vertical
     */
    private Vec3 getRandomDirection(RandomSource random, float maxAngleFromVertical) {
        // Convert max angle from degrees to radians
        float maxAngleRad = maxAngleFromVertical * Mth.DEG_TO_RAD;

        // Random angle from vertical (0 = straight down, maxAngle = most horizontal)
        float angleFromVertical = random.nextFloat() * maxAngleRad;

        // Random azimuthal angle (direction around the vertical axis)
        float azimuth = random.nextFloat() * Mth.TWO_PI;

        // Convert spherical coordinates to Cartesian
        float x = Mth.sin(angleFromVertical) * Mth.cos(azimuth);
        float y = Mth.cos(angleFromVertical); // Negative for downward
        float z = Mth.sin(angleFromVertical) * Mth.sin(azimuth);

        return new Vec3(x, y, z).normalize();
    }

    /**
     * Calculate the radius at a given progress point along the spike
     *
     * @param baseRadius The maximum radius
     * @param progress   0.0 at base, 1.0 at tip
     * @param config     Configuration
     * @return Current radius at this point
     */
    private int calculateRadius(int baseRadius, float progress, Configuration config) {
        float widthMultiplier = config.widthFunction.apply(progress, config.taperStrength);
        return Math.max(0, (int) (baseRadius * widthMultiplier));
    }

    /**
     * Place a circular cross-section perpendicular to the spike direction
     */
    private boolean placeCircularSection(WorldGenLevel level, RandomSource random, Configuration config,
                                         BlockPos center, Vec3 spikeDirection, int radius, BlockPos surfacePos) {
        if (radius <= 0) return false;

        boolean placedAny = false;

        // Get two perpendicular vectors to the spike direction
        Vec3 perpendicular1 = getPerpendicularVector(spikeDirection);
        Vec3 perpendicular2 = spikeDirection.cross(perpendicular1).normalize();

        // Place blocks in a circle
        for (int u = -radius; u <= radius; u++) {
            for (int v = -radius; v <= radius; v++) {
                float distance = (float) Math.sqrt(u * u + v * v);

                // Add some randomness to edges
                float maxDistance = radius + random.nextFloat() * 0.5f;

                if (distance <= maxDistance) {
                    // Calculate position using the perpendicular vectors
                    Vec3 offset = perpendicular1.scale(u).add(perpendicular2.scale(v));
                    BlockPos placePos = center.offset(
                            (int) Math.round(offset.x),
                            (int) Math.round(offset.y),
                            (int) Math.round(offset.z)
                    );

                    if (canPlace(level, placePos, surfacePos)) {
                        BlockState state = config.stateProvider.getState(random, placePos);
                        level.setBlock(placePos, state, 2);
                        placedAny = true;
                    }
                }
            }
        }

        return placedAny;
    }

    /**
     * Get a vector perpendicular to the input vector
     */
    private Vec3 getPerpendicularVector(Vec3 v) {
        // Use cross product with a non-parallel vector
        Vec3 arbitrary = Math.abs(v.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        return v.cross(arbitrary).normalize();
    }

    /**
     * Check if a block can be placed at this position
     * Below surface: only place if the existing block is solid
     * Above surface: can place anywhere
     */
    private boolean canPlace(WorldGenLevel level, BlockPos pos, BlockPos surfacePos) {
        // If below surface, check if the block being replaced is solid
        if (pos.getY() < surfacePos.getY()) {
            BlockState existingState = level.getBlockState(pos);
            // Only replace solid blocks below the surface
            return existingState.isSolid();
        } else {
            // Above surface, can place anywhere (replaces air, plants, etc.)
            return true;
        }
    }

    public enum WidthFunction {
        LINEAR_TAPER {
            @Override
            public float apply(float progress, float taperStrength) {
                // Always 1.0 at base, tapers toward tip
                return 1.0f - progress * taperStrength;
            }
        },

        EXPONENTIAL_TAPER {
            @Override
            public float apply(float progress, float taperStrength) {
                // Normalized so value is exactly 1.0 at progress = 0
                return 1.0f - taperStrength * (1.0f - (float) Math.pow(1.0f - progress, 2.0));
            }
        },

        DIAMOND {
            @Override
            public float apply(float progress, float taperStrength) {
                // Peak at center, but clamp so base is still full width
                return Math.max(1.0f - taperStrength * Math.abs(progress - 0.5f) * 2.0f, 0f);
            }
        },

        CIRCULAR {
            @Override
            public float apply(float progress, float taperStrength) {
                // Circular/spherical cross-section - creates rounded, organic shapes
                // Uses circle equation: sqrt(1 - x²) for smooth curve
                // Adjusted so base (progress=0) is always full width
                float adjustedProgress = progress * taperStrength;
                return (float) Math.sqrt(1.0f - adjustedProgress * adjustedProgress);
            }
        },

        CYLINDER {
            @Override
            public float apply(float progress, float taperStrength) {
                // No taper, constant width
                return 1.0f;
            }
        };

        /**
         * Calculate the width multiplier at a given progress point
         *
         * @param progress      0.0 at base, 1.0 at tip
         * @param taperStrength How aggressive the taper is (0.0 = no taper, 1.0 = full taper)
         * @return Width multiplier (typically 0.0 to 1.0)
         */
        public abstract float apply(float progress, float taperStrength);
    }

    /**
     * @param spikeLength          Total length of the spike
     * @param baseRadius           Radius at the base
     * @param maxAngleFromVertical Max degrees from vertical (0-90)
     * @param widthFunction        How width changes along length
     * @param taperStrength        0.0 = no taper, 1.0 = full taper
     * @param verticalOffset       Vertical offset from origin (can be negative to start below surface)
     */
    public record Configuration(BlockStateProvider stateProvider, IntProvider spikeLength, IntProvider baseRadius,
                                float maxAngleFromVertical, WidthFunction widthFunction,
                                float taperStrength, IntProvider verticalOffset) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(config -> config.stateProvider),
                        IntProvider.POSITIVE_CODEC.fieldOf("spike_length").forGetter(config -> config.spikeLength),
                        IntProvider.POSITIVE_CODEC.fieldOf("base_radius").forGetter(config -> config.baseRadius),
                        Codec.FLOAT.fieldOf("max_angle_from_vertical").forGetter(config -> config.maxAngleFromVertical),
                        Codec.STRING.xmap(
                                s -> WidthFunction.valueOf(s.toUpperCase()),
                                Enum::name
                        ).fieldOf("width_function").forGetter(config -> config.widthFunction),
                        Codec.FLOAT.fieldOf("taper_strength").forGetter(config -> config.taperStrength),
                        IntProvider.codec(-16, 16).fieldOf("vertical_offset").forGetter(config -> config.verticalOffset)
                ).apply(instance, Configuration::new)
        );

    }
}