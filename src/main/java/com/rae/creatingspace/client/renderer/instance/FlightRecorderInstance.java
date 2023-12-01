package com.rae.creatingspace.client.renderer.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.rae.creatingspace.server.blockentities.FlightRecorderBlockEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LightLayer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class FlightRecorderInstance extends KineticBlockEntityInstance<FlightRecorderBlockEntity> {
    protected final RotatingData mainShaft;
    protected final RotatingData memoryRoll;
    final Direction direction;
    public FlightRecorderInstance(MaterialManager materialManager, FlightRecorderBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        direction = blockState.getValue(FACING);

        int blockLight = world.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = world.getBrightness(LightLayer.SKY, pos);

        Direction.Axis localAxis = axis;

        mainShaft = getRotatingMaterial()
                .getModel(AllPartialModels.SHAFT_HALF, blockState,
                        Direction.fromAxisAndDirection(localAxis, Direction.AxisDirection.POSITIVE))
                .createInstance();

        mainShaft.setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(getRotationOffset(localAxis)).setColor(blockEntity)
                .setPosition(getInstancePosition())
                .setBlockLight(blockLight)
                .setSkyLight(skyLight);


        memoryRoll = materialManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(AllPartialModels.ELEVATOR_COIL, blockState, direction)
                .createInstance();

        localAxis = direction.getAxis();
        memoryRoll.setRotationalSpeed(getFanSpeed())
                .setRotationOffset(getRotationOffset(localAxis)).setColor(blockEntity)
                .setPosition(getInstancePosition())
                .setBlockLight(blockLight)
                .setSkyLight(skyLight);

    }
    private float getFanSpeed() {
        return blockEntity.getSpeed();
    }
    @Override
    public void update() {
        updateRotation(mainShaft);
        updateRotation(memoryRoll, direction.getAxis() ,getFanSpeed());
    }

    @Override
    public void updateLight() {
        BlockPos firstSide = pos.relative(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE));
        relight(firstSide, mainShaft);
        BlockPos inFront = pos.relative(direction);
        relight(inFront, memoryRoll);
    }

    @Override
    public void remove() {
        mainShaft.delete();
        memoryRoll.delete();
    }
}
