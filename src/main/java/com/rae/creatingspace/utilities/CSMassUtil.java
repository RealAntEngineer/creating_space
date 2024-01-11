package com.rae.creatingspace.utilities;

import com.rae.creatingspace.utilities.data.MassOfBlockReader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class CSMassUtil {

    public static int mass(BlockState state){
        MassOfBlockReader.PartialMassMap data = MassOfBlockReader.MASS_HOLDER.getData();
        if( data!=null){
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            Map<TagKey<Block>, Integer> massOfTaggedBlocks = MassOfBlockReader.getOnlyTags(data);
            Map<ResourceLocation, Integer> massOfBlocks = MassOfBlockReader.getWithoutTags(data);
            Integer mass = massOfBlocks.get(id);
            if (mass!=null){
                return mass;
            }
            else {
                for (TagKey<Block> tagKey : state.getTags().toList()) {
                    mass = massOfTaggedBlocks.get(tagKey);
                    if (mass != null) {
                        return mass;
                    }
                }
            }
        }
        return 1000;
    }


}
