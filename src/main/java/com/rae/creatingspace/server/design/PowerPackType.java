package com.rae.creatingspace.server.design;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

public class PowerPackType {
    float designEfficiency;
    List<TagKey<Fluid>> possibleOxidizer;
    List<TagKey<Fluid>> possibleFuel;


    public PowerPackType(float designEfficiency, List<TagKey<Fluid>> possibleOxidizer, List<TagKey<Fluid>> possibleFuel) {
        this.designEfficiency = designEfficiency;
        this.possibleOxidizer = possibleOxidizer;
        this.possibleFuel = possibleFuel;
    }
}
