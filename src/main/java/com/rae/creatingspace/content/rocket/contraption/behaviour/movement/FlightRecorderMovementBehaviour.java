package com.rae.creatingspace.content.rocket.contraption.behaviour.movement;

import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.util.RandomSource;

public class FlightRecorderMovementBehaviour implements MovementBehaviour {

    static RandomSource r = RandomSource.create();

    @Override
    public boolean isActive(MovementContext context) {
        return MovementBehaviour.super.isActive(context) && (context.contraption.entity instanceof RocketContraptionEntity);
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world == null ||  context.position == null
                || context.disabled || !context.world.isClientSide()) return;
        //TODO make an animation for the rocket flight ->  rotate the roll ?
    }
}
