package com.rae.creatingspace.utilities;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.utilities.data.DimensionParameterMapReader;
import com.rae.creatingspace.utilities.data.DimensionTagsReader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rae.creatingspace.init.worldgen.DimensionInit.*;
import static com.rae.creatingspace.utilities.data.DimensionParameterMapReader.translator;

public class CSDimensionUtil {

    public static float gravity(ResourceKey<DimensionType> dimensionType) {
        DimensionParameterMapReader.PartialDimensionParameterMap dimensionMapData =
                DimensionParameterMapReader.DIMENSION_MAP_HOLDER.getData();

        if (dimensionMapData!=null) {
            DimensionParameterMapReader.CustomDimensionParameter dimensionParameter = dimensionMapData.dimensionParameterMap().get(dimensionType.location().toString());
            if (dimensionParameter!=null){
                Float gravity = dimensionParameter.gravity();
                if (gravity!=null){
                    return gravity;
                }
            }
        }
        return 9.81f;
    }

    //to optimise
    public static HashMap<ResourceKey<Level>, DimensionParameterMapReader.AccessibilityParameter> accessibleFrom(ResourceKey<Level> currentDimension) {

        DimensionParameterMapReader.PartialDimensionParameterMap dimensionMapData =
                DimensionParameterMapReader.DIMENSION_MAP_HOLDER.getData();
        HashMap<String,HashMap<String, DimensionParameterMapReader.AccessibilityParameter>> compressedAccessibilityMatrix = new HashMap<>();

        if (dimensionMapData!=null) {
            Map<String, DimensionParameterMapReader.CustomDimensionParameter> mapOfDimensionParameters = dimensionMapData.dimensionParameterMap();
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


    public static boolean hasO2Atmosphere(ResourceKey<Level> dimension) {
        DimensionTagsReader.PartialDimensionList data =  DimensionTagsReader.DIMENSION_TAGS_HOLDER.getData(CreatingSpace.resource("no_oxygen"));
        boolean no_02 = false;
        if (data!=null) {
            List<String> dimensions = data.dimensions();
             no_02 = dimensions.contains(dimension.location().toString());
            //System.out.println(no_02);
        }
        return !no_02;
    }
    public static boolean isOrbit(ResourceKey<DimensionType> dimensionType) {
        return gravity(dimensionType) == 0;
    }

    public static ResourceKey<Level> planetUnder(ResourceKey<Level> dimension){
        ResourceKey<Level> underDimension = null;
        //make a map of dimension -> dimension
        if (dimension == MOON_ORBIT_KEY){
            underDimension = MOON_KEY;
        }
        if (dimension == EARTH_ORBIT_KEY){
            underDimension = Level.OVERWORLD;
        }
        return underDimension;
    }


}
