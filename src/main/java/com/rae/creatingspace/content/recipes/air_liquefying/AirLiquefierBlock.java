package com.rae.creatingspace.content.recipes.air_liquefying;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static com.rae.creatingspace.init.graphics.ShapesInit.AIR_LIQUEFIER;

public class AirLiquefierBlock extends DirectionalAxisKineticBlock implements IBE<AirLiquefierBlockEntity> {
    public AirLiquefierBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(AXIS_ALONG_FIRST_COORDINATE,false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS_ALONG_FIRST_COORDINATE).add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return AIR_LIQUEFIER.get(state.getValue(FACING));
    }

    @Override
    public Class<AirLiquefierBlockEntity> getBlockEntityClass() {
        return AirLiquefierBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends AirLiquefierBlockEntity> getBlockEntityType() {
        return BlockEntityInit.AIR_LIQUEFIER.get();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0,pos,$1,blockEntity) -> {
            if(blockEntity instanceof AirLiquefierBlockEntity airLiquefierBlockEntity) {
                airLiquefierBlockEntity.tick(level,pos,state, airLiquefierBlockEntity);
            }
        };
    }

}
