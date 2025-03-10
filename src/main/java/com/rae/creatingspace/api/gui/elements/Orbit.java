package com.rae.creatingspace.api.gui.elements;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import com.simibubi.create.foundation.gui.widget.BoxWidget;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Orbit extends BoxWidget {
    private Integer windowHeight = null;
    private Integer windowWidth = null;
    private double planetY;
    private double planetX;
    private final ResourceLocation dim;
    //it's a circle
    int radius;
    private float zoom;
    private Integer maxSatelliteDistance = 0;

    public void setBodyRadius(int bodyRadius) {
        this.bodyRadius = bodyRadius;
    }

    int bodyRadius = 5;

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
        if (maxSatelliteDistance < satellite.radius) maxSatelliteDistance = satellite.radius;
    }
    public int getMaxSatelliteDistance() {
        return maxSatelliteDistance;
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

    public void setWindow(int windowHeight, int windowWidth) {
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return ((radius / zoom - 5) * (radius / zoom - 5) < ((getX() + xShift - mouseX) * (getX() + xShift - mouseX) + (getY() + yShift - mouseY) * (getY() + yShift - mouseY))) &&
                (((getX() + xShift - mouseX) * (getX() + xShift - mouseX) + (getY() + yShift - mouseY) * (getY() + yShift - mouseY)) < (radius / zoom + 5) * (radius / zoom + 5));
    }

    @Override
    public void render(@NotNull GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            isHovered = isMouseOver(mouseX, mouseY);
            beforeRender(ms, mouseX, mouseY, partialTicks);
            renderButton(ms, mouseX, mouseY, partialTicks);
            afterRender(ms, mouseX, mouseY, partialTicks);
            wasHovered = isHoveredOrFocused();
        }
        float theta = getTheta(partialTicks);
        planetX = CSDimensionUtil.isOrbit(dim) ? getX() : getX() + radius / zoom * Math.sin(theta);
        planetY = CSDimensionUtil.isOrbit(dim) ? getY() : getY() + radius / zoom * Math.cos(theta);
        for (Orbit orbit : satellites) {
            orbit.setX((int) planetX);
            orbit.setY((int) planetY);
        }
    }

    public void renderButton(@NotNull GuiGraphics ms, int mouseX, int mouseY, float partialTicks) {
        if (isInsideWindow(getX() + xShift, getY() + yShift)) {
            drawCircle(ms, getX() + xShift, getY() + yShift, (int) (radius / zoom));
        }
        if (isInsideWindow((int) (xShift + getPlanetX()), (int) (yShift + getPlanetY()))) {
            drawBody(ms, (int) (xShift + getPlanetX()), (int) (yShift + getPlanetY()));
        }
    }



    private boolean isInsideWindow(int x, int y) {
        if (windowWidth == null || windowHeight == null) {
            return true;
        } else {
            boolean flag1 = -radius / zoom <= (x) && (x) <= windowWidth + radius / zoom && 0 <= (y) && (y) <= windowHeight;
            boolean flag2 = -radius / zoom <= (y) && (y) <= windowHeight + radius / zoom && 0 <= (x) && (x) <= windowWidth;
            boolean flag3 = (x) * (x) + (y) * (y) <= (radius / zoom) * (radius / zoom) ||
                    (x - windowWidth) * (x - windowWidth) + (y) * (y) <= (radius / zoom) * (radius / zoom)
                    || (x) * (x) + (y - windowHeight) * (y - windowHeight) <= (radius / zoom) * (radius / zoom) ||
                    (x - windowWidth) * (x - windowWidth) + (y - windowHeight) * (y - windowHeight) <= (radius / zoom) * (radius / zoom);
            return flag1 || flag2 || flag3;
        }
    }

    public double getPlanetY() {
        return planetY;
    }

    public double getPlanetX() {
        return planetX;
    }

    private float getTheta(float partialTicks) {
        float time = Objects.requireNonNull(Minecraft.getInstance().getCameraEntity()).tickCount + partialTicks;
        float speed = 0.01f;
        return radius > 0 ? (float) (time * 2 * Math.PI / radius) * speed : 1;
    }

    private void drawBody(GuiGraphics ms, int centerX, int centerY) {
        if (!CSDimensionUtil.isOrbit(dim)) {
            GuiTexturesInit.render(CreatingSpace.resource(
                            "textures/gui/destination/"
                                    +
                                    dim.getPath()
                                    + ".png"),
                    ms, centerX - bodyRadius, centerY - bodyRadius, 0, 0, bodyRadius * 2 + 1,
                    bodyRadius * 2 + 1, bodyRadius * 2 + 1, bodyRadius * 2 + 1, Color.WHITE);
        }
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        if (!active || !visible)
            return false;
        return isMouseOver(pMouseX, pMouseY);
    }

    //launch lazy renderer when the orbit is bigger that the window
    private void drawCircle(GuiGraphics graphics, int x, int y, int radius) {
        if (radius > 8) {
            int color = gradientColor1.getRGB();
            int startX = 0;
            int startY = radius;
            int stopY = 0;
            int length = 0;
            if (radius > windowWidth) {
                if (x > y) {
                    if (x < 0) {
                        startX = Math.max(-x, 0);
                    } else if (x > windowWidth) {
                        startX = Math.max(x - windowWidth, windowWidth);
                    }
                    stopY = (int) Math.sqrt(Math.min(radius * radius - x * x, radius * radius - (x - windowWidth) * (x - windowWidth)));
                    startY = (int) Math.sqrt(Math.max(radius * radius - x * x, radius * radius - (x - windowWidth) * (x - windowWidth)));
                } else {
                    if (y < 0) {
                        startX = Math.max(-y, 0);
                    } else if (y > windowHeight) {
                        startX = Math.max(y - windowHeight, windowHeight);
                    }
                    stopY = (int) Math.sqrt(Math.min(radius * radius - y * y, radius * radius - (y - windowHeight) * (y - windowHeight)));
                    startY = (int) Math.sqrt(Math.max(radius * radius - y * y, radius * radius - (y - windowHeight) * (y - windowHeight)));

                }
            }
            int d = 1 - radius;
            while (startX <= startY && startY >= stopY) {
                if (d < 0) {
                    d += 2 * (startX + length) + 3;
                } else {
                    drawSymmetricLines(graphics, x, y, startX, startY, length, color);
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
        setFocused(true);
    }

    private void drawSymmetricLines(GuiGraphics graphics, int centerX, int centerY, int startX, int startY, int length, int color) {
        //bottom
        graphics.hLine(centerX + startX, centerX + startX + length, centerY + startY, color);
        graphics.hLine(centerX - startX - length, centerX - startX, centerY + startY, color);

        //top
        graphics.hLine( centerX + startX, centerX + startX + length, centerY - startY, color);
        graphics.hLine( centerX - startX - length, centerX - startX, centerY - startY, color);

        //right
        graphics.vLine( centerX + startY, centerY + startX - 1, centerY + startX + length + 1, color);
        graphics.vLine( centerX + startY, centerY - startX - length - 1, centerY - startX + 1, color);

        //left
        graphics.vLine(centerX - startY, centerY + startX - 1, centerY + startX + length + 1, color);
        graphics.vLine(centerX - startY, centerY - startX - length - 1, centerY - startX + 1, color);

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