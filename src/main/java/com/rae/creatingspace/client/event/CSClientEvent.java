package com.rae.creatingspace.client.event;

import com.rae.creatingspace.client.gui.RemainingO2Overlay;
import com.rae.creatingspace.client.renderer.CopperOxygenBacktankFirstPersonRenderer;
import com.rae.creatingspace.client.renderer.NetheriteOxygenBacktankFirstPersonRenderer;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.server.armor.OxygenBacktankArmorLayer;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.simibubi.create.content.trains.CameraDistanceModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
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
    public static void onMount(EntityMountEvent event) {
        if (event.getEntityMounting() == Minecraft.getInstance().player && event.isMounting() && (event.getEntityBeingMounted() instanceof RocketContraptionEntity rocketContraption)) {
            CameraDistanceModifier.zoomOut((float) (rocketContraption.getBoundingBox().getSize() * CSConfigs.CLIENT.zoomOut.get()));
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
