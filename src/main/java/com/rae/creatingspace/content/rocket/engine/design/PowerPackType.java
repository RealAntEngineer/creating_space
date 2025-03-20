package com.rae.creatingspace.content.rocket.engine.design;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PowerPackType {
    float combustionEfficiency;
    int numberOfPumps;
    List<ResourceLocation> allowedPropellants;
    //List<Couple<Integer>> slots;
    public static final Codec<List<Couple<Integer>>> SLOTS_CODEC = Codec.list(RecordCodecBuilder.create(
            coupleInstance ->
                    coupleInstance.group(
                            Codec.INT.fieldOf("x").forGetter(Pair::getFirst),
                            Codec.INT.fieldOf("y").forGetter(Pair::getSecond)
                    ).apply(coupleInstance, Couple::create)));
    public static final Codec<PowerPackType> DIRECT_CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            Codec.FLOAT.fieldOf("combustionEfficiency").forGetter(i -> i.combustionEfficiency),
                            Codec.INT.fieldOf("numberOfPumps").forGetter(i -> i.numberOfPumps),
                            Codec.list(ResourceLocation.CODEC).fieldOf("allowedPropellants").forGetter(i -> i.allowedPropellants)
                            //SLOTS_CODEC.fieldOf("slots").forGetter(i -> i.slots)
                    ).apply(instance, PowerPackType::new)
    );

    public PowerPackType(float combustionEfficiency, int numberOfPumps, List<ResourceLocation> allowedPropellants) {//, List<Couple<Integer>> slots) {
        this.combustionEfficiency = combustionEfficiency;
        this.numberOfPumps = numberOfPumps;
        this.allowedPropellants = allowedPropellants;
        //this.slots = slots;
    }

    public List<ResourceLocation> getAllowedPropellants() {
        return allowedPropellants;
    }

    public float getCombustionEfficiency() {
        return combustionEfficiency;
    }


    /*public List<Couple<Integer>> getSlots() {
        return slots;
    }*/
}
