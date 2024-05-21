package com.rae.creatingspace.server.design;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.HashMap;
import java.util.Map;

public class PropellantType {
    Map<TagKey<Fluid>, Float> propellantRatio;
    Integer maxISP;
    //for codec use MiscInit.PROPELLANT_TYPE.get().getCodec()

    public PropellantType(Map<TagKey<Fluid>, Float> propellantRatio, Integer maxISP) {
        this.propellantRatio = normalise(propellantRatio);
        this.maxISP = maxISP;
    }

    private static Map<TagKey<Fluid>, Float> normalise(Map<TagKey<Fluid>, Float> propellantConsumptions) {
        Float total = 0f;
        HashMap<TagKey<Fluid>, Float> newMap = new HashMap<>(propellantConsumptions);
        for (float value :
                propellantConsumptions.values()) {
            total += value;
        }
        for (TagKey<Fluid> key : propellantConsumptions.keySet()) {
            newMap.put(key, propellantConsumptions.get(key) / total);
        }
        return newMap;
    }
    @Override
    public String toString() {
        return "PropellantType{" +
                "propellantRatio=" + propellantRatio +
                ", maxISP=" + maxISP +
                '}';
    }

    public Map<TagKey<Fluid>, Float> getPropellantRatio() {
        return propellantRatio;
    }

    public Integer getMaxISP() {
        return maxISP;
    }
}
