package com.rae.creatingspace.server.contraption.movementbehaviour;

import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.rae.creatingspace.server.particle.RocketPlumeParticleData;
import com.rae.creatingspace.utilities.data.FlightDataHelper;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
