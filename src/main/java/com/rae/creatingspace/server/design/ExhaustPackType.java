package com.rae.creatingspace.server.design;

import com.mojang.serialization.Codec;
import com.rae.creatingspace.init.MiscInit;
import net.minecraft.resources.ResourceLocation;

public class ExhaustPackType {
    float designEfficiency;

    public static final Codec<ExhaustPackType> DIRECT_CODEC = ResourceLocation.CODEC.xmap(ExhaustPackType::fromLocation, ExhaustPackType::toLocation);

    public ExhaustPackType(float designEfficiency) {
        this.designEfficiency = designEfficiency;
    }

    public static ExhaustPackType fromLocation(ResourceLocation location) {
        return MiscInit.EXHAUST_PACK_TYPE.get().getDelegateOrThrow(location).value();
    }

    public static ResourceLocation toLocation(ExhaustPackType exhaustPackType) {
        return MiscInit.EXHAUST_PACK_TYPE.get().getDelegateOrThrow(exhaustPackType).key().location();
    }
}
