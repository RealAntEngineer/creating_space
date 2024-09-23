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
import net.minecraft.world.phys.AABB;

import java.util.List;

public class BurnEffect {

    private static final int MAX_BURN_TICKS = 1200;
    private static final int BURN_RADIUS = 5;
    private static final int PARTICLE_RADIUS = 5;

    public static void burnEntities(ServerLevel world, BlockPos pos) {
        AABB range = new AABB(pos).inflate(BURN_RADIUS);
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, range);

        for (LivingEntity entity : entities) {
            double distanceSqr = entity.blockPosition().distSqr(pos);
            if (distanceSqr <= BURN_RADIUS * BURN_RADIUS) {
                double intensity = 1.0 - (Math.sqrt(distanceSqr) / BURN_RADIUS);
                int freezeTicks = (int) (20 * (1 + 3 * intensity));

                if (entity.getTicksFrozen() < MAX_BURN_TICKS) {
                    entity.setSecondsOnFire(Math.min(entity.getTicksFrozen() + freezeTicks, MAX_BURN_TICKS) /20);
                }
            }
        }
    }

    public static void burnWaterAndSpawnParticles(ServerLevel world, BlockPos pos, RandomSource random, boolean... lavaAndParticleFlags) {
        boolean handleLava = lavaAndParticleFlags.length > 0 && lavaAndParticleFlags[0];
        boolean handleParticles = lavaAndParticleFlags.length > 1 && lavaAndParticleFlags[1];

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int dx = -PARTICLE_RADIUS; dx <= PARTICLE_RADIUS; dx++) {
            for (int dy = -PARTICLE_RADIUS; dy <= PARTICLE_RADIUS; dy++) {
                for (int dz = -PARTICLE_RADIUS; dz <= PARTICLE_RADIUS; dz++) {
                    int distanceSqr = dx * dx + dy * dy + dz * dz;
                    if (distanceSqr <= PARTICLE_RADIUS * PARTICLE_RADIUS) {
                        mutablePos.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                        BlockState blockState = world.getBlockState(mutablePos);

                        // Freeze water logic
                        if (blockState.is(Blocks.WATER) || blockState.is(Blocks.ICE) ||
                                blockState.is(Blocks.BLUE_ICE) || blockState.is(Blocks.PACKED_ICE) ||
                                blockState.is(Blocks.FROSTED_ICE) || blockState.is(Blocks.SNOW) ||
                                blockState.is(Blocks.SNOW_BLOCK) || blockState.is(Blocks.POWDER_SNOW)) {
                            world.setBlock(mutablePos, Blocks.AIR.defaultBlockState(), 3);
                            playBurnEffects(world, mutablePos, random);
                        }

                        // Convert grass to coarse dirt
                        if (blockState.is(Blocks.GRASS_BLOCK)) {
                            world.setBlock(mutablePos, Blocks.COARSE_DIRT.defaultBlockState(), 3);
                            playBurnEffects(world, mutablePos, random);
                        }

                        // Burn plants logic
                        if (isPlant(blockState.getBlock())) {
                            world.setBlock(mutablePos, Blocks.AIR.defaultBlockState(), 3);
                            playBurnEffects(world, mutablePos, random);
                        }

                        // Handle cobblestone conversion to lava
                        if (handleLava && blockState.is(BlockTags.BASE_STONE_OVERWORLD) || blockState.is(Blocks.COBBLESTONE)) {
                            world.setBlock(mutablePos, Blocks.LAVA.defaultBlockState(), 3);
                            playBurnEffects(world, mutablePos, random);
                        }

                        // Spawn particles if enabled
                        if (handleParticles) {
                            spawnParticlesIfGroundLevel(world, random, mutablePos);
                        }
                    }
                }
            }
        }
    }

    private static boolean isPlant(Block block) {
        return block.defaultBlockState().is(BlockTags.FLOWERS) || block.defaultBlockState().is(BlockTags.SAPLINGS) || block.defaultBlockState().is(BlockTags.LEAVES) || block.defaultBlockState().is(BlockTags.LOGS_THAT_BURN) ||
                block.defaultBlockState().is(BlockTags.PLANKS) || block.defaultBlockState().is(BlockTags.SIGNS) ||
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

    private static void playBurnEffects(ServerLevel world, BlockPos pos, RandomSource random) {
        // Play sound effect
        world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);

        // Spawn burn particles
        world.sendParticles(ParticleTypes.SMOKE,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                10, 0.3, 0.3, 0.3, 0.01);
    }

    public static void spawnParticlesIfGroundLevel(ServerLevel world, RandomSource random, BlockPos.MutableBlockPos mutablePos) {
        BlockPos belowPos = mutablePos.below();
        if (world.getBlockState(belowPos).isSolidRender(world, belowPos) && random.nextFloat() < 0.05) {
            world.sendParticles(ParticleTypes.SMOKE,
                    mutablePos.getX() + 0.5, mutablePos.getY() + 0.1, mutablePos.getZ() + 0.5,
                    1, 0.0, 0.02, 0.0, 0.01);
        }
    }
}
