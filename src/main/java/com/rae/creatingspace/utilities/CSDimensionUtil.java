package com.rae.creatingspace.utilities;

import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import com.rae.creatingspace.init.TagsInit;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CSDimensionUtil {
    //should be updated on both server and client load (first on client, then on client join)
    public static Map<ResourceLocation, RocketAccessibleDimension> travelMap;

    public static void updateTravelMapFromRegistry(Registry<RocketAccessibleDimension> registry) {
        Map<ResourceLocation, RocketAccessibleDimension> collector = new HashMap<>();
        registry.registryKeySet().forEach(resourceKey -> {
                    collector.put(resourceKey.location(), registry.get(resourceKey.location()));
                }
        );
        travelMap = Map.copyOf(collector);
        System.out.println(travelMap);
    }

    //TODO change resource key to resource location
    @Deprecated(forRemoval = true)
    public static float gravity(ResourceKey<DimensionType> dimensionType) {
        return gravity(dimensionType.location());
    }

    public static float gravity(ResourceLocation location) {
        if (travelMap != null) {
            RocketAccessibleDimension dimensionParameter = travelMap.get(location);
            if (dimensionParameter!=null){
                return dimensionParameter.gravity();
            }
        }
        return 9.81f;
    }

    @Deprecated(forRemoval = true)
    public static int arrivalHeight(ResourceKey<DimensionType> dimensionType) {
        return arrivalHeight(dimensionType.location());
    }

    public static int arrivalHeight(ResourceLocation location) {
        if (travelMap != null) {
            RocketAccessibleDimension dimensionParameter = travelMap.get(location);
            if (dimensionParameter != null) {
                return dimensionParameter.arrivalHeight();
            }
        }
        return 64;
    }
    public static Map<ResourceLocation, RocketAccessibleDimension.AccessibilityParameter> accessibleFrom(ResourceLocation currentDimension) {
        if (travelMap != null) {
            if (travelMap.containsKey(currentDimension)) {
                return travelMap.get(currentDimension).adjacentDimensions();
            }
        }
        return new HashMap<>();
    }

    public static boolean hasO2Atmosphere(ResourceLocation biome) {
        return !TagsInit.CustomBiomeTags.NO_OXYGEN.matches(biome);
    }

    public static boolean hasO2Atmosphere(Holder<Biome> biome) {
        return !TagsInit.CustomBiomeTags.NO_OXYGEN.matches(biome);
    }
    public static boolean isOrbit(ResourceKey<DimensionType> dimensionType) {
        return gravity(dimensionType) == 0;
    }

    public static @Nullable ResourceKey<Level> planetUnder(ResourceLocation dimension) {
        if (travelMap != null) {
            if (travelMap.containsKey(dimension)) {
                return ResourceKey.create(Registry.DIMENSION_REGISTRY, travelMap.get(dimension).orbitedBody());
            }
        }
        return null;
    }

    private static class TravelGraph {
    }
}
