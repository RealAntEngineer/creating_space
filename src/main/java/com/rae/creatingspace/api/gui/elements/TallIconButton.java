package com.rae.creatingspace.api.gui.elements;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.minecraft.client.gui.GuiGraphics;

public class TallIconButton extends IconButton {
    public TallIconButton(int x, int y, ScreenElement icon) {
        super(x, y, 18, 26, icon);
    }

    @Override
    protected void drawBg(GuiGraphics graphics, AllGuiTextures button) {
        super.drawBg(graphics, button);
        graphics.blit(button.location, getX(), getY() + 9, button.startX, button.startY + 1, button.width, button.height - 1);
    }
}
