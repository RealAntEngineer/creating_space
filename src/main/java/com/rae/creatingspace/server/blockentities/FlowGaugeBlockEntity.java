package com.rae.creatingspace.server.blockentities;

import com.rae.creatingspace.server.blocks.FlowGaugeBlock;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.kinetics.gauge.GaugeBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static java.lang.Math.min;

public class FlowGaugeBlockEntity extends GaugeBlockEntity {
    public FlowGaugeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    float flow = 0;

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new FlowMeterFluidTransportBehaviour(this));
        registerAwardables(behaviours, FluidPropagator.getSharedTriggers());
    }


    public void tick(Level level, BlockPos pos, BlockState state, FlowGaugeBlockEntity flowGaugeBlockEntity) {

        FluidTransportBehaviour behavior = flowGaugeBlockEntity
                .getBehaviour(FlowMeterFluidTransportBehaviour.TYPE);

        Couple<Float> pressure =  behavior
                    .getConnection(state.getValue(FlowGaugeBlock.FACING).getClockWise())
                    .getPressure();

        flowGaugeBlockEntity.flow =  Math.max(
                    pressure.getFirst(),pressure.getSecond());
        flowGaugeBlockEntity.dialTarget = min(flowGaugeBlockEntity.flow/1000,1);

        flowGaugeBlockEntity.setChanged();
        super.tick();

    }



    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(componentSpacing.plainCopy().append(Lang.translateDirect("gui.gauge.info_header")));
        tooltip.add(Component.literal(this.flow * 0.5 + " mb/t")); // 1 mb/s *rpm for the real tick -> in game tick = real *2

        return true;
    }

    public static class FlowMeterFluidTransportBehaviour extends FluidTransportBehaviour{

        public FlowMeterFluidTransportBehaviour(SmartBlockEntity be) {
            super(be);
        }

        @Override
        public boolean canHaveFlowToward(BlockState state, Direction direction) {
            return direction.getAxis() == state.getValue(FlowGaugeBlock.FACING).getClockWise().getAxis();
        }


    }
}
