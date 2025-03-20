package com.rae.creatingspace.legacy.saved;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UnlockabledDesignSavedData extends SavedData {

    private static final Codec<Map<String, List<ResourceLocation>>> CODEC = Codec.unboundedMap(Codec.STRING, Codec.list(ResourceLocation.CODEC));
    public HashMap<String, List<ResourceLocation>> unlockedExhaustType = new HashMap<>();
    public HashMap<String, List<ResourceLocation>> unlockedPowerPackType = new HashMap<>();

    public UnlockabledDesignSavedData() {
    }

    public List<ResourceLocation> getExhausts(UUID playerId) {
        return unlockedExhaustType.get(playerId.toString());
    }
    @Override
    public @NotNull CompoundTag save(CompoundTag nbt) {
        nbt.put("unlocked_exhaust_type", CODEC.encodeStart(NbtOps.INSTANCE, unlockedExhaustType)
                .result().orElse(new CompoundTag()));
        nbt.put("unlocked_power_pack_type", CODEC.encodeStart(NbtOps.INSTANCE, unlockedPowerPackType)
                .result().orElse(new CompoundTag()));
        return nbt;
    }

    public static UnlockabledDesignSavedData load(CompoundTag nbt) {
        UnlockabledDesignSavedData savedData = new UnlockabledDesignSavedData();
        savedData.unlockedExhaustType = new HashMap<>(CODEC.parse(NbtOps.INSTANCE, nbt.get("unlocked_exhaust_type"))
                .result().orElse(new HashMap<>()));
        savedData.unlockedPowerPackType = new HashMap<>(CODEC.parse(NbtOps.INSTANCE, nbt.get("unlocked_power_pack_type"))
                .result().orElse(new HashMap<>()));
        return savedData;
    }

    public static UnlockabledDesignSavedData loadData(MinecraftServer server) {
        return server.overworld()
                .getDataStorage()
                .computeIfAbsent(UnlockabledDesignSavedData::load, UnlockabledDesignSavedData::new, "unlocked_designs");
    }

}
