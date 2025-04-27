package com.rae.creatingspace.api.squedule;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class RocketPath {
    public ResourceLocation origin;
    public ResourceLocation destination;
    public Vec2 XZCoord;
    public double cost;

    public RocketPath(ResourceLocation origin, ResourceLocation destinationPlanet,Vec2 XZCoord, double cost) {
        this.origin = origin;
        this.destination = destinationPlanet;
        this.cost = cost;
        this.XZCoord = XZCoord;
    }

    public static RocketPath parse(CompoundTag nextPath) {
        if (nextPath.isEmpty()) return null;
        return new RocketPath(ResourceLocation.tryParse(nextPath.getString("origin")),ResourceLocation.tryParse(nextPath.getString("path")),
                new Vec2(nextPath.getInt("XCoord"),nextPath.getInt("ZCoord")),nextPath.getDouble("cost"));
    }

    public CompoundTag serialize() {
        CompoundTag nextPath = new CompoundTag();
        nextPath.putString("origin",origin.toString());
        nextPath.putString("destination",destination.toString());
        nextPath.putInt("XCoord", (int) XZCoord.x);
        nextPath.putInt("ZCoord", (int) XZCoord.y);
        nextPath.putDouble("cost",cost);
        return nextPath;
    }
}
