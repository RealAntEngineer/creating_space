package com.rae.creatingspace.content.life_support.spacesuit;

import com.rae.creatingspace.api.gui.elements.VerticalDialWidget;
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
    private VerticalDialWidget gauge;
    public static final RemainingO2Overlay INSTANCE = new RemainingO2Overlay();

    RemainingO2Overlay() {
        Minecraft mc = Minecraft.getInstance();
        gauge = new VerticalDialWidget(CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getX(mc.getWindow().getGuiScaledWidth()),
                CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getY(mc.getWindow().getGuiScaledHeight()),
                32, 64, CSConfigs.CLIENT.oxygenBacktank.sliderColor.get().getColor());
    }

    public static void reload(){
        Minecraft mc = Minecraft.getInstance();
        INSTANCE.gauge = new VerticalDialWidget(CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getX(mc.getWindow().getGuiScaledWidth()),
                CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getY(mc.getWindow().getGuiScaledHeight()),
                32, 64, CSConfigs.CLIENT.oxygenBacktank.sliderColor.get().getColor());
    }

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
            //prevO2Value = o2Value;
            //TODO create one at initialization the keep the same
            gauge.setMax(OxygenBacktankUtil.maxOxygen(itemInChestSlot));
            gauge.setChase((int) o2Value);
            //gauge.tickChaser(deltaTracker);
            gauge.render(graphics, (int) mc.mouseHandler.xpos(),(int) mc.mouseHandler.ypos() ,partialTick);

        }

    }

}
