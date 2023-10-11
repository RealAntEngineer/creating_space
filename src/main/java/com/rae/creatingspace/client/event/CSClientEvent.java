package com.rae.creatingspace.client.event;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.client.gui.RemainingO2Overlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(value = Dist.CLIENT)

public class CSClientEvent {

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            // Register overlays
            event.registerAbove(VanillaGuiOverlay.HELMET.id(), "remaining_oxygen", RemainingO2Overlay.INSTANCE);

        }
    }
}
