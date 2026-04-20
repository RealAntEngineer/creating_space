package com.rae.creatingspace.content.worldgen.noise;

import net.minecraft.world.level.levelgen.DensityFunction;

public interface DifferentiableDF {
    double computeDerivative(DensityFunction.FunctionContext context);
}
