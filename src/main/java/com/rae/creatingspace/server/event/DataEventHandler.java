package com.rae.creatingspace.server.event;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CreatingSpace.MODID)
public class DataEventHandler {
    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        CreatingSpace.DESIGN_SAVED_DATA.levelLoaded(world);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        // Send a message to the player
        player.displayClientMessage(Component.literal("Welcome to the server!"), true);
        CreatingSpace.DESIGN_SAVED_DATA.playerLogin(player);
    }
}