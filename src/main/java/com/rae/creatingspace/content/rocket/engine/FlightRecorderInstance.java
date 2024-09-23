package com.rae.creatingspace.content.rocket.engine;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.rae.creatingspace.legacy.server.blockentities.FlightRecorderBlockEntity;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.Direction;

public class FlightRecorderInstance extends SingleRotatingInstance<FlightRecorderBlockEntity> {
    public FlightRecorderInstance(MaterialManager materialManager, FlightRecorderBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    protected Instancer<RotatingData> getModel() {
        Direction facing = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);

        return getRotatingMaterial().getModel(PartialModelInit.MEMORY_ROLL, blockState,facing);
    }

    @Override
    protected Direction.Axis getRotationAxis() {
        return super.getRotationAxis();
    }
}
