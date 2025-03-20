package com.rae.creatingspace.legacy.server.blocks.multiblock.engines;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.content.rocket.engine.RocketEngineBlockEntity.SmallEngine;
import com.rae.creatingspace.legacy.server.blocks.multiblock.SmallRocketStructuralBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


public class SmallEngineBlock extends RocketEngineBlock implements IBE<SmallEngine> {


	public SmallEngineBlock(Properties properties) {
		super(properties);
	}

	@Override
	public Vec3i getOffset(Direction facing) {
		return switch (facing){
			case DOWN -> new Vec3i(0,0,0);
			default -> new Vec3i(0,1,0);
	};
	}


	@Override
	public Vec3i getSize(Direction facing) {
		return new Vec3i(1, 2, 1);
	}

	@Override
	public Class<SmallEngine> getBlockEntityClass() {
		return SmallEngine.class;
	}

	@Override
	public BlockEntityType<? extends SmallEngine> getBlockEntityType() {
		return BlockEntityInit.SMALL_ENGINE.get();
	}


	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		Direction targetSide = Direction.DOWN;
		BlockPos structurePos = pPos.relative(targetSide);
		BlockState occupiedState = pLevel.getBlockState(structurePos);
		BlockState requiredStructure = BlockInit.SMALL_ENGINE_STRUCTURAL.getDefaultState()
				.setValue(SmallRocketStructuralBlock.FACING, targetSide.getOpposite());
		pLevel.setBlockAndUpdate(structurePos, requiredStructure);

		//make the same for big engine block

	}

	@Override
	public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState1, boolean isMoving) {
		super.onRemove(blockState, level, blockPos, blockState1, isMoving);
	}
}
