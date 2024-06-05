package com.rae.creatingspace.server.event;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = CreatingSpace.MODID)
public class DataEventHandler {
    //if it's null, then your server is distant and your on the client
    public static RegistryAccess.Frozen registryAccess = null;
    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        CreatingSpace.DESIGN_SAVED_DATA.levelLoaded(world);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        CreatingSpace.DESIGN_SAVED_DATA.playerLogin(player);
    }


    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        registryAccess = event.getServer().registryAccess();
    }

    /**
     * @param registryKey the registryKey for a common registry
     * @param <T>         the registry object type
     * @return the registry
     */
    public static <T> Registry<T> getSideAwareRegistry(ResourceKey<Registry<T>> registryKey) {
        if (registryAccess != null) {
            return registryAccess.ownedRegistry(registryKey).orElseThrow();
        } else {
            return Objects.requireNonNull(Minecraft.getInstance().getConnection())
                    .registryAccess().registry(registryKey)
                    .orElseThrow();
        }
    }
}