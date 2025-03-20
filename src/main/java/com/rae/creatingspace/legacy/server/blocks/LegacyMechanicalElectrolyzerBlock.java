package com.rae.creatingspace.legacy.server.blocks;


import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.legacy.server.blockentities.LegacyMechanicalElectrolyzerBlockEntity;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class LegacyMechanicalElectrolyzerBlock extends DirectionalKineticBlock implements IBE<LegacyMechanicalElectrolyzerBlockEntity> {
    public LegacyMechanicalElectrolyzerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)//only for the Axis
                .setValue(H_FACING, Direction.NORTH));
    }

    //blockstate

    public static final DirectionProperty H_FACING = DirectionProperty.create("hfacing", Direction.Plane.HORIZONTAL);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(H_FACING);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(H_FACING, context.getHorizontalDirection().getOpposite());
    }


    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == Direction.Axis.Y;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public Class<LegacyMechanicalElectrolyzerBlockEntity> getBlockEntityClass() {
        return LegacyMechanicalElectrolyzerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends LegacyMechanicalElectrolyzerBlockEntity> getBlockEntityType() {
        return BlockEntityInit.LEGACY_ELECTROLIZER.get();
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityInit.ELECTROLIZER.get().create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0, pos, $1, blockEntity) -> {
            if (blockEntity instanceof LegacyMechanicalElectrolyzerBlockEntity electrolyzerBlockEntity) {
                electrolyzerBlockEntity.tick(level, pos, state, (LegacyMechanicalElectrolyzerBlockEntity) blockEntity);
            }
        };
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState p_60576_) {
        return true;
    }
}
