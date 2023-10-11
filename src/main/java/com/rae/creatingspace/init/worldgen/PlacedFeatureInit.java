package com.rae.creatingspace.init.worldgen;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;


public class PlacedFeatureInit {
    public static final DeferredRegister<PlacedFeature> PLACED_FEATURES =
            DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, CreatingSpace.MODID);

    public static final RegistryObject<PlacedFeature> NICKEL_OVERWORLD_ORE = PLACED_FEATURES.register("nickel_overworld_ore",
            () -> new PlacedFeature(ConfiguredFeatureInit.NICKEL_OVERWORLD_ORE.getHolder().get(),
                    commonOrePlacement(20,HeightRangePlacement.triangle(
                            VerticalAnchor.aboveBottom(10),
                            VerticalAnchor.absolute(60)
                    ))));

    public static final RegistryObject<PlacedFeature> NICKEL_MOON_ORE = PLACED_FEATURES.register("nickel_moon_ore",
            () -> new PlacedFeature((ConfiguredFeatureInit.NICKEL_MOON_ORE.getHolder().get()),
                    commonOrePlacement(20,HeightRangePlacement.triangle(
                            VerticalAnchor.aboveBottom(10),
                            VerticalAnchor.absolute(60)
                    ))));
    public static final RegistryObject<PlacedFeature> COBALT_MOON_ORE = PLACED_FEATURES.register("cobalt_moon_ore",
            () -> new PlacedFeature((ConfiguredFeatureInit.COBALT_MOON_ORE.getHolder().get()),
                    commonOrePlacement(20,HeightRangePlacement.triangle(
                            VerticalAnchor.aboveBottom(10),
                            VerticalAnchor.absolute(60)
                    ))));
    public static final RegistryObject<PlacedFeature> ALUMINUM_MOON_ORE = PLACED_FEATURES.register("aluminium_moon_ore",
            () -> new PlacedFeature((ConfiguredFeatureInit.ALUMINIUM_MOON_ORE.getHolder().get()),
                    commonOrePlacement(20,HeightRangePlacement.triangle(
                            VerticalAnchor.aboveBottom(10),
                            VerticalAnchor.absolute(60)
                    ))));
    private static List<PlacementModifier> commonOrePlacement(int countPerChunk, PlacementModifier height) {
        return orePlacement(CountPlacement.of(countPerChunk),height);
    }
    private static List<PlacementModifier> orePlacement(PlacementModifier count, PlacementModifier height) {
        return List.of(count, InSquarePlacement.spread(), height, BiomeFilter.biome());
    }

    public static void register(IEventBus bus) {
        PLACED_FEATURES.register(bus);
    }
}
