package com.rae.creatingspace.legacy.saved;

import com.mojang.serialization.Codec;
import com.rae.creatingspace.CreatingSpace;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PlayerUnlockedDesign {
    private static final Codec<List<ResourceLocation>> CODEC = Codec.list(ResourceLocation.CODEC);

    List<ResourceLocation> unlockedExhaustType = new ArrayList<>(List.of(CreatingSpace.resource("bell_nozzle")));
    List<ResourceLocation> unlockedPowerPackType = new ArrayList<>(List.of(CreatingSpace.resource("open_cycle")));


    public List<ResourceLocation> getUnlockedExhaustType() {
        return unlockedExhaustType;
    }

    public List<ResourceLocation> getUnlockedPowerPackType() {
        return unlockedPowerPackType;
    }

    public void addUnlockedExhaustType(ResourceLocation location){
        unlockedExhaustType.add(location);
    }
    public void addUnlockedPowerPackType(ResourceLocation location){
        unlockedPowerPackType.add(location);
    }
    public void copyFrom(PlayerUnlockedDesign pUD){
        this.unlockedExhaustType = pUD.unlockedExhaustType;
        this.unlockedPowerPackType = pUD.unlockedPowerPackType;
    }
    public void save(CompoundTag nbt) {
        nbt.put("unlocked_exhaust_type", CODEC.encodeStart(NbtOps.INSTANCE, unlockedExhaustType)
                .result().orElse(new CompoundTag()));
        nbt.put("unlocked_power_pack_type", CODEC.encodeStart(NbtOps.INSTANCE, unlockedPowerPackType)
                .result().orElse(new CompoundTag()));
    }
    public void load(CompoundTag nbt) {
        unlockedExhaustType = new ArrayList<>(CODEC.parse(NbtOps.INSTANCE, nbt.get("unlocked_exhaust_type"))
                .result().orElse(new ArrayList<>()));
        unlockedPowerPackType = new ArrayList<>(CODEC.parse(NbtOps.INSTANCE, nbt.get("unlocked_power_pack_type"))
                .result().orElse(new ArrayList<>()));
    }
}
