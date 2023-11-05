package com.rae.creatingspace.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.client.gui.screen.elements.SliderWidget;
import com.rae.creatingspace.server.armor.OxygenBacktankUtil;
import com.rae.creatingspace.server.armor.OxygenBacktankItem;
import net.minecraft.client.Minecraft;
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
    public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {

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
            gauge = new SliderWidget(30,screenHeight-80,32,64);
            gauge.setMax(OxygenBacktankUtil.maxOxygen(itemInChestSlot));
            gauge.setValues((int) o2Value, (int) prevO2Value);
            gauge.render(poseStack, (int) mc.mouseHandler.xpos(),(int) mc.mouseHandler.ypos() ,partialTick);

        }

    }

}
