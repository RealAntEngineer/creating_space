package com.rae.creatingspace.legacy.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import org.joml.Matrix4f;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.renderer.GameRenderer;

public class CustomRenderHelper {

    public static void drawStretched(PoseStack ms, int left, int top, int w, int h, int z, GuiTexturesInit tex) {
        tex.bind();
        drawTexturedQuad(ms.last()
                        .pose(), Color.WHITE, left, left + w, top, top + h, z, tex.startX / 256f, (tex.startX + tex.width) / 256f,
                tex.startY / 256f, (tex.startY + tex.height) / 256f);
    }

    private static void drawTexturedQuad(Matrix4f m, Color c, int left, int right, int top, int bot, int z, float u1, float u2, float v1, float v2) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        bufferbuilder.vertex(m, (float) left , (float) bot, (float) z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).uv(u1, v2).endVertex();
        bufferbuilder.vertex(m, (float) right, (float) bot, (float) z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).uv(u2, v2).endVertex();
        bufferbuilder.vertex(m, (float) right, (float) top, (float) z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).uv(u2, v1).endVertex();
        bufferbuilder.vertex(m, (float) left , (float) top, (float) z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).uv(u1, v1).endVertex();
        tesselator.end();
        RenderSystem.disableBlend();
    }
}
