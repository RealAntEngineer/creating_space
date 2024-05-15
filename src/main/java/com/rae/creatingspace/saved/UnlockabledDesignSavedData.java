package com.rae.creatingspace.saved;

import com.mojang.serialization.Codec;
import com.rae.creatingspace.server.design.ExhaustPackType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UnlockabledDesignSavedData extends SavedData {

    private static final Codec<Map<String, List<ExhaustPackType>>> EXHAUST_CODEC = Codec.unboundedMap(Codec.STRING, Codec.list(ExhaustPackType.DIRECT_CODEC));
    public Map<String, List<ExhaustPackType>> unlockedExhaustType = new HashMap<>();

    public UnlockabledDesignSavedData() {
    }

    public List<ExhaustPackType> getExhausts(UUID playerId) {
        return unlockedExhaustType.get(playerId.toString());
    }

    public void addToUnlocked(UUID playerId, ExhaustPackType type) {
        unlockedExhaustType.get(playerId.toString()).add(type);
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.put("unlocked_exhaust_type", EXHAUST_CODEC.encodeStart(NbtOps.INSTANCE, unlockedExhaustType)
                .result().orElse(new CompoundTag()));
        return nbt;
    }

    public static UnlockabledDesignSavedData load(CompoundTag nbt) {
        UnlockabledDesignSavedData savedData = new UnlockabledDesignSavedData();
        savedData.unlockedExhaustType = EXHAUST_CODEC.parse(NbtOps.INSTANCE, nbt.get("unlocked_exhaust_type"))
                .result().orElse(new HashMap<>());
        return savedData;
    }

    public static UnlockabledDesignSavedData loadData(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(UnlockabledDesignSavedData::load, UnlockabledDesignSavedData::new, "unlocked_designs");
    }

}
