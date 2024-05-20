package com.rae.creatingspace.server.design;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.MiscInit;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;

public class ExhaustPackType {
    float designEfficiency;
    ResourceLocation id;

    public static final Codec<ExhaustPackType> DIRECT_CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            Codec.FLOAT.fieldOf("designEfficiency").forGetter(i -> i.designEfficiency),
                            ResourceLocation.CODEC.fieldOf("id").forGetter(i -> i.id)
                    ).apply(instance, ExhaustPackType::new)
    );

    public ExhaustPackType(float designEfficiency, ResourceLocation id) {
        this.designEfficiency = designEfficiency;
        this.id = id;
    }

    public static ExhaustPackType fromLocation(ResourceLocation location) {
        return RegistryObject.create(location, MiscInit.DEFERRED_EXHAUST_PACK_TYPE.getRegistryKey(),
                CreatingSpace.MODID).get();
    }

    public static ResourceLocation toLocation(ExhaustPackType exhaustPackType) {
        return exhaustPackType.id;
    }
}
