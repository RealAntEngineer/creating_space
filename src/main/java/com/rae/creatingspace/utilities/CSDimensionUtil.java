package com.rae.creatingspace.utilities;

import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import com.rae.creatingspace.init.TagsInit;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CSDimensionUtil {
    //should be updated on both server and client load (first on client, then on client join)
    private static final Logger LOGGER = LogManager.getLogger();

    public static Map<ResourceLocation, RocketAccessibleDimension> getTravelMap() {
        if (travelMap == null){
            LOGGER.info("updating the travel map");
            CSDimensionUtil.updatePlanetsFromRegistry(Objects.requireNonNull(Minecraft.getInstance().getConnection())
                    .registryAccess().registry(RocketAccessibleDimension.REGISTRY_KEY)
                    .orElseThrow());
            LOGGER.info("updating the space travel cost map");
            CSDimensionUtil.updateCostMap();
            CSDimensionUtil.removeUnreachableDimensions();
        }
        return travelMap;
    }

    public static List<ResourceLocation> getPlanets() {
        if (planets == null){
            LOGGER.info("updating the travel map");
            CSDimensionUtil.updatePlanetsFromRegistry(Objects.requireNonNull(Minecraft.getInstance().getConnection())
                    .registryAccess().registry(RocketAccessibleDimension.REGISTRY_KEY)
                    .orElseThrow());
            LOGGER.info("updating the space travel cost map");
            CSDimensionUtil.updateCostMap();
            CSDimensionUtil.removeUnreachableDimensions();
        }
        return planets;
    }

    public static void removeUnreachableDimensions() {
        planets = planets.stream().filter(
                l-> {
                    Map<ResourceLocation, Integer> cost = costAdjacentMap.get(l);
                    for (Map.Entry<ResourceLocation, Integer> entries:
                         cost.entrySet()) {
                        if (entries.getValue() > 0 && entries.getValue() < Integer.MAX_VALUE){
                            return true;
                        }
                    }
                    return false;
                }
        ).toList();
    }

    private static Map<ResourceLocation, RocketAccessibleDimension> travelMap;
    private static List<ResourceLocation> planets;
    private static Map<ResourceLocation, Map<ResourceLocation, Integer>> costAdjacentMap;//map of (source, target) -> cost

    public static void updatePlanetsFromRegistry(Registry<RocketAccessibleDimension> registry) {
        Map<ResourceLocation, RocketAccessibleDimension> collector = new HashMap<>();
        registry.registryKeySet().forEach(resourceKey -> {
                    collector.put(resourceKey.location(), registry.get(resourceKey.location()));
                }
        );
        travelMap = Map.copyOf(collector);
        planets = collector.keySet().stream().toList();
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
        return isOrbit(dimensionType.location());
    }

    public static boolean isOrbit(ResourceLocation dimension) {
        return gravity(dimension) == 0;
    }
    public static @Nullable ResourceKey<Level> planetUnder(ResourceLocation dimension) {
        if (travelMap != null) {
            if (travelMap.containsKey(dimension)) {
                return ResourceKey.create(Registry.DIMENSION_REGISTRY, travelMap.get(dimension).orbitedBody());
            }
        }
        return null;
    }

    //TODO put in api as it's an utility method
    private static Map<ResourceLocation, Integer> dijkstra(ResourceLocation start) {
        // Initialize distances map
        Map<ResourceLocation, Integer> distances = new HashMap<>();
        Map<ResourceLocation, Boolean> visited = new HashMap<>();

        for (ResourceLocation location : travelMap.keySet()) {
            distances.put(location, Integer.MAX_VALUE);
            visited.put(location, false);
        }
        distances.put(start, 0);

        // Priority queue for selecting the next node with the smallest distance
        PriorityQueue<Map.Entry<ResourceLocation, Integer>> priorityQueue = new PriorityQueue<>(Map.Entry.comparingByValue());
        priorityQueue.add(new AbstractMap.SimpleEntry<>(start, 0));

        while (!priorityQueue.isEmpty()) {
            Map.Entry<ResourceLocation, Integer> entry = priorityQueue.poll();
            ResourceLocation current = entry.getKey();

            if (visited.get(current)) continue;

            visited.put(current, true);

            RocketAccessibleDimension currentDimension = travelMap.get(current);
            if (currentDimension != null) {
                for (Map.Entry<ResourceLocation, RocketAccessibleDimension.AccessibilityParameter> adjacent : currentDimension.adjacentDimensions().entrySet()) {
                    ResourceLocation target = adjacent.getKey();
                    int deltaV = adjacent.getValue().deltaV(); 
                    int newDist = distances.get(current) + deltaV;
                    if (distances.get(target) != null) {
                        if (newDist < distances.get(target)) {
                            distances.put(target, newDist);
                            priorityQueue.add(new AbstractMap.SimpleEntry<>(target, newDist));
                        }
                    } else {
                        LOGGER.warn("unexpected null value loading route from : "+ current + " to "+ target);
                    }
                }
            }
        }
        return distances;
    }

    /**
     * if the value returned is < 0, the target dimension is unreachable
     */
    public static int cost(ResourceLocation from, ResourceLocation to) {
        if (costAdjacentMap == null) {
            updateCostMap();
            LOGGER.warn("unexpected null value for space travel cost map, reloading");
        }
        return costAdjacentMap.getOrDefault(from, new HashMap<>()).getOrDefault(to, -1);
    }

    public static void updateCostMap() {
        costAdjacentMap = new HashMap<>();
        for (ResourceLocation location : getTravelMap().keySet()) {
            costAdjacentMap.put(location, dijkstra(location));
        }
    }
}
