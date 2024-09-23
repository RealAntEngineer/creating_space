package com.rae.creatingspace.content.fluids.effect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FreezeEffect {

    public static void freezeEntities(ServerLevel world, BlockPos pos) {
        AABB range = new AABB(pos).inflate(5);
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, range);

        for (LivingEntity entity : entities) {
            double distanceSqr = entity.blockPosition().distSqr(pos);
            if (distanceSqr <= 25.0) { // 5.0 * 5.0
                double intensity = 1.0 - (Math.sqrt(distanceSqr) / 5.0);
                int freezeTicks = (int) (20 * (1 + 3 * intensity));
                if (entity.getTicksFrozen() < 1200) {
                    entity.setTicksFrozen(entity.getTicksFrozen() + freezeTicks);
                }
            }
        }
    }

    public static void freezeWaterAndSpawnParticles(ServerLevel world, BlockPos pos, RandomSource random, boolean... lavaAndParticleFlags) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        boolean handleLava = lavaAndParticleFlags.length > 0 && lavaAndParticleFlags[0];
        boolean handleParticles = lavaAndParticleFlags.length > 1 && lavaAndParticleFlags[1];

        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                for (int dz = -5; dz <= 5; dz++) {
                    int distanceSqr = dx * dx + dy * dy + dz * dz;
                    if (distanceSqr <= 25) { // 5 * 5, ensures spherical range
                        mutablePos.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                        BlockState blockState = world.getBlockState(mutablePos);

                        // Freeze water logic
                        if (blockState.is(Blocks.WATER)) {
                            if (blockState.getValue(BlockStateProperties.LEVEL) == 0) {
                                int distanceCategory = (int) Math.ceil(Math.sqrt(distanceSqr));

                                switch (distanceCategory) {
                                    case 1 -> transformBlock(world, mutablePos, Blocks.BLUE_ICE.defaultBlockState());
                                    case 2 -> transformBlock(world, mutablePos, Blocks.PACKED_ICE.defaultBlockState());
                                    case 3 -> transformBlock(world, mutablePos, Blocks.ICE.defaultBlockState());
                                    case 4 -> transformBlock(world, mutablePos, Blocks.POWDER_SNOW.defaultBlockState());
                                    case 5 -> transformBlock(world, mutablePos, Blocks.SNOW_BLOCK.defaultBlockState());
                                }
                            } else {
                                transformBlock(world, mutablePos, Blocks.SNOW_BLOCK.defaultBlockState());
                            }
                        }

                        // Handle lava conversion to cobblestone
                        if (handleLava && blockState.is(Blocks.LAVA)) {
                            transformBlock(world, mutablePos, Blocks.COBBLESTONE.defaultBlockState());
                        }

                        // Freeze plants
                        freezePlant(world, mutablePos, random);

                        // Spawn particles in the spherical ice zone
                        if (handleParticles) {
                            spawnParticlesIfGroundLevel(world, random, mutablePos);
                        }
                    }
                }
            }
        }
    }

    private static boolean isPlant(Block block) {
        return block.defaultBlockState().is(BlockTags.FLOWERS) || block.defaultBlockState().is(BlockTags.SAPLINGS) || block.defaultBlockState().is(BlockTags.LEAVES) ||
                block == Blocks.GRASS || block == Blocks.FERN ||
                block == Blocks.DEAD_BUSH || block == Blocks.VINE ||
                block == Blocks.TALL_GRASS || block == Blocks.LARGE_FERN ||
                block == Blocks.SEAGRASS || block == Blocks.TALL_SEAGRASS ||
                block == Blocks.SUGAR_CANE || block == Blocks.CACTUS ||
                block == Blocks.MOSS_BLOCK || block == Blocks.FLOWERING_AZALEA ||
                block == Blocks.FLOWERING_AZALEA_LEAVES || block == Blocks.FLOWER_POT ||
                block == Blocks.AZALEA || block == Blocks.SWEET_BERRY_BUSH ||
                block == Blocks.LILY_PAD;
    }

    private static void freezePlant(ServerLevel world, BlockPos pos, RandomSource random) {
        BlockState currentState = world.getBlockState(pos);

        // Check if the block is a plant
        if (isPlant(currentState.getBlock())) {
            // Replace the plant with ice or another block to simulate freezing
            transformBlock(world, pos, Blocks.AIR.defaultBlockState());

            // Play freezing effects
            playFreezeEffects(world, pos, random);
        }
    }

    private static void transformBlock(ServerLevel world, BlockPos pos, BlockState newState) {
        world.setBlock(pos, newState, 3);
        playTransformSound(world, pos);
    }

    private static void playTransformSound(ServerLevel world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static void playFreezeEffects(ServerLevel world, BlockPos pos, RandomSource random) {
        // Spawn freezing particles
        world.sendParticles(ParticleTypes.SNOWFLAKE,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                10, 0.3, 0.3, 0.3, 0.01);
    }

    public static void spawnParticlesIfGroundLevel(ServerLevel world, RandomSource random, BlockPos.MutableBlockPos mutablePos) {
        BlockPos belowPos = mutablePos.below();
        if (world.getBlockState(belowPos).isSolidRender(world, belowPos) && random.nextFloat() < 0.05) {
            world.sendParticles(ParticleTypes.SNOWFLAKE,
                    mutablePos.getX() + 0.5, mutablePos.getY() + 0.1, mutablePos.getZ() + 0.5,
                    1, 0.0, 0.02, 0.0, 0.01);
        }
    }
}
