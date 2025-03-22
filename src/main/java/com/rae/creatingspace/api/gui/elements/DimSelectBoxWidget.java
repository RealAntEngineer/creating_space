package com.rae.creatingspace.api.gui.elements;

import com.simibubi.create.foundation.gui.widget.BoxWidget;
import com.simibubi.create.foundation.gui.widget.Label;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class DimSelectBoxWidget extends BoxWidget {
    private final ResourceLocation dim;
    private final Label label;
    protected int color;
    protected Font font;

    public DimSelectBoxWidget(int x, int y, int width, int height, Component text, ResourceLocation dim) {
        super(x, y, width,height);
        font = Minecraft.getInstance().font;
        this.dim = dim;
        this.label = new Label(x+3,y+(height-10)/2,text);
        label.setTextAndTrim(text,true,112);
        color = 0xFFFFFF;
    }


    public ResourceLocation getDim() {
        return dim;
    }

    @Override
    public void doRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.doRender(graphics, mouseX, mouseY, partialTicks);
        if (label == null || label.text.getString().isEmpty())
            return;

        label.render(graphics,mouseX,mouseY,partialTicks
        );
    }
}
