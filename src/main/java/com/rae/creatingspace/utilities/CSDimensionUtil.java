package com.rae.creatingspace.utilities;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.utilities.data.AccessibilityMatrixReader;
import com.rae.creatingspace.utilities.data.NoO2AtmosphereReader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rae.creatingspace.init.worldgen.DimensionInit.*;
import static com.rae.creatingspace.utilities.data.AccessibilityMatrixReader.translator;

public class CSDimensionUtil {

    public static float gravity(ResourceKey<DimensionType> dimensionType) {
        if(dimensionType == EARTH_ORBIT_TYPE){
            return 0f;
        }
        if (dimensionType == MOON_ORBIT_TYPE){
            return 0f;
        }
        if (dimensionType == MOON_TYPE){
            return 1.6f;
        }
        return 9.81f;
    }

    //to optimise
    public static HashMap<ResourceKey<Level>, AccessibilityMatrixReader.AccessibilityParameter> accessibleFrom(ResourceKey<Level> currentDimension) {
        Map<String, Map<String, AccessibilityMatrixReader.AccessibilityParameter>> compressedAccessibilityMatrix = new HashMap<>();

        AccessibilityMatrixReader.PartialAccessibilityMatrix matrixHolderData =
                AccessibilityMatrixReader.MATRIX_HOLDER.getData(CreatingSpace.resource("accessibility_matrix"));
        if (matrixHolderData!=null) {
            Map<String, Map<String, AccessibilityMatrixReader.AccessibilityParameter>> newCompressedMatrix = matrixHolderData.partialMatrix();

            if (matrixHolderData.replace()) {
                compressedAccessibilityMatrix = newCompressedMatrix;
            }
            else {
                HashMap<String, Map<String, AccessibilityMatrixReader.AccessibilityParameter>> mutableCompressedMatrix = new HashMap<>(compressedAccessibilityMatrix);
                HashMap<String, Map<String, AccessibilityMatrixReader.AccessibilityParameter>> mutableNewMatrix = new HashMap<>(newCompressedMatrix);

                for (String origin : newCompressedMatrix.keySet()){
                    if (mutableCompressedMatrix.containsKey(origin)){
                        mutableCompressedMatrix.merge(origin,mutableNewMatrix.get(origin),
                                (oldValue,newValue)-> {
                                    HashMap<String, AccessibilityMatrixReader.AccessibilityParameter> mergedValue = new HashMap<>(oldValue);
                                    for (String destination : newValue.keySet()){
                                        //! immutable Map !!!
                                        mergedValue.put(destination,newValue.get(destination));
                                    }
                                    return mergedValue;
                                });
                    } else {
                        mutableCompressedMatrix.put(origin,mutableNewMatrix.get(origin));
                    }
                }
                compressedAccessibilityMatrix = mutableCompressedMatrix;
            }
        }


        HashMap<ResourceKey<Level>, HashMap<ResourceKey<Level>, AccessibilityMatrixReader.AccessibilityParameter>> accessibilityMap = translator(compressedAccessibilityMatrix);//.createFromStringList(list);

        //better : a get method in the Reader Class ? -> making an abstract Reader Class ?

        if (accessibilityMap.containsKey(currentDimension)){
            return  accessibilityMap.get(currentDimension);
        }
        return new HashMap<>();
    }


    public static boolean hasO2Atmosphere(ResourceKey<Level> dimension) {
        NoO2AtmosphereReader.PartialNoO2AtmosphereList data =  NoO2AtmosphereReader.NO_ATMOSPHERE_HOLDER.getData(CreatingSpace.resource("no_oxygen"));
        boolean no_02 = false;
        if (data!=null) {
            List<String> dimensions = data.dimensions();
             no_02 = /*CSConfigs.COMMON.dimAccess.no_02.get()*/dimensions.contains(dimension.location().toString());
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
