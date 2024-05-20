package com.rae.creatingspace.server.design;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.Map;

public class PropellantType {
    Map<TagKey<Fluid>, Float> propellantConsumptions;
    Integer maxISP;

    Codec<PropellantType> DIREC_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(TagKey.codec(Registry.FLUID_REGISTRY), Codec.FLOAT).fieldOf("propellantConsumptions").forGetter(i -> i.propellantConsumptions),
            Codec.INT.fieldOf("maxISP").forGetter(i -> i.maxISP)
    ).apply(instance, PropellantType::new));

    public PropellantType(Map<TagKey<Fluid>, Float> propellantConsumptions, Integer maxISP) {
        this.propellantConsumptions = propellantConsumptions;
        this.maxISP = maxISP;
    }

    @Override
    public String toString() {
        return "PropellantType{" +
                "propellantConsumptions=" + propellantConsumptions +
                ", maxISP=" + maxISP +
                '}';
    }

    public Map<TagKey<Fluid>, Float> getPropellantConsumptions() {
        return propellantConsumptions;
    }

    public Integer getMaxISP() {
        return maxISP;
    }
}
