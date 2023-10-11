package com.rae.creatingspace.init.worldgen;

import com.google.common.base.Suppliers;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.utilities.tags.CustomBlockTags;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

public class ConfiguredFeatureInit {
    public static final RuleTest MOON_ORE_REPLACEABLES = new TagMatchTest(CustomBlockTags.MOON_STONE_REPLACEABLE);
    public static final DeferredRegister<ConfiguredFeature<?,?>>
            CONFIGURED_FEATURES = DeferredRegister.create(
                    Registry.CONFIGURED_FEATURE_REGISTRY, CreatingSpace.MODID);

    private static final Supplier<List<OreConfiguration.TargetBlockState>>
            NICKEL_OVERWORLD_REPLACEMENT = Suppliers.memoize(
                    ()->List.of(
                            OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, BlockInit.NICKEL_ORE.get().defaultBlockState()),
                            OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, BlockInit.DEEPSLATE_NICKEL_ORE.get().defaultBlockState())
                    )
            );
    private static final Supplier<List<OreConfiguration.TargetBlockState>>
            NICKEL_MOON_REPLACEMENT = Suppliers.memoize(
            ()->List.of(
                    OreConfiguration.target(MOON_ORE_REPLACEABLES, BlockInit.MOON_NICKEL_ORE.get().defaultBlockState())
            )
    );
    private static final Supplier<List<OreConfiguration.TargetBlockState>>
            COBALT_MOON_REPLACEMENT = Suppliers.memoize(
            ()->List.of(
                    OreConfiguration.target(MOON_ORE_REPLACEABLES, BlockInit.MOON_COBALT_ORE.get().defaultBlockState())
            )
    );
    private static final Supplier<List<OreConfiguration.TargetBlockState>>
            ALUMINUM_MOON_REPLACEMENT = Suppliers.memoize(
            ()->List.of(
                    OreConfiguration.target(MOON_ORE_REPLACEABLES, BlockInit.MOON_ALUMINUM_ORE.get().defaultBlockState())
            )
    );

    public static final RegistryObject<ConfiguredFeature<?,?>> NICKEL_OVERWORLD_ORE =
            CONFIGURED_FEATURES.register(
            "nickel_overworld_ore",
            ()-> new ConfiguredFeature<>(
                    Feature.ORE,
                    new OreConfiguration(NICKEL_OVERWORLD_REPLACEMENT.get(),9)));
    public static final RegistryObject<ConfiguredFeature<?,?>> NICKEL_MOON_ORE =
            CONFIGURED_FEATURES.register(
                    "nickel_moon_ore",
                    ()-> new ConfiguredFeature<>(
                            Feature.ORE,
                            new OreConfiguration(NICKEL_MOON_REPLACEMENT.get(),9)));

    public static final RegistryObject<ConfiguredFeature<?,?>> ALUMINIUM_MOON_ORE =
            CONFIGURED_FEATURES.register(
                    "aluminum_moon_ore",
                    ()-> new ConfiguredFeature<>(
                            Feature.ORE,
                            new OreConfiguration(ALUMINUM_MOON_REPLACEMENT.get(),9)));
    public static final RegistryObject<ConfiguredFeature<?,?>> COBALT_MOON_ORE =
            CONFIGURED_FEATURES.register(
                    "cobalt_moon_ore",
                    ()-> new ConfiguredFeature<>(
                            Feature.ORE,
                            new OreConfiguration(COBALT_MOON_REPLACEMENT.get(),9,0F)));

    /*public static final RegistryObject<ConfiguredFeature<?,?>> IRON_MOON_ORE =
            CONFIGURED_FEATURES.register(
                    "iron_moon_ore",
                    ()-> new ConfiguredFeature<>(
                            Feature.ORE,
                            new OreConfiguration(NICKEL_MOON_REPLACEMENT.get(),9)));
    public static final RegistryObject<ConfiguredFeature<?,?>> ZINC_MOON_ORE =
            CONFIGURED_FEATURES.register(
                    "zinc_moon_ore",
                    ()-> new ConfiguredFeature<>(
                            Feature.ORE,
                            new OreConfiguration(NICKEL_MOON_REPLACEMENT.get(),9)));

    public static final RegistryObject<ConfiguredFeature<?,?>> GOLD_MOON_ORE =
            CONFIGURED_FEATURES.register(
                    "zinc_moon_ore",
                    ()-> new ConfiguredFeature<>(
                            Feature.ORE,
                            new OreConfiguration(NICKEL_OVERWORLD_REPLACEMENT.get(),9)));

    public static final RegistryObject<ConfiguredFeature<?,?>> TIN_MOON_ORE =
            CONFIGURED_FEATURES.register(
                    "zinc_moon_ore",
                    ()-> new ConfiguredFeature<>(
                            Feature.ORE,
                            new OreConfiguration(NICKEL_OVERWORLD_REPLACEMENT.get(),9)));



    public static final RegistryObject<ConfiguredFeature<?,?>> TIN_MARS_ORE =
            CONFIGURED_FEATURES.register(
                    "zinc_moon_ore",
                    ()-> new ConfiguredFeature<>(
                            Feature.ORE,
                            new OreConfiguration(NICKEL_OVERWORLD_REPLACEMENT.get(),9)));*/

    public static void register(IEventBus bus) {
        CONFIGURED_FEATURES.register(bus);
    }


}
