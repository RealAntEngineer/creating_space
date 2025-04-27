package com.rae.creatingspace.mixin.worldgen;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Aquifer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Aquifer.FluidStatus.class)
public interface FluidStatusAccessor {
    @Accessor("fluidType")
    BlockState getFluidType();
    @Accessor("fluidLevel")
    int getFluidLevel();
}
