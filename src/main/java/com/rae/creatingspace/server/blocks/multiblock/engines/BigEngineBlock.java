package com.rae.creatingspace.server.blocks.multiblock.engines;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.server.blockentities.RocketEngineBlockEntity.BigEngine;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public class BigEngineBlock extends RocketEngineBlock implements IBE<BigEngine> {
	public BigEngineBlock(Properties pr) {
		super(pr);
		this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, true));
	}

	@Override
	public Vec3i getOffset(Direction facing) {
		return new Vec3i(0,0,0);
	}

	@Override
	public Vec3i getSize(Direction facing) {
		return new Vec3i(3,3,3);
	}

	@Override
	public Class<BigEngine> getBlockEntityClass() {
		return BigEngine.class;
	}

	@Override
	public BlockEntityType<? extends BigEngine> getBlockEntityType() {
		return BlockEntityInit.BIG_ENGINE.get();
	}


	@Override
	public void tick(BlockState p_222945_, ServerLevel p_222946_, BlockPos p_222947_, RandomSource p_222948_) {

	}
}
