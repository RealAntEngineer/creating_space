package com.rae.creatingspace.api.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.utilities.CSDimensionUtil;
import com.simibubi.create.foundation.gui.widget.BoxWidget;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Orbit extends BoxWidget {
    private final ResourceLocation dim;
    //it's a circle
    int radius;
    private float zoom;

    public void setxShift(int xShift) {
        this.xShift = xShift;
    }

    public void setyShift(int yShift) {
        this.yShift = yShift;
    }

    private int xShift;
    private int yShift;

    public void setSatellites(List<Orbit> satellites) {
        this.satellites = new ArrayList<>(satellites);
    }

    public void addSatellite(Orbit satellite) {
        this.satellites.add(satellite);
    }

    ArrayList<Orbit> satellites = new ArrayList<>();

    public Orbit(int centerX, int centerY, int radius, ResourceLocation dim) {
        super(centerX, centerY, radius * 2, radius * 2);
        this.radius = radius;
        this.dim = dim;
    }

    public Orbit(int centerX, int centerY, int radius, ResourceLocation dim, List<Orbit> satellites) {
        super(centerX, centerY, radius * 2, radius * 2);
        this.radius = radius;
        this.dim = dim;
        this.satellites = new ArrayList<>(satellites);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return ((radius / zoom - 5) * (radius / zoom - 5) < ((x + xShift - mouseX) * (x + xShift - mouseX) + (y + yShift - mouseY) * (y + yShift - mouseY))) &&
                (((x + xShift - mouseX) * (x + xShift - mouseX) + (y + yShift - mouseY) * (y + yShift - mouseY)) < (radius / zoom + 5) * (radius / zoom + 5));
    }

    @Override
    public void render(@NotNull PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = isMouseOver(mouseX, mouseY);
            beforeRender(ms, mouseX, mouseY, partialTicks);
            renderButton(ms, mouseX, mouseY, partialTicks);
            afterRender(ms, mouseX, mouseY, partialTicks);
            wasHovered = isHoveredOrFocused();
        }
        float theta = getTheta(partialTicks);
        for (Orbit orbit : satellites) {
            orbit.x = (int) (x + radius / zoom * Math.sin(theta));
            orbit.y = (int) (y + radius / zoom * Math.cos(theta));
        }
    }

    @Override
    public void renderButton(@NotNull PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        drawCircle(ms, x + xShift, y + yShift, (int) (radius / zoom));
        drawBody(ms, xShift + getPlanetX(partialTicks), yShift + getPlanetY(partialTicks));
    }

    public int getPlanetY(float partialTicks) {
        return CSDimensionUtil.isOrbit(dim) ? y : (int) (y + radius / zoom * Math.cos(getTheta(partialTicks)));
    }

    public int getPlanetX(float partialTicks) {
        return CSDimensionUtil.isOrbit(dim) ? x : (int) (x + radius / zoom * Math.sin(getTheta(partialTicks)));
    }

    private float getTheta(float partialTicks) {
        float time = Objects.requireNonNull(Minecraft.getInstance().getCameraEntity()).tickCount + partialTicks;
        float speed = 0.01f;
        return (float) (time * 2 * Math.PI / radius) * speed;
    }

    //TODO :make a render that take polar coordinates
    //TODO : verify if it's inside the screen
    private void drawBody(PoseStack ms, int centerX, int centerY) {
        if (!CSDimensionUtil.isOrbit(dim)) {
            GuiTexturesInit.render(CreatingSpace.resource(
                            "textures/gui/destination/"
                                    +
                                    dim.getPath()
                                    + ".png"),
                    ms, centerX - 5, centerY - 5, 0, 0, 11,
                    11, 11, 11, Color.WHITE);
        }
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        if (!active || !visible)
            return false;
        return isMouseOver(pMouseX, pMouseY);
    }

    private void drawCircle(PoseStack ms, int x, int y, int radius) {
        if (radius > 4) {

            int color = gradientColor1.getRGB();
            int startX = 0;
            int startY = radius;
            int length = 0;
            int d = 1 - radius;
            while (startX <= startY) {
                if (d < 0) {
                    d += 2 * (startX + length) + 3;
                } else {
                    drawSymmetricLines(ms, x, y, startX, startY, length, color);
                    d += 2 * ((startX + length) - startY) + 5;
                    startY -= 1;
                    startX += length;
                    length = 0;
                }
                length += 1;
            }
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        changeFocus(true);
    }

    private void drawSymmetricLines(PoseStack poseStack, int centerX, int centerY, int startX, int startY, int length, int color) {
        //bottom
        hLine(poseStack, centerX + startX, centerX + startX + length, centerY + startY, color);
        hLine(poseStack, centerX - startX - length, centerX - startX, centerY + startY, color);

        //top
        hLine(poseStack, centerX + startX, centerX + startX + length, centerY - startY, color);
        hLine(poseStack, centerX - startX - length, centerX - startX, centerY - startY, color);

        //right
        vLine(poseStack, centerX + startY, centerY + startX - 1, centerY + startX + length + 1, color);
        vLine(poseStack, centerX + startY, centerY - startX - length - 1, centerY - startX + 1, color);

        //left
        vLine(poseStack, centerX - startY, centerY + startX - 1, centerY + startX + length + 1, color);
        vLine(poseStack, centerX - startY, centerY - startX - length - 1, centerY - startX + 1, color);

    }

    public ResourceLocation getDim() {
        return dim;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void shiftX(int amount) {
        this.xShift += amount;
    }

    public void shiftY(int amount) {
        this.yShift += amount;
    }
}