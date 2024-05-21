package com.rae.creatingspace.server.design;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PowerPackType {
    float designEfficiency;
    List<ResourceLocation> forbiddenPropellant;

    public PowerPackType(float designEfficiency, List<ResourceLocation> forbiddenPropellant) {
        this.designEfficiency = designEfficiency;
        this.forbiddenPropellant = forbiddenPropellant;
    }
}
