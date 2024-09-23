package com.rae.creatingspace.content.fluids;

import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class CustomLavaFluid extends ForgeFlowingFluid {
    protected CustomLavaFluid(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSource(FluidState p_76140_) {
        return false;
    }

    @Override
    public int getAmount(FluidState p_164509_) {
        return 0;
    }
}
