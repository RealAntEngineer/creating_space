package com.rae.creatingspace.init.worldgen;

import com.mojang.serialization.Codec;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.worldgen.CustomDensityFunctions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DensityFunctionInit {
    private static final DeferredRegister<Codec<? extends DensityFunction>> DENSITY_FUNCTIONS = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, CreatingSpace.MODID);

    public static final RegistryObject<Codec<CustomDensityFunctions.WorleyDensityFunction>> WORLEY_NOISE_FUNCTION = DENSITY_FUNCTIONS
            .register("worley_noise", CustomDensityFunctions.WorleyDensityFunction.CODEC::codec);

    public static void register(IEventBus bus){
        DENSITY_FUNCTIONS.register(bus);
    }
}
