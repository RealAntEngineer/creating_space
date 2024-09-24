package com.rae.creatingspace.content.rocket.engine;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class RocketEngineBlock extends HorizontalDirectionalBlock {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        //builder.add(ACTIVE);
        super.createBlockStateDefinition(builder.add(ACTIVE).add(FACING));
    }
    public RocketEngineBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(ACTIVE, Boolean.valueOf(true))
                .setValue(FACING,Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(ACTIVE, Boolean.valueOf(true))
                .setValue(FACING,context.getHorizontalDirection().getOpposite());
    }

    public abstract Vec3i getOffset(Direction facing);

    public abstract Vec3i getSize(Direction facing);
}
