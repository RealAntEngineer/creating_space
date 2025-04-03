package com.rae.colony_api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class MBController extends DirectionalBlock {
    protected final MBShape shape;
    final DirectionalBlock structure;
    protected MBController(Properties properties, DirectionalBlock structure) {
        super(properties);
        this.structure = structure;
        this.shape = makeShapes(structure);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }
    @Override
    public void setPlacedBy(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, entity, stack);
        shape.repairStructure(worldIn, pos, state.getValue(FACING));
    }
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(FACING,context.getClickedFace());

    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }
    protected abstract MBShape makeShapes(DirectionalBlock structure);

    public DirectionalBlock getStructure() {
        return structure;
    }
    public Vec3i getPlaceOffset(Direction facing) {
        return shape.getOffset(facing,true);
    }
    public Vec3i getSize(Direction facing){
        return shape.getSize(facing);
    }
}