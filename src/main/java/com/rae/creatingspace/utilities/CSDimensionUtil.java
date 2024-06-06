package com.rae.creatingspace.utilities;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.utilities.data.DimensionParameterMapReader;
import com.rae.creatingspace.utilities.data.DimensionTagsReader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rae.creatingspace.utilities.data.DimensionParameterMapReader.translator;

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
    //to optimise
    @Deprecated(forRemoval = true)//use  accessibleFrom(ResourceLocation currentDimension) instead
    public static HashMap<ResourceKey<Level>,
            DimensionParameterMapReader.AccessibilityParameter> accessibleFrom(ResourceKey<Level> currentDimension) {

        DimensionParameterMapReader.PartialDimensionParameterMap travelMap =
                DimensionParameterMapReader.DIMENSION_MAP_HOLDER.getData();
        HashMap<String,HashMap<String, DimensionParameterMapReader.AccessibilityParameter>> compressedAccessibilityMatrix = new HashMap<>();

        if (travelMap != null) {
            Map<String, DimensionParameterMapReader.CustomDimensionParameter> mapOfDimensionParameters = travelMap.dimensionParameterMap();
            for (String originDimension : mapOfDimensionParameters.keySet()) {
                HashMap<String, DimensionParameterMapReader.AccessibilityParameter> adjacentDimensions = new HashMap<>(mapOfDimensionParameters.get(originDimension).adjacentDimensions());

                for(String destination : adjacentDimensions.keySet()){
                    Integer arrivalHeight = mapOfDimensionParameters.get(destination).arrivalHeight();
                    if (arrivalHeight==null){
                        arrivalHeight = 64;
                    }
                    adjacentDimensions.put(destination,
                            new DimensionParameterMapReader.AccessibilityParameter(
                                    adjacentDimensions.get(destination).deltaV(),
                                    arrivalHeight));
                }

                compressedAccessibilityMatrix.put(originDimension,adjacentDimensions);
            }

            //the "replace" boolean doesn't do anything for now so ...
        }

        HashMap<ResourceKey<Level>, HashMap<ResourceKey<Level>, DimensionParameterMapReader.AccessibilityParameter>> accessibilityMap = translator(compressedAccessibilityMatrix);//.createFromStringList(list);

        //better : a get method in the Reader Class ? -> making an abstract Reader Class ?

        if (accessibilityMap.containsKey(currentDimension)){
            return  accessibilityMap.get(currentDimension);
        }
        return new HashMap<>();
    }

    public static Map<ResourceLocation, RocketAccessibleDimension.AccessibilityParameter> accessibleFrom(ResourceLocation currentDimension) {
        if (travelMap != null) {
            if (travelMap.containsKey(currentDimension)) {
                return travelMap.get(currentDimension).adjacentDimensions();
            }
        }
        return new HashMap<>();
    }

    @Deprecated
    public static boolean hasO2Atmosphere(ResourceKey<Level> dimension) {
        DimensionTagsReader.PartialDimensionList data =  DimensionTagsReader.DIMENSION_TAGS_HOLDER.getData(CreatingSpace.resource("no_oxygen"));
        boolean no_02 = false;
        if (data!=null) {
            List<String> dimensions = data.dimensions();
             no_02 = dimensions.contains(dimension.location().toString());
        }
        return !no_02;
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

    public static @Nullable ResourceKey<Level> planetUnder(ResourceKey<Level> dimension){
        DimensionParameterMapReader.PartialDimensionParameterMap dimensionMapData =
                DimensionParameterMapReader.DIMENSION_MAP_HOLDER.getData();

        if (dimensionMapData!=null) {
            DimensionParameterMapReader.CustomDimensionParameter dimensionParameter =
                    dimensionMapData.dimensionParameterMap()
                            .get(dimension.location().toString());
            if (dimensionParameter!=null){
                //TODO better protection against bad resource location
                if (dimensionParameter.planetUnder() != "") {
                    return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dimensionParameter.planetUnder()));
                }
            }
        }
        return null;
    }

    private static class TravelGraph {
    }
}
