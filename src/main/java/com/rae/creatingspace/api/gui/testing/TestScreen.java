package com.rae.creatingspace.api.gui.testing;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TestScreen extends Screen {

    public TestScreen() {
        super(Component.literal("Widget Test"));
    }

    @Override
    protected void init() {

        TestContainerWidget container = new TestContainerWidget(width / 2 - 90, height / 2 - 50, font);

        addRenderableWidget(container);
    }


    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        renderBackground(graphics);

        super.render(graphics, mouseX, mouseY, partialTicks);

        /*graphics.drawCenteredString(
                font,
                "ScrollInput + Label + EditBox + Button",
                width / 2,
                20,
                0xFFFFFF
        );*/
    }
}