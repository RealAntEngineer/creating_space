package com.rae.creatingspace.content.event;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import com.rae.creatingspace.legacy.saved.UnlockedDesignManager;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = CreatingSpace.MODID)
public class DataEventHandler {
    //if it's null, then your server is distant and your on the client
    private static final Logger LOGGER = LogManager.getLogger();
    public static RegistryAccess.Frozen registryAccess = null;
    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        LevelAccessor world = event.getLevel();
        UnlockedDesignManager.levelLoaded(world);
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        UnlockedDesignManager.playerLogin(player);
        /*LOGGER.info("updating the travel map");
        CSDimensionUtil.updatePlanetsFromRegistry(getSideAwareRegistry(RocketAccessibleDimension.REGISTRY_KEY));
        LOGGER.info("updating the space travel cost map");
        CSDimensionUtil.updateCostMap();*/
    }
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        LOGGER.info("getting the server registry access");
        registryAccess = event.getServer().registryAccess();
        LOGGER.info("updating the travel map");
        CSDimensionUtil.updatePlanetsFromRegistry(registryAccess.registryOrThrow(RocketAccessibleDimension.REGISTRY_KEY));
        LOGGER.debug("updating the space travel cost map");
        CSDimensionUtil.updateCostMap();
        CSDimensionUtil.removeUnreachableDimensions();
    }


    //will get sent to FormicAPI since it's something that is general.
    /**
     * Utility function for getting a datapack driven registry from either the server or the client
     * @param registryKey the registryKey for a common registry
     * @param <T>         the registry object type
     * @return the registry
     */
    public static <T> Registry<T> getSideAwareRegistry(ResourceKey<Registry<T>> registryKey) {
        //TODO add side checks so the relatively fragile call to
        if (registryAccess != null) {
            return registryAccess.registry(registryKey)
                    .orElseThrow(() -> new IllegalStateException("Registry " + registryKey.location() + " not found"));
        } else {
            LOGGER.debug("Getting the registry access from the client");
            var connection = Minecraft.getInstance().getConnection();
            if (connection == null) {
                LOGGER.error("Minecraft client connection unavailable - registry not yet synced");
                throw new IllegalStateException("Cannot access registry " + registryKey.location() +
                        " before client connection is established. This typically means you're trying to " +
                        "access a datapack registry too early during initialization.");
            }
            return connection.registryAccess()
                    .registry(registryKey)
                    .orElseThrow(() -> new IllegalStateException("Registry " + registryKey.location() + " not found"));
        }
    }
}