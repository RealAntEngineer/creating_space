package com.rae.creatingspace.content.life_support.sealer;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import java.util.function.Consumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class CSFanVisual extends KineticBlockEntityVisual<KineticBlockEntity> {
    protected final RotatingInstance positiveShaft;
    protected final RotatingInstance negativeShaft;
    protected final RotatingInstance fan;
    final Direction direction;

    public CSFanVisual(VisualizationContext context, KineticBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        direction = blockState.getValue(FACING);

        positiveShaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF))
                .createInstance();
        negativeShaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF))
                .createInstance();
        fan = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.ENCASED_FAN_INNER))
                .createInstance();

        positiveShaft.setup(blockEntity,rotationAxis(), blockEntity.getSpeed())
                .setPosition(getVisualPosition())
                .rotateToFace(Direction.SOUTH, rotationAxis())
                .setChanged();
        negativeShaft.setup(blockEntity,rotationAxis(), blockEntity.getSpeed())
                .setPosition(getVisualPosition())
                .rotateToFace(Direction.NORTH, rotationAxis())
                .setChanged();

        fan.setup(blockEntity,direction.getAxis(), getFanSpeed())
                .setPosition(getVisualPosition())
                .rotateToFace(Direction.SOUTH, direction.getOpposite())
                .setChanged();
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
    public void update(float pt) {
        negativeShaft.setup(blockEntity,rotationAxis(), blockEntity.getSpeed())
                .setChanged();
        positiveShaft.setup(blockEntity,rotationAxis(), blockEntity.getSpeed())
                .setChanged();
        fan.setup(blockEntity,direction.getAxis(), getFanSpeed())
                .setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        BlockPos positiveAxis = pos.relative(rotationAxis(),1);
        relight(positiveAxis, positiveShaft);

        BlockPos negativeAxis = pos.relative(rotationAxis(),-1);
        relight(negativeAxis, negativeShaft);

        BlockPos inFront = pos.relative(direction);
        relight(inFront, fan);
    }

    @Override
    protected void _delete() {
        negativeShaft.delete();
        positiveShaft.delete();
        fan.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<Instance> consumer) {
        consumer.accept(negativeShaft);
        consumer.accept(positiveShaft);
        consumer.accept(fan);
    }
}
