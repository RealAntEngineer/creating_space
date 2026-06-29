package com.rae.creatingspace.content.life_support.spacesuit;

import com.rae.creatingspace.api.gui.elements.VerticalDialWidget;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.configs.CSOxygenBacktank;
import com.rae.creatingspace.init.DataComponentsInit;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class RemainingO2Overlay implements LayeredDraw.Layer {
    private       VerticalDialWidget gauge;
    public static final RemainingO2Overlay INSTANCE = new RemainingO2Overlay();

    RemainingO2Overlay() {
        Minecraft mc = Minecraft.getInstance();

        try {
            gauge = new VerticalDialWidget(CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getX(mc.getWindow().getGuiScaledWidth()),
                    CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getY(mc.getWindow().getGuiScaledHeight()),
                    32, 64, CSConfigs.CLIENT.oxygenBacktank.sliderColor.get().getColor());
        } catch (IllegalStateException stateException){
            gauge = new VerticalDialWidget(0, 0,
                    32, 64, CSOxygenBacktank.ColorSelection.WHITE.getColor());
        }
    }

    public static void reload(){
        Minecraft mc = Minecraft.getInstance();
        try {
            INSTANCE.gauge = new VerticalDialWidget(CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getX(mc.getWindow().getGuiScaledWidth()),
                    CSConfigs.CLIENT.oxygenBacktank.sliderPlace.get().getY(mc.getWindow().getGuiScaledHeight()),
                    32, 64, CSConfigs.CLIENT.oxygenBacktank.sliderColor.get().getColor());
        } catch (IllegalStateException stateException){
            INSTANCE.gauge = new VerticalDialWidget(0, 0,
                    32, 64, CSOxygenBacktank.ColorSelection.WHITE.getColor());
        }
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
            int o2Value = itemInChestSlot.getOrDefault(DataComponentsInit.OXYGEN_LEVEL, 0);
            gauge.setMax(OxygenBacktankUtil.maxOxygen(itemInChestSlot));
            gauge.setChase((int) o2Value);
            gauge.tickChaser(deltaTracker);
            gauge.render(graphics, (int) mc.mouseHandler.xpos(),(int) mc.mouseHandler.ypos() ,deltaTracker.getRealtimeDeltaTicks());

        }
    }
}
