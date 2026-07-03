package com.rae.creatingspace.content.event;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.life_support.spacesuit.RemainingO2Overlay;
import com.rae.creatingspace.content.life_support.spacesuit.CopperOxygenBacktankFirstPersonRenderer;
import com.rae.creatingspace.content.life_support.spacesuit.NetheriteOxygenBacktankFirstPersonRenderer;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.life_support.spacesuit.OxygenBacktankArmorLayer;
import com.rae.creatingspace.content.rocket.contraption.entity.RocketContraptionEntity;
import com.rae.creatingspace.content.rocket.engine.table.EngineFabricationBlueprint;
import com.rae.creatingspace.content.rocket.engine.EngineItem;
import com.rae.creatingspace.init.EngineMaterialInit;
import com.simibubi.create.content.trains.CameraDistanceModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

import static com.rae.creatingspace.content.rocket.engine.RocketEngineItem.appendEngineDependentText;


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
            try {
                if (recipeData != null && !recipeData.isEmpty()) {
                    components.add(Component.literal("Recipe Information :").withStyle(ChatFormatting.GOLD));

                    int size          = recipeData.getInt("size");
                    int materialLevel = recipeData.getInt("materialLevel");
                    if (recipeData.contains("size")) components.add(Component.literal("  Size : " + size).withStyle(ChatFormatting.GRAY));

                    if (recipeData.contains("materialLevel"))
                        components.add(Component.literal("  Material Level : " + EngineMaterialInit.materialNames.get(materialLevel)).withStyle(ChatFormatting.GRAY));
                    ResourceLocation powerPackType        = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, recipeData.get("powerPackType")).result().orElse(null);
                    ResourceLocation exhaustPackType      = ResourceLocation.CODEC.parse(NbtOps.INSTANCE, recipeData.get("exhaustPackType")).result().orElse(null);
                    MutableComponent powerPackComponent   = powerPackType != null ? Component.translatable(powerPackType.toLanguageKey("power_pack_type")) : Component.literal("not defined");
                    MutableComponent exhaustPackComponent = exhaustPackType != null ? Component.translatable(exhaustPackType.toLanguageKey("exhaust_pack_type")) : Component.literal("not defined");

                    components.add(Component.literal("  Technology : ").append(powerPackComponent).append(" | ").append(exhaustPackComponent).withStyle(ChatFormatting.GRAY));

                }
                CompoundTag engineInfo = itemStack.getTagElement("blockEntity");
                if (engineInfo!=null && !engineInfo.isEmpty()) {
                    components.add(Component.literal("For engine :").withStyle(ChatFormatting.GOLD));
                    appendEngineDependentText(components, "  ", engineInfo);
                }
            } catch (Exception exception) {
                CreatingSpace.LOGGER.error("caught exception during tooltip :", exception);
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
