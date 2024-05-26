package com.rae.creatingspace.server.design;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class ExhaustPackType {
    float minExpansionRatio;
    float maxExpansionRatio;
    ResourceLocation id;

    public static final Codec<ExhaustPackType> DIRECT_CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            Codec.FLOAT.fieldOf("minExpansionRatio").forGetter(i -> i.minExpansionRatio),
                            Codec.FLOAT.fieldOf("maxExpansionRatio").forGetter(i -> i.maxExpansionRatio),
                            ResourceLocation.CODEC.fieldOf("id").forGetter(i -> i.id)
                    ).apply(instance, ExhaustPackType::new)
    );

    public ExhaustPackType(float minExpansionRatio, float maxExpansionRatio, ResourceLocation id) {
        this.minExpansionRatio = minExpansionRatio;
        this.maxExpansionRatio = maxExpansionRatio;
        this.id = id;
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
