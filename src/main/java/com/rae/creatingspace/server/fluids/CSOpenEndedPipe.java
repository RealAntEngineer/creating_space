package com.rae.creatingspace.server.fluids;

import com.rae.creatingspace.server.effect.FreezeEffect;
import com.simibubi.create.content.fluids.OpenEndedPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraftforge.fluids.FluidStack;

import static com.simibubi.create.content.fluids.OpenEndedPipe.registerEffectHandler;

public class CSOpenEndedPipe {

    static {
        registerEffectHandler(new OxygenEffectHandler());
    }

    public static class OxygenEffectHandler implements OpenEndedPipe.IEffectHandler {

        @Override
        public boolean canApplyEffects(OpenEndedPipe openEndedPipe, FluidStack fluidStack) {
            return fluidStack.getFluid().getFluidType().getTemperature() < 100;
        }

        @Override
        public void applyEffects(OpenEndedPipe openEndedPipe, FluidStack fluidStack) {
            ServerLevel world = (ServerLevel) openEndedPipe.getWorld();
            BlockPos pos = openEndedPipe.getPos();
            RandomSource random = world.random;

            // Freeze entities
            FreezeEffect.freezeEntities(world, pos);

            // Freeze water and spawn particles
            FreezeEffect.freezeWaterAndSpawnParticles(world, pos, random, false, true);
        }
    }
}
