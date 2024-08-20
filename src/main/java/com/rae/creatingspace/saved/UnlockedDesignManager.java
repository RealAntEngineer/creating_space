package com.rae.creatingspace.saved;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnlockedDesignManager {
    private static UnlockabledDesignSavedData savedData;
    public static List<ResourceLocation> getExhaustUnlocked(Player player){
        return savedData.unlockedExhaustType.get(player.getStringUUID());
    }
    public static List<ResourceLocation> getPowerPackUnlocked(Player player){
        return savedData.unlockedPowerPackType.get(player.getStringUUID());
    }

    //register only the ResourceLocation not the design class
    public static void addExhaustForPlayer(Player player, ResourceLocation exhaustPackType) {
        if (player instanceof ServerPlayer serverPlayer) {
            savedData = UnlockabledDesignSavedData.loadData(serverPlayer.getServer());
            if (!savedData.unlockedExhaustType.containsKey(serverPlayer.getStringUUID())) {
                savedData.unlockedExhaustType.put(serverPlayer.getStringUUID(), new ArrayList<>(List.of(exhaustPackType)));
                savedData.setDirty();
            }
            //TODO cleanup
            if (!savedData.unlockedExhaustType.get(serverPlayer.getStringUUID()).contains(exhaustPackType)) {
                HashMap<String, List<ResourceLocation>> originalMap = new HashMap<>(savedData.unlockedExhaustType);
                ArrayList<ResourceLocation> newList = new ArrayList<>(savedData.unlockedExhaustType.get(serverPlayer.getStringUUID()));
                newList.add(exhaustPackType);
                originalMap.put(serverPlayer.getStringUUID(), newList);
                savedData.unlockedExhaustType = originalMap;
                savedData.setDirty();
            }
        }
    }

    public static void addPowerPackForPlayer(Player player, ResourceLocation powerPackType) {
        if (player instanceof ServerPlayer serverPlayer) {
            savedData = UnlockabledDesignSavedData.loadData(serverPlayer.getServer());
            if (!savedData.unlockedPowerPackType.containsKey(serverPlayer.getStringUUID())) {
                savedData.unlockedPowerPackType.put(serverPlayer.getStringUUID(), new ArrayList<>(List.of(powerPackType)));
                savedData.setDirty();
            }
            //TODO cleanup
            if (!savedData.unlockedPowerPackType.get(serverPlayer.getStringUUID()).contains(powerPackType)) {
                HashMap<String, List<ResourceLocation>> originalMap = new HashMap<>(savedData.unlockedPowerPackType);
                ArrayList<ResourceLocation> newList = new ArrayList<>(savedData.unlockedPowerPackType.get(serverPlayer.getStringUUID()));
                newList.add(powerPackType);
                originalMap.put(serverPlayer.getStringUUID(), newList);
                savedData.unlockedPowerPackType = originalMap;
                savedData.setDirty();
            }
        }
    }
    public static void playerLogin(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            savedData = UnlockabledDesignSavedData.loadData(serverPlayer.getServer());
            if (!savedData.unlockedExhaustType.containsKey(serverPlayer.getStringUUID())) {
                savedData.unlockedExhaustType.put(serverPlayer.getStringUUID(), new ArrayList<>());
                savedData.setDirty();
            }
        }
    }

    public static void levelLoaded(LevelAccessor level) {
        MinecraftServer server = level.getServer();
        if (server == null || server.overworld() != level)
            return;
        savedData = UnlockabledDesignSavedData.loadData(server);
    }

}
