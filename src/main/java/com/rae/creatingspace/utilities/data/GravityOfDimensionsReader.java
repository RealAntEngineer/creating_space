package com.rae.creatingspace.utilities.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;

import java.util.Map;

import static com.rae.creatingspace.CreatingSpace.LOGGER;

public class GravityOfDimensionsReader {

    //here to manage the reading of data and translation of matrix
    public static final UnboundedMapCodec<String, Integer> DIMENSIONS_GRAVITY_CODEC =
            Codec.unboundedMap(Codec.STRING,Codec.INT);

    public static final Codec<PartialDimensionsGravityMap> PARTIAL_DIMENSIONS_GRAVITY_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(i->i.replace),
                    DIMENSIONS_GRAVITY_CODEC.fieldOf("values").forGetter(i->i.gravityMap)
            ).apply(instance, PartialDimensionsGravityMap::new));


    public static final CodecJsonDataManager<PartialDimensionsGravityMap> GRAVITY_HOLDER = new CodecJsonDataManager("creatingspace_utilities", PARTIAL_DIMENSIONS_GRAVITY_CODEC, LOGGER);


    public record PartialDimensionsGravityMap(boolean replace, Map<String, Integer> gravityMap){

    }
}
