package com.rae.creatingspace.content.rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.content.rocket.contraption.entity.RocketContraptionEntity;
import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsHandler;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class RocketHUD {

    public static final IGuiOverlay OVERLAY = RocketHUD::renderOverlay;

    static LerpedFloat displayedSpeed = LerpedFloat.linear();//speed will be for the
    static LerpedFloat displayedThrottle = LerpedFloat.linear();


    static int hudPacketCooldown = 5;

    public static Component currentPrompt;
    public static boolean currentPromptShadow;
    public static int promptKeepAlive = 0;


    public static void tick() {
        if (promptKeepAlive > 0)
            promptKeepAlive--;
        else
            currentPrompt = null;//what is the prompt ?

        Minecraft mc = Minecraft.getInstance();

    }



    public static void renderOverlay(ForgeGui gui, GuiGraphics graphics, float partialTicks, int width,
                                     int height) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        if (!(RocketControlsHandler.getContraption() instanceof RocketContraptionEntity cce))
            return;
        Entity cameraEntity = Minecraft.getInstance()
                .getCameraEntity();
        if (cameraEntity == null)
            return;
        BlockPos localPos = RocketControlsHandler.getControlsPos();
        if (localPos == null)
            return;

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();

        graphics.renderTooltip(gui.getFont(), Component.literal("test"), width / 2, height / 2);
        // todo put the custom rocket HUD logic here.


        poseStack.popPose();
    }
}
