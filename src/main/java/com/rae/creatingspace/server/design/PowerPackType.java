package com.rae.creatingspace.server.design;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.CreatingSpace;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PowerPackType {
    public float getCombustionEfficiency() {
        return combustionEfficiency;
    }

    float combustionEfficiency;

    public List<ResourceLocation> getAllowedPropellants() {
        return allowedPropellants;
    }

    List<ResourceLocation> allowedPropellants;
    public static final Codec<PowerPackType> DIRECT_CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            Codec.FLOAT.fieldOf("combustionEfficiency").forGetter(i -> i.combustionEfficiency),
                            Codec.list(ResourceLocation.CODEC).fieldOf("allowedPropellants").forGetter(i -> i.allowedPropellants)
                    ).apply(instance, PowerPackType::new)
    );

    public PowerPackType(float combustionEfficiency, List<ResourceLocation> allowedPropellants) {
        CreatingSpace.LOGGER.info("Loading a power pack" + allowedPropellants);
        this.combustionEfficiency = combustionEfficiency;
        this.allowedPropellants = allowedPropellants;
    }
}
