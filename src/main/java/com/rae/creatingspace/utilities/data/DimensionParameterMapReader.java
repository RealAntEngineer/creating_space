package com.rae.creatingspace.utilities.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import com.rae.creatingspace.CreatingSpace;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rae.creatingspace.CreatingSpace.LOGGER;

public class DimensionParameterMapReader {

    //here to manage the reading of data and translation of matrix
    public static final Codec<AccessibilityParameter> ACCESSIBILITY_PARAMETER_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("deltaV").forGetter(i -> i.deltaV),
                            Codec.INT.optionalFieldOf("arrivalHeight",64).forGetter(i->i.arrivalHeight)
                    )
                    .apply(instance, AccessibilityParameter::new));
    public static final UnboundedMapCodec<String, AccessibilityParameter> ADJACENT_DIMENSIONS_CODEC =
            Codec.unboundedMap(Codec.STRING,ACCESSIBILITY_PARAMETER_CODEC);
    public static final Codec<DimensionEffectElement> DIMENSION_EFFECT_ELEMENT_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("fileName").forGetter(i->i.name),
                    Codec.INT.fieldOf("size").forGetter(i->i.size)

            ).apply(instance,DimensionEffectElement::new));
    public static final Codec<CustomDimensionParameter> DIMENSION_PARAMETER_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            ADJACENT_DIMENSIONS_CODEC.fieldOf("adjacentDimensions").forGetter(i -> i.adjacentDimensions),
                            Codec.INT.fieldOf("arrivalHeight").forGetter(i->i.arrivalHeight),
                            Codec.FLOAT.fieldOf("gravity").forGetter(i->i.gravity),
                            DIMENSION_EFFECT_ELEMENT_CODEC.fieldOf("orbitingPlanet").forGetter(i->i.orbitingPlanet),
                            Codec.list(DIMENSION_EFFECT_ELEMENT_CODEC).fieldOf("suns").forGetter(i->i.suns)

                    )
                    .apply(instance, CustomDimensionParameter::new));

    public static final Codec<Map<String,CustomDimensionParameter>> DIMENSION_PARAMETER_MAP_CODEC =
            Codec.unboundedMap(Codec.STRING,DIMENSION_PARAMETER_CODEC);

    public static final Codec<PartialDimensionParameterMap> PARTIAL_MAP_DIM_PARAMETER_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(i->i.replace),
                    DIMENSION_PARAMETER_MAP_CODEC.fieldOf("values").forGetter(i->i.dimensionParameterMap)
            ).apply(instance, PartialDimensionParameterMap::new));


    public static final SingleFileCodecJsonDataManager<PartialDimensionParameterMap> DIMENSION_MAP_HOLDER = new SingleFileCodecJsonDataManager<>("creatingspace_utilities", CreatingSpace.resource("dimensions_parameters"), PARTIAL_MAP_DIM_PARAMETER_CODEC, LOGGER);

    //make a custom codec ? -> cleaner
    public static HashMap<ResourceKey<Level>, HashMap<ResourceKey<Level>, AccessibilityParameter>> translator(HashMap<String, HashMap<String, AccessibilityParameter>> mapString){
        HashMap<ResourceKey<Level>, HashMap<ResourceKey<Level>, AccessibilityParameter>>
                accessibilityMatrix = new HashMap<>();
        if (!(mapString == null)) {
            for (String origin : mapString.keySet()) {
                HashMap<ResourceKey<Level>, AccessibilityParameter> accessibleDimensionsMap =
                        new HashMap<>();
                for (String destination : mapString.get(origin).keySet()) {
                    AccessibilityParameter parameter = mapString.get(origin).get(destination);
                    ResourceKey<Level> destinationKey = ResourceKey.create(Registries.DIMENSION,
                            new ResourceLocation(destination));
                    accessibleDimensionsMap.put(destinationKey, parameter);
                }
                ResourceKey<Level> originKey = ResourceKey.create(Registries.DIMENSION,
                        new ResourceLocation(origin));
                accessibilityMatrix.put(originKey, accessibleDimensionsMap);
            }
        }
        return accessibilityMatrix;
    }

    public record PartialDimensionParameterMap(boolean replace, Map<String, CustomDimensionParameter> dimensionParameterMap){

    }

    public record AccessibilityParameter(int deltaV,int arrivalHeight){

    }

    public record CustomDimensionParameter(Map<String, AccessibilityParameter> adjacentDimensions, Integer arrivalHeight, Float gravity, DimensionEffectElement orbitingPlanet, List<DimensionEffectElement> suns){

    }

    public record DimensionEffectElement(String name, int size){

    }
}
