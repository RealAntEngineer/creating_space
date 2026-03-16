package com.rae.creatingspace.api.gui.testing;


import com.rae.creatingspace.api.gui.elements.CompoundWidget;
import net.minecraft.client.gui.GuiGraphics;

import com.rae.creatingspace.api.gui.elements.BackgroundScrollInput;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class TestContainerWidget extends CompoundWidget {

    private ScrollInput scrollInput;
    private Label label;
    private EditBox editBox;
    private IconButton button;

    public TestContainerWidget(int x, int y, net.minecraft.client.gui.Font font) {
        super(x, y);

        int baseX = x + 10;
        int baseY = y + 10;

        // Label for ScrollInput
        label = new Label(baseX + 5, baseY + 5, Component.literal("Scroll Value"));

        // ScrollInput
        scrollInput = new BackgroundScrollInput(baseX, baseY, 60, 18)
                .withRange(0, 100)
                .writingTo(label);

        scrollInput.onChanged();

        addWidget(scrollInput);
        addWidget(label);

        // Text field
        editBox = new EditBox(font, baseX, baseY + 30, 120, 18, Component.literal("Input"));
        editBox.setValue("Type here");

        addWidget(editBox);

        // Button
        button = new IconButton(baseX, baseY + 60, AllIcons.I_CONFIRM);
        button.withCallback(() -> {

            System.out.println("Button clicked");
            System.out.println("Scroll value = " + scrollInput.getState());
            System.out.println("Text = " + editBox.getValue());

        });

        addWidget(button);
    }

    @Override
    protected void doRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        // container background
        graphics.fill(getX(), getY(), getX() + 180, getY() + 100, 0x88000000);

        super.doRender(graphics, mouseX, mouseY, partialTicks);
    }
}