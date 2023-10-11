package com.rae.creatingspace.init.worldgen;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.server.worldgen.CustomSurfaceRuleData;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static net.minecraft.world.level.levelgen.NoiseRouterData.*;
import static net.minecraft.world.level.levelgen.NoiseSettings.create;

public class NoiseInit {

    public static final DeferredRegister<NoiseGeneratorSettings> NOISE_PARAMETERS_DEFERRED_REGISTER =
            DeferredRegister.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, CreatingSpace.MODID);

    private static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
    private static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE_MOON = createKey("moon/base_3d_noise");
    protected static final NoiseSettings MOON_NOISE_SETTINGS = create(-64, 384, 1, 2);
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("overworld/sloped_cheese");
    private static final ResourceKey<DensityFunction> NOODLE = createKey("overworld/caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = createKey("overworld/caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("overworld/caves/spaghetti_2d");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("overworld/caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> ENTRANCES = createKey("overworld/caves/entrances");
    private static final ResourceKey<DensityFunction> Y = createKey("y");

    public static final RegistryObject<NoiseGeneratorSettings> MOON_NOISE = NOISE_PARAMETERS_DEFERRED_REGISTER.register(
            "moon_noise",
            () -> new NoiseGeneratorSettings(
                    MOON_NOISE_SETTINGS,
                    BlockInit.MOON_STONE.getDefaultState(),
                    Blocks.AIR.defaultBlockState(),
                    moon(BuiltinRegistries.DENSITY_FUNCTION),
                    CustomSurfaceRuleData.moon(),
                    List.of(),
                    64,
                    false,
                    false,
                    true,
                    false)

    );


    private static DensityFunction slideMoonLikeOverworld(DensityFunction densityFunction) {
        return slide(densityFunction, -64, 384, 80, 64, -0.078125D, 0, 24, 0.1171875D);
    }

    private static DensityFunction slideMoon(DensityFunction densityFunction) {
        return slide(densityFunction, -64, 384, 100, 64, -0.1, 0, 24, 0.2);
    }

    private static DensityFunction slide(DensityFunction densityFunction, int bottom, int ceiling, int TopFromY, int TopToY, double p_224449_, int BottomFromY, int BottomToY, double p_224452_) {
        DensityFunction firstClamped1 = DensityFunctions.yClampedGradient(bottom + ceiling - TopFromY, bottom + ceiling - TopToY, 1.0D, 0.0D);
        DensityFunction firstLerped = DensityFunctions.lerp(firstClamped1, p_224449_, densityFunction);
        DensityFunction firstClamped2 = DensityFunctions.yClampedGradient(bottom + BottomFromY, bottom + BottomToY, 0.0D, 1.0D);
        return DensityFunctions.lerp(firstClamped2, p_224452_, firstLerped);
    }
    private static DensityFunction underground(Registry<DensityFunction> densityFunctionRegistry, DensityFunction densityFunction) {
        DensityFunction spaghetti2d = getFunction(densityFunctionRegistry, SPAGHETTI_2D);
        DensityFunction spaghettiRoughness = getFunction(densityFunctionRegistry, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction caveLayer = DensityFunctions.noise(getNoise(Noises.CAVE_LAYER), 8.0D);
        DensityFunction caveLayer2 = DensityFunctions.mul(DensityFunctions.constant(4.0D), caveLayer.square());
        DensityFunction caveCheese = DensityFunctions.noise(getNoise(Noises.CAVE_CHEESE), 0.6666666666666666D);
        DensityFunction caveCheese2 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27D), caveCheese).clamp(-1.0D, 1.0D), DensityFunctions.add(DensityFunctions.constant(1.5D), DensityFunctions.mul(DensityFunctions.constant(-0.64D), densityFunction)).clamp(0.0D, 0.5D));
        DensityFunction unifiedCaveHole = DensityFunctions.add(caveLayer2, caveCheese2);
        DensityFunction caveAndEntrance = DensityFunctions.min(DensityFunctions.min(unifiedCaveHole, getFunction(densityFunctionRegistry, ENTRANCES)), DensityFunctions.add(spaghetti2d, spaghettiRoughness));
        DensityFunction deco = getFunction(densityFunctionRegistry, PILLARS);
        DensityFunction whenToPutDeco = DensityFunctions.rangeChoice(deco, -1000000.0D, 0.03D, DensityFunctions.constant(-1000000.0D), deco);
        return DensityFunctions.max(caveAndEntrance, whenToPutDeco);
    }

    private static NoiseRouter moon(Registry<DensityFunction> densityFunctionRegistry) {
        DensityFunction lava = DensityFunctions.noise(getNoise(Noises.AQUIFER_LAVA));
        DensityFunction xShift = getFunction(densityFunctionRegistry, SHIFT_X);
        DensityFunction zShift = getFunction(densityFunctionRegistry, SHIFT_Z);
        DensityFunction temperature = DensityFunctions.shiftedNoise2d(xShift, zShift, 0.25D, getNoise(Noises.TEMPERATURE));
        DensityFunction vegetation = DensityFunctions.shiftedNoise2d(xShift, zShift, 0.25D, getNoise(Noises.VEGETATION));
        DensityFunction factor = getFunction(densityFunctionRegistry, FACTOR);
        DensityFunction depth = getFunction(densityFunctionRegistry, DEPTH);

        DensityFunction finalDepth = noiseGradientDensity(DensityFunctions.cache2d(factor), depth);
        DensityFunction slopedCheese = getFunction(densityFunctionRegistry, SLOPED_CHEESE);
        DensityFunction entrance = DensityFunctions.min(slopedCheese, DensityFunctions.mul(DensityFunctions.constant(5.0D), getFunction(densityFunctionRegistry, ENTRANCES)));
        DensityFunction rangeChoice = DensityFunctions.rangeChoice(slopedCheese, -1000000.0D, 1.5625D, entrance, underground(densityFunctionRegistry, slopedCheese));
        DensityFunction finalDensity = DensityFunctions.min(postProcess(slideMoon(rangeChoice)), getFunction(densityFunctionRegistry, NOODLE));//3d noise

        DensityFunction aquaBarrier = DensityFunctions.noise(getNoise(Noises.AQUIFER_BARRIER), 0.5D);
        DensityFunction fluidLevelFlood = DensityFunctions.noise(getNoise(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67D);
        DensityFunction fluidLevelSpread = DensityFunctions.noise(getNoise(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143D);
        DensityFunction continents = getFunction(densityFunctionRegistry, CONTINENTS);
        DensityFunction erosion = getFunction(densityFunctionRegistry, EROSION);
        DensityFunction ridges = getFunction(densityFunctionRegistry, RIDGES);
        DensityFunction initialDensity = slideMoon(DensityFunctions.add(finalDepth, DensityFunctions.constant(-0.703125D)).clamp(-64.0D, 64.0D));//terrain shaping
        DensityFunction gap = DensityFunctions.noise(getNoise(Noises.ORE_GAP));
        return new NoiseRouter(aquaBarrier, fluidLevelFlood, fluidLevelSpread, lava, temperature, vegetation, continents, erosion, depth, ridges, initialDensity, finalDensity, DensityFunctions.zero(), DensityFunctions.zero(), gap);
    }

    private static DensityFunction noiseGradientDensity(DensityFunction p_212272_, DensityFunction p_212273_) {
        DensityFunction densityfunction = DensityFunctions.mul(p_212273_, p_212272_);
        return DensityFunctions.mul(
                DensityFunctions.constant(4.0D),
                densityfunction.quarterNegative());
    }
    private static DensityFunction postProcess(DensityFunction p_224493_) {
        DensityFunction densityfunction = DensityFunctions.blendDensity(p_224493_);
        return DensityFunctions.mul(
                DensityFunctions.interpolated(densityfunction),
                DensityFunctions.constant(0.64D)).squeeze();
    }

    private static Holder<NormalNoise.NoiseParameters> getNoise(ResourceKey<NormalNoise.NoiseParameters> p_209543_) {
        return BuiltinRegistries.NOISE.getHolderOrThrow(p_209543_);
    }
    private static DensityFunction getFunction(Registry<DensityFunction> p_224465_, ResourceKey<DensityFunction> p_224466_) {
        return new DensityFunctions.HolderHolder(p_224465_.getHolderOrThrow(p_224466_));
    }
    private static ResourceKey<DensityFunction> createKey(String p_209537_) {
        return ResourceKey.create(Registry.DENSITY_FUNCTION_REGISTRY, new ResourceLocation(p_209537_));
    }
    public static void register(IEventBus bus) {
        NOISE_PARAMETERS_DEFERRED_REGISTER.register(bus);
    }

}
