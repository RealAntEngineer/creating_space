package com.rae.creatingspace.api.gui.elements;

import com.simibubi.create.foundation.gui.widget.BoxWidget;
import com.simibubi.create.foundation.gui.widget.Label;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class LabeledBoxWidget extends BoxWidget {
    private final Label label;
    protected int color;

    public LabeledBoxWidget(int x, int y, Component text){
        super(x, y, Minecraft.getInstance().font.width(text)+10,14);
        this.label = new Label(x+5,y+3,text);
        color = 0xFFFFFF;
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        return false;
    }

    @Override
    public void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.doRender(graphics, mouseX, mouseY, partialTicks);
        if (label == null || label.text.getString().isEmpty())
            return;
        label.render(graphics,mouseX,mouseY,partialTicks);
    }

    public void setTextAndTrim(Component text, boolean trimFront, int maxPx) {
        label.setTextAndTrim(text,trimFront,maxPx);
    }

    public void setToolTip(Component text) {
        toolTip.clear();
        toolTip.add(text);
    }
}
