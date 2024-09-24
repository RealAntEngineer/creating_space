package com.rae.creatingspace.content.rocket.flight_recorder;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class FlightRecorderBlock extends DirectionalAxisKineticBlock implements IBE<FlightRecorderBlockEntity> {
    public FlightRecorderBlock(Properties properties) {
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
    public boolean useShapeForLightOcclusion(BlockState p_60576_) {
        return true;
    }


    @Override
    public Class<FlightRecorderBlockEntity> getBlockEntityClass() {
        return FlightRecorderBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FlightRecorderBlockEntity> getBlockEntityType() {
        return BlockEntityInit.FLIGHT_RECORDER.get();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0,pos,$1,blockEntity) -> {
            if(blockEntity instanceof FlightRecorderBlockEntity flightRecorderBlockEntity) {
                flightRecorderBlockEntity.tick(level,pos,state, flightRecorderBlockEntity);
            }
        };
    }

}
