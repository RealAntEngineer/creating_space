package com.rae.creatingspace.utilities.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

import static com.rae.creatingspace.CreatingSpace.LOGGER;

public class NoO2AtmosphereReader {

    //here to manage the reading of data and translation of matrix
    public static final Codec<List<String>> DIMENSION_TAGS_CODEC =
            Codec.list(Codec.STRING);

    public static final Codec<PartialDimensionList> PARTIAL_DIMENSION_TAGS_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(i->i.replace),
                    DIMENSION_TAGS_CODEC.fieldOf("values").forGetter(i->i.dimensions)
            ).apply(instance, PartialDimensionList::new));


    public static final CodecJsonDataManager<PartialDimensionList> DIMENSION_TAGS_HOLDER = new CodecJsonDataManager<>("creatingspace_utilities/dimension_tags", PARTIAL_DIMENSION_TAGS_CODEC, LOGGER);


    public record PartialDimensionList(boolean replace, List<String> dimensions){

    }
}
