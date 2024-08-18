package com.rae.creatingspace.server.fluids;

import com.simibubi.create.content.fluids.VirtualFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.registries.ForgeRegistries;

public class CustomVirtualFluid extends VirtualFluid {
    public CustomVirtualFluid(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSource(FluidState fluidState) {
        return !ForgeRegistries.FLUIDS.getKey(fluidState.getType()).getPath().contains("flowing");
    }
}
