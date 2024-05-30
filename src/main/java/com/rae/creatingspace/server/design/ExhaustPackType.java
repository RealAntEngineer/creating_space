package com.rae.creatingspace.server.design;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.utility.Couple;

import java.util.ArrayList;
import java.util.List;

public class ExhaustPackType {
    float minExpansionRatio;
    float maxExpansionRatio;
    List<Couple<Integer>> slots = new ArrayList<>();
    public static final Codec<ExhaustPackType> DIRECT_CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            Codec.FLOAT.fieldOf("minExpansionRatio").forGetter(i -> i.minExpansionRatio),
                            Codec.FLOAT.fieldOf("maxExpansionRatio").forGetter(i -> i.maxExpansionRatio)
                    ).apply(instance, (Float minExpansionRatio1, Float maxExpansionRatio1) -> new ExhaustPackType(minExpansionRatio1, maxExpansionRatio1))
    );

    public ExhaustPackType(float minExpansionRatio, float maxExpansionRatio) {
        this.minExpansionRatio = minExpansionRatio;
        this.maxExpansionRatio = maxExpansionRatio;
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
}
