package com.rae.colony_api.thermal_utilities;

import net.minecraft.nbt.CompoundTag;

public record SpecificRealGazState(Float temperature, Float pressure, Float specificEnthalpy, Float vaporQuality) {
    /*public Codec<SpecificRealGazState> CODEC = RecordCodecBuilder.create(

    );*/

    public SpecificRealGazState(Float temperature, Float pressure, Float specificEnthalpy, Float vaporQuality){
        this.temperature = Math.max(0,temperature);
        this.pressure = Math.max(0,pressure);
        this.specificEnthalpy = specificEnthalpy;
        if (vaporQuality > 1){
            //System.out.println("vapor quality > 1 given, check your code");
        }
        this.vaporQuality = Math.max(0,Math.min(vaporQuality,1));
    }
    public SpecificRealGazState(CompoundTag tag){
        this(tag.getFloat("temperature"), tag.getFloat("pressure"), tag.getFloat("specific_enthalpy"), tag.getFloat("vapor_quality"));
    }
    @Override
    public String toString() {
        return "SpecificRealGazState{" +
                "temperature=" + temperature +
                ", pressure=" + pressure +
                ", specific_enthalpy=" + specificEnthalpy +
                ", vaporQuality=" + vaporQuality +
                '}';
    }

    public CompoundTag serialize(){
        CompoundTag tag = new CompoundTag();
        tag.putFloat("temperature",temperature);
        tag.putFloat("pressure",pressure);
        tag.putFloat("specific_enthalpy", specificEnthalpy);
        tag.putFloat("vapor_quality",vaporQuality);
        return tag;
    }

}
