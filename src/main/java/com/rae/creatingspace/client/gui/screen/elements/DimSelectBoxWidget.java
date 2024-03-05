package com.rae.creatingspace.client.gui.screen.elements;

import com.simibubi.create.foundation.gui.widget.BoxWidget;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DimSelectBoxWidget extends BoxWidget {
    private final ResourceKey<Level> dim;
    private final Label label;
    protected int color;
    protected Font font;

    public DimSelectBoxWidget(int x, int y, int width, int height, Component text, ResourceKey<Level> dim){
        super(x, y, width,height);
        font = Minecraft.getInstance().font;
        this.dim = dim;
        this.label = new Label(x+3,y+(height-10)/2,text);
        label.setTextAndTrim(text,true,112);
        color = 0xFFFFFF;
    }



    public ResourceKey<Level> getDim() {
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
