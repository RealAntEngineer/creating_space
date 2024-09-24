package com.rae.creatingspace.api.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.gui.widget.IconButton;

public class TallIconButton extends IconButton {
    public TallIconButton(int x, int y, ScreenElement icon) {
        super(x, y, 18, 26, icon);
    }

    @Override
    protected void drawBg(PoseStack matrixStack, AllGuiTextures button) {
        super.drawBg(matrixStack, button);
        blit(matrixStack, x, y + 9, button.startX, button.startY + 1, button.width, button.height - 1);
    }
}
