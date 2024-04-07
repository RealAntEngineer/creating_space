package com.rae.creatingspace.client.event;

import com.rae.creatingspace.client.gui.RemainingO2Overlay;
import com.rae.creatingspace.client.renderer.CopperOxygenBacktankFirstPersonRenderer;
import com.rae.creatingspace.client.renderer.NetheriteOxygenBacktankFirstPersonRenderer;
import com.rae.creatingspace.server.armor.OxygenBacktankArmorLayer;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class CSClientEvent {
    protected static boolean isGameActive() {
        return !(Minecraft.getInstance().level == null || Minecraft.getInstance().player == null);
    }
    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event){
        if (!isGameActive())
            return;
        CopperOxygenBacktankFirstPersonRenderer.clientTick();
        NetheriteOxygenBacktankFirstPersonRenderer.clientTick();
    }

    @SubscribeEvent
    public static void calculateRocketFovModifier(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();
        if (vehicle instanceof RocketContraptionEntity rocket) {
            CameraType cameraType = Minecraft.getInstance().options.getCameraType();
            if (!cameraType.isFirstPerson() && !cameraType.isMirrored()) {
                event.setNewFovModifier(event.getFovModifier() * 10);
            }
        }
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void addEntityRendererLayers(EntityRenderersEvent.AddLayers event) {
            EntityRenderDispatcher dispatcher = Minecraft.getInstance()
                    .getEntityRenderDispatcher();
            OxygenBacktankArmorLayer.registerOnAll(dispatcher);
        }
        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
            // Register overlays
            event.registerAbove(VanillaGuiOverlay.HELMET.id(), "remaining_oxygen", RemainingO2Overlay.INSTANCE);

        }
    }
}
