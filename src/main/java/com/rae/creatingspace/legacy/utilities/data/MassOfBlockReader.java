package com.rae.creatingspace.legacy.utilities.data;

import com.rae.colony_api.data.managers.FloatMapDataLoader;
import com.rae.creatingspace.CreatingSpace;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class MassOfBlockReader {


    public static final FloatMapDataLoader<Block> MASS_MAP =
            new FloatMapDataLoader<>(CreatingSpace.MODID, "blocks_mass", ForgeRegistries.BLOCKS.getRegistryKey());


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
