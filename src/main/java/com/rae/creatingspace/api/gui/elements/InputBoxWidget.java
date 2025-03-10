package com.rae.creatingspace.api.gui.elements;

import com.simibubi.create.foundation.gui.widget.BoxWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class InputBoxWidget extends BoxWidget {
    private EditBox editBox;

    public InputBoxWidget(int x, int y, int width, int height, Component initialValue) {
        super(x, y, width, height);
        editBox = new EditBox(Minecraft.getInstance().font,x,y,width,height,initialValue);
        editBox.setBordered(false);
    }

    public EditBox getEditBox() {
        return editBox;
    }

    @Override
    public void tick() {
        super.tick();
        //editBox.tick();
    }

    public void setValue(int value){
        this.editBox.setValue(String.valueOf(value));
    }
    public String getValue() {
        return editBox.getValue();
    }

    @Override
    public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
        return editBox.mouseClicked(p_93641_, p_93642_, p_93643_);
    }

    @Override
    public boolean keyPressed(int p_94745_, int p_94746_, int p_94747_) {
        return editBox.keyPressed(p_94745_, p_94746_, p_94747_);
    }

    @Override
    public boolean charTyped(char p_94732_, int p_94733_) {
        return editBox.charTyped(p_94732_, p_94733_);
    }

    @Override
    public boolean keyReleased(int p_94750_, int p_94751_, int p_94752_) {
        return editBox.keyReleased(p_94750_, p_94751_, p_94752_);
    }

    public void setVisibility(boolean visible){
        this.visible = visible;
        editBox.visible = visible;
    }

    public void setActivity(boolean active){
        this.active = active;
        editBox.active = active;
    }

    public void setResponder(Consumer<String> stringConsumer){
        editBox.setResponder(stringConsumer);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        //editBox.render(ms,mouseX,mouseY,partialTicks);
    }


}
