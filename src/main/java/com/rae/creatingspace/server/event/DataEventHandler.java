package com.rae.creatingspace.server.event;

import com.rae.creatingspace.CreatingSpace;
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
        CreatingSpace.DESIGN_SAVED_DATA.playerLogin(player);
    }

    /*@SubscribeEvent
    public static void onCreatingRegistry(NewRegistryEvent event){
        event.create(new RegistryBuilder<PowerPackType>().setName(MiscInit.Keys.POWER_PACK_TYPE.location()).disableSaving()
                .allowModification().dataPackRegistry(PowerPackType.DIRECT_CODEC, PowerPackType.DIRECT_CODEC));
    }*/
    /*@SubscribeEvent
    public static void onSeverStarted(ServerStartedEvent event){
        MinecraftServer server = event.getServer();
        final RegistryAccess registries = server.registryAccess();
        registries.registryOrThrow(MiscInit.Keys.POWER_PACK_TYPE).entrySet().forEach(
                resourceKeyPowerPackTypeEntry -> {
                    System.out.println(resourceKeyPowerPackTypeEntry.getKey());
                }
        );
    }*/
}