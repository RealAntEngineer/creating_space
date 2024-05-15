package com.rae.creatingspace.saved;

import com.rae.creatingspace.server.design.ExhaustPackType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnlockedDesignManager {
    public Map<String, List<ExhaustPackType>> unlockedExhaustType = new HashMap<>();

    public void addExhaustForPlayer(Player player, ExhaustPackType exhaustPackType) {
        if (player instanceof ServerPlayer serverPlayer) {
            UnlockabledDesignSavedData savedData = UnlockabledDesignSavedData.loadData(serverPlayer.getServer());
            unlockedExhaustType = savedData.unlockedExhaustType;
            if (!unlockedExhaustType.containsKey(serverPlayer.getStringUUID())) {
                unlockedExhaustType.put(serverPlayer.getStringUUID(), new ArrayList<>());
                savedData.setDirty();
            }
            if (unlockedExhaustType.get(serverPlayer.getStringUUID()).contains(exhaustPackType)) {
                unlockedExhaustType.get(serverPlayer.getStringUUID()).add(exhaustPackType);
            }
        }
    }

    public void playerLogin(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            UnlockabledDesignSavedData savedData = UnlockabledDesignSavedData.loadData(serverPlayer.getServer());
            unlockedExhaustType = savedData.unlockedExhaustType;
            if (!unlockedExhaustType.containsKey(serverPlayer.getStringUUID())) {
                unlockedExhaustType.put(serverPlayer.getStringUUID(), new ArrayList<>());
                savedData.setDirty();
            }
        }
    }

    public void levelLoaded(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null || server.overworld() != level)
            return;
        this.unlockedExhaustType = UnlockabledDesignSavedData.loadData(server).unlockedExhaustType;
    }

}
