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
    //replace by Codec
    /*public static HashMap<Couple<TagKey<Fluid>>, Couple<Float>> fromNBTtoMapCouple(CompoundTag partialDrainAmountPerFluid) {
        HashMap<Couple<TagKey<Fluid>>, Couple<Float>> returnedMap = new HashMap<>();
        for (String stringCouple:partialDrainAmountPerFluid.getAllKeys()){
            CompoundTag coupleNBT = partialDrainAmountPerFluid.getCompound(stringCouple);
            String[] stringTags = stringCouple
                    .replace("(","")
                    .replace(")","")
                    .replace("TagKey[","")
                    .replace("]","").split(", ");

            TagKey<Fluid> oxTag = FluidTags.create(new ResourceLocation(stringTags[0].split(" / ")[1]));
            TagKey<Fluid> fuelTag = FluidTags.create(new ResourceLocation(stringTags[1].split(" / ")[1]));

            returnedMap.put(Couple.create(oxTag,fuelTag),Couple.create(coupleNBT.getFloat("first"),coupleNBT.getFloat("last")));

        }
        return returnedMap;
    }

    public static HashMap<Couple<TagKey<Fluid>>, RocketContraption.ConsumptionInfo> fromNBTtoMapInfo(CompoundTag perTagFluidConsumption) {
        HashMap<Couple<TagKey<Fluid>>, RocketContraption.ConsumptionInfo> returnedMap = new HashMap<>();
        for (String stringCouple:perTagFluidConsumption.getAllKeys()){
            CompoundTag coupleNBT = perTagFluidConsumption.getCompound(stringCouple);
            String[] stringTags = stringCouple
                    .replace("(","")
                    .replace(")","")
                    .replace("TagKey[","")
                    .replace("]","").split(", ");

            TagKey<Fluid> oxTag = FluidTags.create(new ResourceLocation(stringTags[0].split(" / ")[1]));
            TagKey<Fluid> fuelTag = FluidTags.create(new ResourceLocation(stringTags[1].split(" / ")[1]));

            returnedMap.put(Couple.create(oxTag,fuelTag),new RocketContraption.ConsumptionInfo(coupleNBT.getFloat("oxConsumption"),coupleNBT.getFloat("oxConsumption"),coupleNBT.getInt("partialThrust")));

        }
        return returnedMap;
    }

    public static CompoundTag fromMapCoupleToNBT(HashMap<Couple<TagKey<Fluid>>, Couple<Float>> partialDrainAmountPerFluid) {
        CompoundTag returnNBT = new CompoundTag();
        for (Couple<TagKey<Fluid>> combination:partialDrainAmountPerFluid.keySet()){
            String stringCouple = combination.toString();
            Couple<Float> floatCouple = partialDrainAmountPerFluid.get(combination);
            CompoundTag coupleNBT = new CompoundTag();
            coupleNBT.putFloat("first",floatCouple.get(true));
            coupleNBT.putFloat("last",floatCouple.get(false));

            returnNBT.put(stringCouple,coupleNBT);

        }
        return returnNBT;
    }

    public static CompoundTag fromMapInfoToNBT(HashMap<Couple<TagKey<Fluid>>, RocketContraption.ConsumptionInfo> perTagFluidConsumption) {
        CompoundTag returnNBT = new CompoundTag();
        for (Couple<TagKey<Fluid>> combination:perTagFluidConsumption.keySet()){
            String stringCouple = combination.toString();
            RocketContraption.ConsumptionInfo consumptionInfo = perTagFluidConsumption.get(combination);
            CompoundTag infoNBT = new CompoundTag();
            infoNBT.putFloat("oxConsumption",consumptionInfo.oxConsumption());
            infoNBT.putFloat("fuelConsumption",consumptionInfo.fuelConsumption());
            infoNBT.putInt("partialThrust",consumptionInfo.partialThrust());
            returnNBT.put(stringCouple,infoNBT);

        }
        return returnNBT;
    }
*/
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
