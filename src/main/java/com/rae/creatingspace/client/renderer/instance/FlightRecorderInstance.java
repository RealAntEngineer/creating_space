package com.rae.creatingspace.client.renderer.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.rae.creatingspace.init.graphics.PartialModelInit;
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
    protected final RotatingData firstShaft;
    protected final RotatingData oppositeShaft;
    protected final RotatingData memoryRoll;
    final Direction direction;
    public FlightRecorderInstance(MaterialManager materialManager, FlightRecorderBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        direction = blockState.getValue(FACING);

        int blockLight = world.getBrightness(LightLayer.BLOCK, pos);
        int skyLight = world.getBrightness(LightLayer.SKY, pos);

        Direction.Axis localAxis = axis;

        firstShaft = getRotatingMaterial()
                .getModel(AllPartialModels.SHAFT_HALF, blockState,
                        Direction.fromAxisAndDirection(localAxis, Direction.AxisDirection.POSITIVE))
                .createInstance();
        oppositeShaft = getRotatingMaterial()
                .getModel(AllPartialModels.SHAFT_HALF, blockState,
                        Direction.fromAxisAndDirection(localAxis, Direction.AxisDirection.NEGATIVE))
                .createInstance();


        firstShaft.setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(getRotationOffset(localAxis)).setColor(blockEntity)
                .setPosition(getInstancePosition())
                .setBlockLight(blockLight)
                .setSkyLight(skyLight);

        oppositeShaft.setRotationalSpeed(-getBlockEntitySpeed())
                .setRotationOffset(getRotationOffset(localAxis)).setColor(blockEntity)
                .setPosition(getInstancePosition())
                .setBlockLight(blockLight)
                .setSkyLight(skyLight);




        memoryRoll = materialManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(PartialModelInit.MEMORY_ROLL, blockState,
                        Direction.fromAxisAndDirection(localAxis, Direction.AxisDirection.POSITIVE))
                .createInstance();

        //localAxis = direction.getAxis();
        memoryRoll.setRotationalSpeed(getBlockEntitySpeed())
                .setRotationOffset(getRotationOffset(localAxis)).setColor(blockEntity)
                .setPosition(getInstancePosition())
                .setBlockLight(blockLight)
                .setSkyLight(skyLight);

        setup(firstShaft);
        setup(oppositeShaft);
        setup(memoryRoll);
    }
    @Override
    public void update() {
        updateRotation(firstShaft);
        updateRotation(oppositeShaft);
        updateRotation(memoryRoll);
    }

    @Override
    public void updateLight() {
        BlockPos firstSide = pos.relative(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE));
        relight(firstSide, firstShaft);
        BlockPos oppositeSide = pos.relative(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE));
        relight(oppositeSide,oppositeShaft);
        BlockPos inFront = pos.relative(direction);
        relight(inFront, memoryRoll);
    }
   @Override
    public void remove() {
        firstShaft.delete();
        oppositeShaft.delete();
        memoryRoll.delete();
    }
}
