package com.rae.creatingspace.legacy.server.blocks.multiblock.engines;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
                .setValue(ACTIVE, Boolean.TRUE)
                .setValue(FACING,Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context))
                .setValue(ACTIVE, Boolean.TRUE)
                .setValue(FACING,context.getHorizontalDirection().getOpposite());
    }

    public abstract Vec3i getOffset(Direction facing);

    public abstract Vec3i getSize(Direction facing);
}
