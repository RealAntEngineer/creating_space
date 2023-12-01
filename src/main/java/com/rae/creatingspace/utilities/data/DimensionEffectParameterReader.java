package com.rae.creatingspace.utilities.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

import static com.rae.creatingspace.CreatingSpace.LOGGER;

public class DimensionEffectParameterReader {

    //here to manage the reading of data and translation of matrix
    public static final Codec<EffectParameter> EFFECTS_PARAMETER_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.optionalFieldOf("modid", "").forGetter(EffectParameter::modid), // Field for mod I
                            Codec.STRING.optionalFieldOf("texture", "").forGetter(EffectParameter::texture) // Field for texture location

                    )
                    .apply(instance, EffectParameter::new));

    public static final Codec<Map<String, EffectParameter>> EFFECT_MAP_CODEC =
            Codec.unboundedMap(Codec.STRING, EFFECTS_PARAMETER_CODEC);

    public static final Codec<PartialEffectMap> PARTIAL_EFFECT_MAP_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(i->i.replace),
                    EFFECT_MAP_CODEC.fieldOf("values").forGetter(i->i.partialMap)
            ).apply(instance, PartialEffectMap::new));


    public static final CodecJsonDataManager<PartialEffectMap> EFFECT_MAP_HOLDER = new CodecJsonDataManager("creatingspace_utilities", PARTIAL_EFFECT_MAP_CODEC, LOGGER);

    //make a custom codec ? -> cleaner
    public static HashMap<ResourceKey<Level>, EffectParameter> translator(Map<String, EffectParameter> mapString){
        HashMap<ResourceKey<Level>, EffectParameter>
                dimensionMap = new HashMap<>();
        if (!(mapString == null)) {
            for (String dimension : mapString.keySet()) {

                EffectParameter parameter = mapString.get(dimension);
                ResourceKey<Level> dimensionKey = ResourceKey.create(Registry.DIMENSION_REGISTRY,
                        new ResourceLocation(dimension));
                dimensionMap.put(dimensionKey, parameter);
            }
        }
        return dimensionMap;
    }

    public record PartialEffectMap(boolean replace, Map<String, EffectParameter> partialMap){

    }

    public record EffectParameter(String modid, String texture){

    }
}
