package com.rae.creatingspace.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rae.creatingspace.content.recipes.air_liquefying.AirLiquefierBlock;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;

public class AnimatedAirLiquefier extends AnimatedKinetics {
    protected static final int SCALE = 24;

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();

        matrixStack.pushPose();
        matrixStack.translate(SCALE * 2, SCALE * 2, 0);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-12.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));

        blockElement(shaft(Direction.Axis.X))
                .rotateBlock(getCurrentAngle(), 0, 0)
                .atLocal(0, 0, 1)
                .scale(SCALE)
                .render(graphics);

        AnimatedKinetics.defaultBlockElement(AllPartialModels.ENCASED_FAN_INNER)
                .rotateBlock(180, 0, getCurrentAngle() * 16)
                .atLocal(0, 0, 1)
                .scale(SCALE)
                .render(graphics);

        AnimatedKinetics.defaultBlockElement(
                BlockInit.AIR_LIQUEFIER.getDefaultState().setValue(
                        AirLiquefierBlock.AXIS_ALONG_FIRST_COORDINATE, true))
                .rotateBlock(0, 180, 0)
                .atLocal(0, 0, 1)
                .scale(SCALE)
                .render(graphics);
        matrixStack.popPose();
    }

}
