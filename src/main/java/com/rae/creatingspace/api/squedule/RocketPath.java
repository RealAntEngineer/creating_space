package com.rae.creatingspace.api.squedule;

import net.minecraft.resources.ResourceLocation;

public class RocketPath {
    public ResourceLocation origin;
    public ResourceLocation destination;
    public double cost;

    public RocketPath(ResourceLocation origin, ResourceLocation destination, double cost) {
        this.origin = origin;
        this.destination = destination;
        this.cost = cost;
    }
}
