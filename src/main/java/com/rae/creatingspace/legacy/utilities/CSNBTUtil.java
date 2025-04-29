package com.rae.creatingspace.legacy.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSNBTUtil {
    // for propellant consumption -> make documentation + need to make codec for those data structures
    // there is a lot of code duplication -> remove it
    //TODO replace by Codec
    public static HashMap<TagKey<Fluid>, Float> fromNBTtoMapFluidTagsFloat(CompoundTag perTagFluidMap) {
        HashMap<TagKey<Fluid>, Float> returnedMap = new HashMap<>();
        for (String stringCouple:perTagFluidMap.getAllKeys()){
            Float integerValue = perTagFluidMap.getFloat(stringCouple);
            String stringTag = stringCouple
                    .replace("(","")
                    .replace(")","")
                    .replace("TagKey[","")
                    .replace("]","");

            TagKey<Fluid> fluidTagKey = FluidTags.create(new ResourceLocation(stringTag.split(" / ")[1]));

            returnedMap.put(fluidTagKey,integerValue);

        }
        return returnedMap;
    }

    public static CompoundTag fromMapFluidTagsFloatToNBT(HashMap<TagKey<Fluid>, Float> map) {
        CompoundTag returnedMap = new CompoundTag();
        for (TagKey<Fluid> fluidTagKey:map.keySet()){
            Float integerValue = map.get(fluidTagKey);
            String stringTag = fluidTagKey.toString();

            returnedMap.putFloat(stringTag,integerValue);

        }
        return returnedMap;
    }

    public static HashMap<TagKey<Fluid>, Integer> fromNBTtoMapFluidTagsInteger(CompoundTag perTagFluidMap) {
        HashMap<TagKey<Fluid>, Integer> returnedMap = new HashMap<>();
        for (String stringCouple:perTagFluidMap.getAllKeys()){
            Integer integerValue = perTagFluidMap.getInt(stringCouple);
            String stringTag = stringCouple
                    .replace("(","")
                    .replace(")","")
                    .replace("TagKey[","")
                    .replace("]","");

            TagKey<Fluid> fluidTagKey = FluidTags.create(new ResourceLocation(stringTag.split(" / ")[1]));

            returnedMap.put(fluidTagKey,integerValue);

        }
        return returnedMap;
    }

    public static CompoundTag fromMapFluidTagsIntegerToNBT(HashMap<TagKey<Fluid>, Integer> map) {
        CompoundTag returnedMap = new CompoundTag();
        for (TagKey<Fluid> fluidTagKey:map.keySet()){
            Integer integerValue = map.get(fluidTagKey);
            String stringTag = fluidTagKey.toString();

            returnedMap.putInt(stringTag,integerValue);

        }
        return returnedMap;
    }
    public static ArrayList<Long> BlockPosToLong(List<BlockPos> blockPosList){
        ArrayList<Long> longs = new ArrayList<>();
        for (BlockPos pos: blockPosList){
            longs.add(pos.asLong());
        }
        return longs;
    }

    public static List<BlockPos> LongsToBlockPos(long[] localPosOfFlightRecorders) {
        ArrayList<BlockPos> blockPosList = new ArrayList<>();
        for (Long compressedPos:localPosOfFlightRecorders){
            blockPosList.add(BlockPos.of(compressedPos));
        }
        return blockPosList;
    }
}
