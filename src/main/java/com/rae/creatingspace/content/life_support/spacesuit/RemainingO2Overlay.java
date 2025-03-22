package com.rae.creatingspace.content.life_support.spacesuit;

import com.rae.creatingspace.api.gui.elements.SliderWidget;
import com.rae.creatingspace.configs.CSConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class RemainingO2Overlay implements IGuiOverlay {
    private SliderWidget gauge;
    public static final RemainingO2Overlay INSTANCE = new RemainingO2Overlay();
    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {

        Minecraft mc = Minecraft.getInstance();

        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        LocalPlayer player = mc.player;
        if (player == null)
            return;

        ItemStack itemInChestSlot = player.getItemBySlot(EquipmentSlot.CHEST);

        if (itemInChestSlot.getItem() instanceof OxygenBacktankItem){
            CompoundTag tag = itemInChestSlot.getOrCreateTag();
            float o2Value = tag.getFloat("Oxygen");
            float prevO2Value =  tag.getFloat("prevOxygen");
            //prevO2Value = o2Value;
            //TODO create one at initialization the keep the same
            gauge = new SliderWidget(CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getX(screenWidth), CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getY(screenHeight), 32, 64, CSConfigs.CLIENT.oxygenBacktank.sliderColor.get().getColor());
            gauge.setMax(OxygenBacktankUtil.maxOxygen(itemInChestSlot));
            gauge.setValues((int) o2Value, (int) prevO2Value);
            gauge.render(graphics, (int) mc.mouseHandler.xpos(),(int) mc.mouseHandler.ypos() ,partialTick);

        }

    }

}
