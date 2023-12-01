package com.rae.creatingspace.utilities.data.codec;

import com.rae.creatingspace.server.contraption.RocketContraption;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;

public class CSNBTUtil {
    //for propellant consumption -> make documentation + need to make codec for those data structures
    public static HashMap<Couple<TagKey<Fluid>>, Couple<Float>> fromNBTtoMapCouple(CompoundTag partialDrainAmountPerFluid) {
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

            returnedMap.put(Couple.create(oxTag,fuelTag),new RocketContraption.ConsumptionInfo(coupleNBT.getFloat("oxConsumption"),coupleNBT.getFloat("oxConsumption"),coupleNBT.getInt("partialTrust")));

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
            infoNBT.putInt("partialTrust",consumptionInfo.partialTrust());
            returnNBT.put(stringCouple,infoNBT);

        }
        return returnNBT;
    }
}
