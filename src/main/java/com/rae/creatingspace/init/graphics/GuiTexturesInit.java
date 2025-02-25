package com.rae.creatingspace.init.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum GuiTexturesInit implements ScreenElement {

    ROCKET_CONTROLS("rocket_controls",13,0,226,226),
    ROCKET_ASSEMBLE("rocket_assemble", 0, 0, 192, 76),
    ROCKET_ICON("rocket_assemble", 1, 231, 16, 24),
    LAUNCH_BUTTON("rocket_controls",155,202,77,18),//clean
    INSTRUCTION_BACKGROUND("schedule/schedule_2",3,0,318,89,512,512),
    SEALER_BACKGROUND("oxygen_sealer",13,0,256,124),
    ROCKET_ENGINEER_TABLE("rocket_engineer_table/background", 0, 0, 382, 126, 512, 512),
    ROCKET_ENGINEER_TABLE_INPUT("rocket_engineer_table/background", 0, 125, 131, 126, 512, 512),
    UPGRADABLE_EQUIPMENT("upgradable_equipment", 0, 0, 64, 64, 64, 64),
    SCROLL_BACKGROUND("scroll_background",0,0,66,20),
    O2_GAUGE_FRAME("o2_gauge/frame",0,0,32,64,64,64),
    O2_GAUGE_SLIDER("o2_gauge/slider",0,0,32,64,64,64),
    O2_GAUGE_SHADOW("o2_gauge/shadow",0,0,32,64,64,64),
    ROCKET_INFO("rocket_info", 0, 0, 111, 143);//clean

    //copy of allGuiTexture's methods
    public static final int FONT_COLOR = 0x575F7A;
    public final ResourceLocation location;
    public final int width, height;
    public final int startX, startY;
    public final int sheet_width, sheet_height;

    GuiTexturesInit(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    GuiTexturesInit(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    GuiTexturesInit(String location, int startX, int startY, int width, int height) {
        this(CreatingSpace.MODID, location, startX, startY, width, height,256,256);
    }
    GuiTexturesInit(String location, int startX, int startY, int width, int height,int sheet_width, int sheet_height) {
        this(CreatingSpace.MODID, location, startX, startY, width, height,sheet_width,sheet_height);
    }

    GuiTexturesInit(String namespace, String location, int startX, int startY, int width, int height,int sheet_width, int sheet_height) {
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
    @Override
    public void render(PoseStack ms, int x, int y) {
        bind();
        GuiComponent.blit(ms, x, y, 0, startX, startY, width, height, sheet_width, sheet_height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, int x, int y, GuiComponent component) {
        bind();
        component.blit(ms, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, int x, int y, int sheet_width, int sheet_height, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(ms, c, x, y,0, startX, startY, width, height, sheet_width, sheet_height);
    }
    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(ms, c, x, y,0, startX, startY, width, height, sheet_width, sheet_height);
    }

    @OnlyIn(Dist.CLIENT)
    public static void render(ResourceLocation location, PoseStack ms, int x, int y, int startX, int startY, int width, int height, int sheet_width, int sheet_height, Color c) {
        RenderSystem.setShaderTexture(0, location);
        UIRenderHelper.drawColoredTexture(ms, c, x, y, 0, startX, startY, width, height, sheet_width, sheet_height);
    }
}
