package com.rae.creatingspace.init.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum GuiTexturesInit implements ScreenElement {

    ROCKET_CONTROLS("rocket_controls",13,0,226,226),
    LAUNCH_BUTTON("rocket_controls",155,202,77,18),
    UP_ARROW("rocket_controls",159,231,16,23),
    DOWN_ARROW("rocket_controls",211,232,16,23),

    ON_EARTH("near_planets",0,339,220,173,512,512),
    ON_EARTH_ORBIT("near_planets",0,250,220,173,512,512),
    OVERWORLD("overworld",0,83,220,173);

    //copy of allGuiTexture's methods
    public static final int FONT_COLOR = 0x575F7A;
    public final ResourceLocation location;
    public int width, height;
    public int startX, startY;
    public int sheet_width, sheet_height;

    private GuiTexturesInit(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private GuiTexturesInit(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    private GuiTexturesInit(String location, int startX, int startY, int width, int height) {
        this(CreatingSpace.MODID, location, startX, startY, width, height,256,256);
    }
    private GuiTexturesInit(String location, int startX, int startY, int width, int height,int sheet_width, int sheet_height) {
        this(CreatingSpace.MODID, location, startX, startY, width, height,sheet_width,sheet_height);
    }

    private GuiTexturesInit(String namespace, String location, int startX, int startY, int width, int height,int sheet_width, int sheet_height) {
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
        this.sheet_width = sheet_width;
        this.sheet_height =sheet_height;
    }

    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }


    /*@OnlyIn(Dist.CLIENT)
    @Override
    public void render(PoseStack ms, int x, int y) {
        bind();
        GuiComponent.blit(ms, x, y, 0, startX, startY, width, height, 256, 256);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, int x, int y, GuiGraphics component) {
        bind();
        component.blit(ms, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(ms, c, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, int x, int y, int sheet_width, int sheet_height, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(ms, c, x, y,0, startX, startY, width, height, sheet_width, sheet_height);
    }
    @OnlyIn(Dist.CLIENT)
    public void renderNotStandardSheetSize(PoseStack ms, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(ms, c, x, y,0, startX, startY, width, height, sheet_width, sheet_height);
    }*/
}
