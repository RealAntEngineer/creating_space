package com.rae.creatingspace.api.design;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;

import java.util.List;

public class ExhaustPackType {
    float minExpansionRatio;
    float maxExpansionRatio;
    List<Couple<Integer>> slots;
    public static final Codec<List<Couple<Integer>>> SLOTS_CODEC = Codec.list(RecordCodecBuilder.create(
            coupleInstance ->
                    coupleInstance.group(
                            Codec.INT.fieldOf("x").forGetter(Pair::getFirst),
                            Codec.INT.fieldOf("y").forGetter(Pair::getSecond)
                    ).apply(coupleInstance, Couple::create)));
    public static final Codec<ExhaustPackType> DIRECT_CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            Codec.FLOAT.fieldOf("minExpansionRatio").forGetter(i -> i.minExpansionRatio),
                            Codec.FLOAT.fieldOf("maxExpansionRatio").forGetter(i -> i.maxExpansionRatio),
                            SLOTS_CODEC
                                    .fieldOf("slots")
                                    .forGetter(i -> i.slots)
                    ).apply(instance, ExhaustPackType::new));

    public ExhaustPackType(float minExpansionRatio, float maxExpansionRatio, List<Couple<Integer>> slots) {
        this.minExpansionRatio = minExpansionRatio;
        this.maxExpansionRatio = maxExpansionRatio;
        this.slots = slots;
    }

    public float getMinExpansionRatio() {
        return minExpansionRatio;
    }

    public float getMaxExpansionRatio() {
        return maxExpansionRatio;
    }

    /**
     * @param size           is the size of the combustion chamber in m3
     * @param expansionRatio is the ratio between the throat area and the exhaust area
     * @return the mass of the engine in Kg
     */
    public float getMass(float size, float expansionRatio) {
        return (expansionRatio / (maxExpansionRatio - minExpansionRatio) + 1) * size * 2000;
    }

    public List<Couple<Integer>> getSlots() {
        return slots;
    }

    @Override
    public String toString() {
        return "ExhaustPackType{" +
                "minExpansionRatio=" + minExpansionRatio +
                ", maxExpansionRatio=" + maxExpansionRatio +
                ", slots=" + slots +
                '}';
    }
}
