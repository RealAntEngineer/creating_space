package com.rae.creatingspace.content.life_support.spacesuit;

import com.rae.creatingspace.api.gui.elements.SliderWidget;
import com.rae.creatingspace.configs.CSConfigs;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

public class RemainingO2Overlay implements LayeredDraw.Layer {
    private final SliderWidget gauge;
    public static final RemainingO2Overlay INSTANCE = new RemainingO2Overlay();
    RemainingO2Overlay() {
        Minecraft mc = Minecraft.getInstance();
        gauge = new SliderWidget(CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getX(mc.getWindow().getScreenWidth()),
                CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getY(mc.getWindow().getScreenHeight()),
                32, 64, CSConfigs.CLIENT.oxygenBacktank.sliderColor.get().getColor());
    }
    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        LocalPlayer player = mc.player;
        if (player == null)
            return;

        ItemStack itemInChestSlot = player.getItemBySlot(EquipmentSlot.CHEST);

        if (itemInChestSlot.getItem() instanceof OxygenBacktankItem){
            CompoundTag tag = itemInChestSlot.getOrCreateTag();//TODO DataComponents
            float o2Value = tag.getFloat("Oxygen");

            gauge.setMax(OxygenBacktankUtil.maxOxygen(itemInChestSlot));
            gauge.setChase((int) o2Value);
            gauge.render(graphics, (int) mc.mouseHandler.xpos(),(int) mc.mouseHandler.ypos() ,deltaTracker.getRealtimeDeltaTicks());

        }
    }
}
