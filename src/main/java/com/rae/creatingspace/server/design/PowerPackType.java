package com.rae.creatingspace.server.design;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class PowerPackType {
    public float getCombustionEfficiency() {
        return combustionEfficiency;
    }

    float combustionEfficiency;
    List<ResourceLocation> forbiddenPropellant;

    public PowerPackType(float combustionEfficiency, List<ResourceLocation> forbiddenPropellant) {
        this.combustionEfficiency = combustionEfficiency;
        this.forbiddenPropellant = forbiddenPropellant;
    }
}
