package com.rae.creatingspace.content.recipes.air_liquefying;

import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.foundation.render.AllMaterialSpecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class AirLiquefierInstance extends KineticBlockEntityInstance<AirLiquefierBlockEntity> {
    protected final RotatingData firstShaft;
    protected final RotatingData oppositeShaft;

    protected final RotatingData fan;
    final Direction direction;
    private final Direction opposite;
    public AirLiquefierInstance(MaterialManager materialManager, AirLiquefierBlockEntity blockEntity) {
        super(materialManager, blockEntity);

        direction = blockState.getValue(FACING);
        opposite = direction.getOpposite();

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

        fan = materialManager.defaultCutout()
                .material(AllMaterialSpecs.ROTATING)
                .getModel(AllPartialModels.ENCASED_FAN_INNER, blockState, opposite)
                .createInstance();
        localAxis = direction.getAxis();
        fan.setRotationalSpeed(getFanSpeed())
                .setRotationOffset(getRotationOffset(localAxis)).setColor(blockEntity)
                .setPosition(getInstancePosition())
                .setBlockLight(blockLight)
                .setSkyLight(skyLight);

        setup(firstShaft);
        setup(oppositeShaft);
        setup(fan,localAxis);

    }
    private float getFanSpeed() {
        float speed = blockEntity.getSpeed() * 5;
        if (speed > 0)
            speed = Mth.clamp(speed, 80, 64 * 20);
        if (speed < 0)
            speed = Mth.clamp(speed, -64 * 20, -80);
        return speed;
    }
    @Override
    public void update() {
        updateRotation(firstShaft);
        updateRotation(oppositeShaft);
        updateRotation(fan, direction.getAxis() ,getFanSpeed());
    }

    @Override
    public void updateLight() {
        BlockPos firstSide = pos.relative(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE));
        relight(firstSide, firstShaft);
        BlockPos oppositeSide = pos.relative(Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE));
        relight(oppositeSide,oppositeShaft);
        BlockPos inFront = pos.relative(direction);
        relight(inFront, fan);
    }

    @Override
    public void remove() {
        firstShaft.delete();
        oppositeShaft.delete();
        fan.delete();
    }
}
