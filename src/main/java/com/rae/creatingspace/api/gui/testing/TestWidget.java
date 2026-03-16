package com.rae.creatingspace.api.gui.testing;

import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TestWidget extends AbstractSimiWidget {

    public TestWidget(int x, int y) {
        super(x, y, 40, 20, Component.literal("Test"));
    }

    @Override
    protected void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        int color;

        if (!active)
            color = 0xFF777777;
        else if (isHovered)
            color = 0xFF00FF00;
        else
            color = 0xFF0000FF;

        graphics.fill(getX(), getY(), getX() + width, getY() + height, color);

        graphics.drawString(
                Minecraft.getInstance().font,
                "Btn",
                getX() + 6,
                getY() + 6,
                0xFFFFFF,
                false
        );
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        System.out.println("TestWidget clicked at " + mouseX + ", " + mouseY);
        super.onClick(mouseX, mouseY);
    }
}
