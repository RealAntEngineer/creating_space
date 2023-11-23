package com.rae.creatingspace.utilities.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;

import java.util.Map;

import static com.rae.creatingspace.CreatingSpace.LOGGER;

public class MassOfBlockReader {

    //here to manage the reading of data and translation of matrix
    public static final UnboundedMapCodec<String, Integer> BLOCKS_MASS_CODEC =
            Codec.unboundedMap(Codec.STRING,Codec.INT);

    public static final Codec<PartialMassMap> PARTIAL_BLOCKS_MASS_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(i->i.replace),
                    BLOCKS_MASS_CODEC.fieldOf("values").forGetter(i->i.massMap)
            ).apply(instance, PartialMassMap::new));


    public static final CodecJsonDataManager<PartialMassMap> MASS_HOLDER = new CodecJsonDataManager("creatingspace_utilities", PARTIAL_BLOCKS_MASS_CODEC, LOGGER);



    public record PartialMassMap(boolean replace, Map<String, Integer> massMap){

    }
}
