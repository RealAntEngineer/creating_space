package com.rae.creatingspace.utilities.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

import static com.rae.creatingspace.CreatingSpace.LOGGER;

public class NoO2AtmosphereReader {

    //here to manage the reading of data and translation of matrix
    public static final Codec<List<String>> DIMENSIONS_GRAVITY_CODEC =
            Codec.list(Codec.STRING);

    public static final Codec<PartialNoO2AtmosphereList> PARTIAL_DIMENSIONS_GRAVITY_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(i->i.replace),
                    DIMENSIONS_GRAVITY_CODEC.fieldOf("values").forGetter(i->i.dimensions)
            ).apply(instance, PartialNoO2AtmosphereList::new));


    public static final CodecJsonDataManager<PartialNoO2AtmosphereList> NO_ATMOSPHERE_HOLDER = new CodecJsonDataManager<>("creatingspace_utilities", PARTIAL_DIMENSIONS_GRAVITY_CODEC, LOGGER);


    public record PartialNoO2AtmosphereList(boolean replace, List<String> dimensions){

    }
}
