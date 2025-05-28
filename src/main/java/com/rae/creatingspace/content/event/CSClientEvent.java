package com.rae.creatingspace.content.event;

import com.rae.creatingspace.content.life_support.spacesuit.RemainingO2Overlay;
import com.rae.creatingspace.content.life_support.spacesuit.CopperOxygenBacktankFirstPersonRenderer;
import com.rae.creatingspace.content.life_support.spacesuit.NetheriteOxygenBacktankFirstPersonRenderer;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankArmorLayer;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.content.rocket.engine.table.EngineFabricationBlueprint;
import com.rae.creatingspace.content.rocket.engine.EngineItem;
import com.rae.creatingspace.init.EngineMaterialInit;
import com.rae.creatingspace.init.ingameobject.MaterialInit;
import com.simibubi.create.content.trains.CameraDistanceModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;


import java.util.List;

import static com.rae.creatingspace.content.rocket.engine.RocketEngineItem.appendEngineDependentText;


@EventBusSubscriber(value = Dist.CLIENT)
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

    @SubscribeEvent
    public static void addToItemTooltip(ItemTooltipEvent event) {
        if (event.getEntity() == null)
            return;

        ItemStack itemStack = event.getItemStack();
        List<Component> components = event.getToolTip();
        if (!(itemStack.getItem() instanceof EngineFabricationBlueprint || itemStack.getItem() instanceof EngineItem)) {
            CompoundTag recipeData = itemStack.getTagElement("engineRecipeData");
            try {
                if (recipeData != null) {
                    int size = recipeData.getInt("size");
                    int materialLevel = recipeData.getInt("materialLevel");
                    if (recipeData.contains("size")) components.add(Component.literal("size : " + size));
                    if (recipeData.contains("materialLevel")) components.add(Component.literal("materialLevel : " + EngineMaterialInit.materials.get(materialLevel)));
                    try {
                        ResourceLocation exhaustPackType = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, recipeData.get("exhaustPackType")).get().orThrow();
                        components.add(Component.translatable(exhaustPackType.toLanguageKey("exhaust_pack_type")));
                    } catch (Exception ignored) {
                    }
                    try {
                        ResourceLocation powerPackType = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, recipeData.get("powerPackType")).get().orThrow();
                        components.add(Component.translatable(powerPackType.toLanguageKey("power_pack_type")));
                    } catch (Exception ignored) {
                    }
                }
                CompoundTag engineInfo = itemStack.getTagElement("blockEntity");
                if (engineInfo != null) {
                    components.add(Component.literal("for engine :"));
                    appendEngineDependentText(components,engineInfo);
                }
            } catch (Exception ignored){

            }
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        //TODO look at Create's client events handler
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
