package com.rae.creatingspace.api.gui.elements;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.widget.IconButton;
import net.createmod.catnip.gui.element.ScreenElement;
import net.minecraft.client.gui.GuiGraphics;

public class TallIconButton extends IconButton {
    public TallIconButton(int x, int y, ScreenElement icon) {
        super(x, y, 18, 26, icon);
    }

    @Override
    protected void drawBg(GuiGraphics graphics, AllGuiTextures button) {
        super.drawBg(graphics, button);
        graphics.blit(button.location, getX(), getY() + 9, button.getStartX(), button.getStartY() + 1, button.getWidth(), button.getHeight() - 1);
    }
}
