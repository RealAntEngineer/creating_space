package com.rae.creatingspace.server.contraption.movementbehaviour;

import com.rae.creatingspace.server.contraption.RocketContraption;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;

public class EngineMovementBehaviour implements MovementBehaviour {
    @Override
    public boolean isActive(MovementContext context) {
        return MovementBehaviour.super.isActive(context) && (context.contraption instanceof RocketContraption);
    }

    @Override
    public void tick(MovementContext context) {
        if (context.world == null || !context.world.isClientSide || context.position == null
                || context.disabled)
            return;

        // Mostly copied from CampfireBlock and CampfireBlockEntity
        RandomSource random = context.world.random;

            for (int i = 0; i < 10; ++i) {
                context.world.addAlwaysVisibleParticle(
                        ParticleTypes.FLAME,
                        true, context.position.x() + random.nextDouble() / (random.nextBoolean() ? 3D : -3D),
                        context.position.y() - 1 ,
                        context.position.z() + random.nextDouble() / (random.nextBoolean() ? 3D : -3D),
                        0.0D, -3D, 0.0D);

        }
    }
}
