package com.rae.creatingspace.utilities.data;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.CreatingSpace;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

import static com.rae.creatingspace.CreatingSpace.LOGGER;

public class AccessibilityMatrixReader {

    //here to manage the reading of data and translation of matrix
    public static final Codec<AccessibilityParameter> ACCESSIBILITY_PARAMETER_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
            Codec.INT.fieldOf("deltaV").forGetter(i -> i.deltaV),
            Codec.INT.fieldOf("arrivalHeight").forGetter(i -> i.arrivalHeight)
    )
                    .apply(instance, AccessibilityParameter::new));

    public static final Codec<Map<String, Map<String, AccessibilityParameter>>> ACCESSIBILITY_MATRIX_CODEC =
            Codec.unboundedMap(Codec.STRING,Codec.unboundedMap(Codec.STRING, ACCESSIBILITY_PARAMETER_CODEC));

    public static final Codec<PartialAccessibilityMatrix> PARTIAL_ACCESSIBILITY_MATRIX_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(i->i.replace),
                    ACCESSIBILITY_MATRIX_CODEC.fieldOf("values").forGetter(i->i.partialMatrix)
            ).apply(instance,PartialAccessibilityMatrix::new));


    public static final CodecJsonDataManager<PartialAccessibilityMatrix> MATRIX_HOLDER = new CodecJsonDataManager("creatingspace_utilities", PARTIAL_ACCESSIBILITY_MATRIX_CODEC, LOGGER);


    public static HashMap<ResourceKey<Level>, HashMap<ResourceKey<Level>, AccessibilityParameter>> translator(Map<String, Map<String, AccessibilityParameter>> mapString){
        HashMap<ResourceKey<Level>, HashMap<ResourceKey<Level>, AccessibilityParameter>>
                accessibilityMatrix = new HashMap<>();
        if (!(mapString == null)) {
            for (String origin : mapString.keySet()) {
                HashMap<ResourceKey<Level>, AccessibilityParameter> accessibleDimensionsMap =
                        new HashMap<>();
                for (String destination : mapString.get(origin).keySet()) {
                    AccessibilityParameter parameter = mapString.get(origin).get(destination);
                    ResourceKey<Level> destinationKey = ResourceKey.create(Registry.DIMENSION_REGISTRY,
                            new ResourceLocation(destination));
                    accessibleDimensionsMap.put(destinationKey, parameter);
                }
                ResourceKey<Level> originKey = ResourceKey.create(Registry.DIMENSION_REGISTRY,
                        new ResourceLocation(origin));
                accessibilityMatrix.put(originKey, accessibleDimensionsMap);
            }
        }
        return accessibilityMatrix;
    }

    public record PartialAccessibilityMatrix(boolean replace, Map<String, Map<String, AccessibilityParameter>> partialMatrix){

    }

    public record AccessibilityParameter(int deltaV,int arrivalHeight){

    }
}
