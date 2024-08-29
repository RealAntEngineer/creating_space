package com.rae.creatingspace.server.blocks;

import com.rae.creatingspace.server.effect.BurnEffect;
import com.rae.creatingspace.server.effect.FreezeEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class FreezerBlock extends Block {

    public FreezerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(state, world, pos, random);

        FreezeEffect.freezeEntities(world, pos);
        FreezeEffect.freezeWaterAndSpawnParticles(world, pos, world.random, true, true);

        world.scheduleTick(pos, this, 1);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        if (!world.isClientSide()) {
            ((ServerLevel) world).scheduleTick(pos, this, 20);
        }
    }
}
