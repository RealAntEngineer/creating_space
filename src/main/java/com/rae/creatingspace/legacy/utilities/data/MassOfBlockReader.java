package com.rae.creatingspace.legacy.utilities.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import com.rae.creatingspace.CreatingSpace;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
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


    public static final SingleFileCodecJsonDataManager<PartialMassMap> MASS_HOLDER = new SingleFileCodecJsonDataManager<>("creatingspace_utilities", CreatingSpace.resource("blocks_mass"), PARTIAL_BLOCKS_MASS_CODEC, LOGGER);

    public static Map<TagKey<Block>, Integer> getOnlyTags(PartialMassMap data) {
        Map<String, Integer> rawMap = data.massMap();
        HashMap<TagKey<Block>, Integer> finalMap = new HashMap<>();
        for (String key : rawMap.keySet()){
            if (key.contains("#")){
                String location = key.replace("#","");
                ResourceLocation tagLocation = new ResourceLocation(location);
                TagKey<Block> blockTag = BlockTags.create(tagLocation);
                finalMap.put(blockTag,rawMap.get(key));
            }
        }
        return finalMap;
    }

    public static Map<ResourceLocation, Integer> getWithoutTags(PartialMassMap data) {
        Map<String, Integer> rawMap = data.massMap();
        HashMap<ResourceLocation, Integer> finalMap = new HashMap<>();
        for (String key : rawMap.keySet()){
            if (!key.contains("#")){
                ResourceLocation blockLocation = new ResourceLocation(key);
                finalMap.put(blockLocation,rawMap.get(key));
            }
        }
        return finalMap;
    }


    public record PartialMassMap(boolean replace, Map<String, Integer> massMap){

    }
}
