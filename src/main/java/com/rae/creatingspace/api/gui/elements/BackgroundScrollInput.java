package com.rae.creatingspace.api.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import org.jetbrains.annotations.NotNull;

public class BackgroundScrollInput extends ScrollInput {
    public BackgroundScrollInput(int xIn, int yIn, int widthIn, int heightIn) {
        super(xIn, yIn, widthIn, heightIn);
    }

    @Override
    public void renderButton(@NotNull PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        GuiTexturesInit.SCROLL_BACKGROUND.render(ms,x-1,y-1,this);
        super.renderButton(ms, mouseX, mouseY, partialTicks);
    }
}
