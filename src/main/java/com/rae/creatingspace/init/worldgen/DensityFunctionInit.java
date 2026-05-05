package com.rae.creatingspace.init.worldgen;

import com.mojang.serialization.Codec;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.worldgen.CustomDensityFunctions;
import com.rae.creatingspace.content.worldgen.noise.PhacelleErosionNoise;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DensityFunctionInit {
    private static final DeferredRegister<Codec<? extends DensityFunction>> DENSITY_FUNCTIONS = DeferredRegister.create(Registries.DENSITY_FUNCTION_TYPE, CreatingSpace.MODID);

    public static final RegistryObject<Codec<CustomDensityFunctions.SinInterpolationDF>> SIN_INTERPOLATION = DENSITY_FUNCTIONS
            .register("sin_interpolation", CustomDensityFunctions.SinInterpolationDF.CODEC::codec);

    public static final RegistryObject<Codec<CustomDensityFunctions.LinearInterpolationDF>> LINEAR_INTERPOLATION = DENSITY_FUNCTIONS
            .register("linear_interpolation", CustomDensityFunctions.LinearInterpolationDF.CODEC::codec);

    public static final RegistryObject<Codec<CustomDensityFunctions.WorleyDensityFunction>> WORLEY_NOISE = DENSITY_FUNCTIONS
            .register("worley_noise", CustomDensityFunctions.WorleyDensityFunction.CODEC::codec);

    public static final RegistryObject<Codec<CustomDensityFunctions.Worley2DDensityFunction>> WORLEY_NOISE_2D = DENSITY_FUNCTIONS
            .register("worley_noise_2d", CustomDensityFunctions.Worley2DDensityFunction.CODEC::codec);

    public static final RegistryObject<Codec<PhacelleErosionNoise>> PhacelleNoise = DENSITY_FUNCTIONS.register(
            "phacelle_erosion", PhacelleErosionNoise.CODEC::codec);

    public static void register(IEventBus bus){
        DENSITY_FUNCTIONS.register(bus);
    }
}