package com.rae.creatingspace.client.gui.screen.elements;

import com.simibubi.create.foundation.gui.widget.BoxWidget;
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
    private ResourceKey<Level> dim;
    private Component text;
    protected int color;
    protected Font font;

    @Override
    public <T extends BoxWidget> T withBorderColors(Couple<Color> colors) {
        return super.withBorderColors(colors);
    }

    public DimSelectBoxWidget(int x, int y, int width, int height, Component text, ResourceKey<Level> dim){
        super(x, y, width,height);
        font = Minecraft.getInstance().font;
        this.dim = dim;
        this.text = text;
        color = 0xFFFFFF;
    }


    @Override
    public Component getMessage() {
        return super.getMessage();
    }
    @Override
    public void onClick(double x, double y) {
        super.onClick(x, y);

    }

    public ResourceKey<Level> getDim() {
        return dim;
    }

    @Override
    public void renderButton(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(graphics, mouseX, mouseY, partialTicks);
        if (text == null || text.getString().isEmpty())
            return;
        graphics.drawString(font,text.getString(),getX()+3, (int) (height/2f -4 + getY()),color);
    }
}
