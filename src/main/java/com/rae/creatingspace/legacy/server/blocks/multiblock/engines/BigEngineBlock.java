package com.rae.creatingspace.legacy.server.blocks.multiblock.engines;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.content.rocket.engine.RocketEngineBlockEntity.BigEngine;
import com.rae.creatingspace.legacy.server.blocks.multiblock.BigRocketStructuralBlock;
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
	public void tick(BlockState state, ServerLevel level, BlockPos blockPos, RandomSource source) {
		verifyOrPlace(state,level,blockPos);
		}

		private void verifyOrPlace(BlockState state, ServerLevel level, BlockPos blockPos) {
			Vec3i size = getSize(state.getValue(FACING));
			for(int x = -1; x<size.getX()- 1; x++) {
				for (int y = -1; y < size.getY() - 1; y++) {
					for (int z = -1; z < size.getZ() - 1; z++) {
						if (!(x == 0 && y == 0 && z == 0)) {
							BlockPos pos = blockPos.offset(x, y, z);
							Direction ghostFacing;
							if (x == 0 && z == 0) {
								if (y == -1) {
									ghostFacing = Direction.UP;
								} else {
									ghostFacing = Direction.DOWN;
								}
							}
							else if (x<0){
								ghostFacing = Direction.EAST;
							}
							else if (x>0){
								ghostFacing = Direction.WEST;
							}
							else if (z>0){
								ghostFacing = Direction.NORTH;

							}
							else {
								ghostFacing = Direction.SOUTH;
							}

							BlockState ghostState = BlockInit.BIG_ENGINE_STRUCTURAL.getDefaultState()
									.setValue(BigRocketStructuralBlock.FACING, ghostFacing);

							if (isRightState(ghostState,level.getBlockState(pos)))
							level.setBlock(pos,ghostState,11);
						}
					}
				}
		}
	}

	private boolean isRightState(BlockState ghostState, BlockState blockState) {
		if(blockState.is(ghostState.getBlock()))
			return blockState.getValue(BigRocketStructuralBlock.FACING) ==
					ghostState.getValue(BigRocketStructuralBlock.FACING);
		return false;
	}
}
