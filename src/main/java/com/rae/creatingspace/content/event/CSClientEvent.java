package com.rae.creatingspace.content.event;

import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.content.life_support.spacesuit.RemainingO2Overlay;
import com.rae.creatingspace.content.life_support.spacesuit.CopperOxygenBacktankFirstPersonRenderer;
import com.rae.creatingspace.content.life_support.spacesuit.NetheriteOxygenBacktankFirstPersonRenderer;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankArmorLayer;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.content.rocket.engine.table.EngineFabricationBlueprint;
import com.rae.creatingspace.content.rocket.engine.EngineItem;
import com.rae.creatingspace.content.rocket.engine.RocketEngineItem;
import com.simibubi.create.content.trains.CameraDistanceModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;


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

    @SubscribeEvent
    public static void addToItemTooltip(ItemTooltipEvent event) {
        if (event.getEntity() == null)
            return;

        ItemStack itemStack = event.getItemStack();
        List<Component> components = event.getToolTip();
        if (!(itemStack.getItem() instanceof EngineFabricationBlueprint || itemStack.getItem() instanceof EngineItem)) {
            CompoundTag recipeData = itemStack.getTagElement("engineRecipeData");
            if (recipeData != null) {
                int size = recipeData.getInt("size");
                int materialLevel = recipeData.getInt("materialLevel");
                components.add(Component.literal("size : " + size));
                components.add(Component.literal("materialLevel : " + materialLevel));
                try {
                    ResourceLocation exhaustPackType = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, recipeData.get("exhaustPackType")).get().orThrow();
                    ResourceLocation powerPackType = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, recipeData.get("powerPackType")).get().orThrow();

                    components.add(Component.translatable(exhaustPackType.toLanguageKey("exhaust_pack_type")));
                    components.add(Component.translatable(powerPackType.toLanguageKey("power_pack_type")));
                } catch (Exception ignored) {
                }
            }
            CompoundTag engineInfo = itemStack.getTagElement("blockEntity");
            if (engineInfo != null) {
                components.add(Component.literal("for engine :"));
                //TODO there is a need for a description : size and type of exhaustPack + powerPack + material level
                PropellantType propellantType = PropellantTypeInit.getSyncedPropellantRegistry().getOptional(
                        ResourceLocation.CODEC.parse(NbtOps.INSTANCE, engineInfo.get("propellantType"))
                                .resultOrPartial(s -> {
                                }).orElse(PropellantTypeInit.METHALOX.getId())).orElseThrow();
                RocketEngineItem.appendEngineDependentText(components, propellantType, (int) (propellantType.getMaxISP() * engineInfo.getFloat("efficiency")), engineInfo.getInt("mass"), engineInfo.getInt("thrust"));
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
