package com.rae.creatingspace.content.rocket.squedule;


import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.content.rocket.squedule.condition.ScheduleWaitCondition;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.gui.widget.AbstractSimiWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class ScheduleEntryWidget extends AbstractSimiWidget {

    private final int entryIndex;
    //need a reference to the entry
    private final ScheduleEntry entry;
    private static final int CARD_HEADER = 22;
    private static final int CARD_WIDTH = 195;

    //for later
    protected ScheduleEntryWidget(int x, int y, ScheduleEntry entry, int entryIndex) {
        super(x, y);
        this.entry = entry;
        this.entryIndex = entryIndex;
    }


    //to do latter

    /*
    public int renderScheduleEntry(GuiGraphics graphics, int yOffset) {
        int zLevel = -100;

        AllGuiTextures light = AllGuiTextures.SCHEDULE_CARD_LIGHT;
        AllGuiTextures medium = AllGuiTextures.SCHEDULE_CARD_MEDIUM;
        AllGuiTextures dark = AllGuiTextures.SCHEDULE_CARD_DARK;

        int cardWidth = CARD_WIDTH;
        int cardHeader = CARD_HEADER;
        int maxRows = 0;
        for (List<ScheduleWaitCondition> list : entry.conditions)
            maxRows = Math.max(maxRows, list.size());
        boolean supportsConditions = entry.instruction.supportsConditions();
        int cardHeight = cardHeader + (supportsConditions ? 24 + maxRows * 18 : 4);

        PoseStack matrixStack = graphics.pose();


        matrixStack.pushPose();
        matrixStack.translate(getX() + 25, getY() + yOffset, 0);

        UIRenderHelper.drawStretched(graphics, 0, 1, cardWidth, cardHeight - 2, zLevel, light);
        UIRenderHelper.drawStretched(graphics, 1, 0, cardWidth - 2, cardHeight, zLevel, light);
        UIRenderHelper.drawStretched(graphics, 1, 1, cardWidth - 2, cardHeight - 2, zLevel, dark);
        UIRenderHelper.drawStretched(graphics, 2, 2, cardWidth - 4, cardHeight - 4, zLevel, medium);
        UIRenderHelper.drawStretched(graphics, 2, 2, cardWidth - 4, cardHeader, zLevel,
                supportsConditions ? light : medium);

        AllGuiTextures.SCHEDULE_CARD_REMOVE.render(graphics, cardWidth - 14, 2);
        AllGuiTextures.SCHEDULE_CARD_DUPLICATE.render(graphics, cardWidth - 14, cardHeight - 14);

        int i = entryIndex;
        if (i > 0)
            AllGuiTextures.SCHEDULE_CARD_MOVE_UP.render(graphics, cardWidth, cardHeader - 14);
        if (i < schedule.entries.size() - 1)
            AllGuiTextures.SCHEDULE_CARD_MOVE_DOWN.render(graphics, cardWidth, cardHeader);

        UIRenderHelper.drawStretched(graphics, 8, 0, 3, cardHeight + 10, zLevel,
                AllGuiTextures.SCHEDULE_STRIP_LIGHT);
        (supportsConditions ? AllGuiTextures.SCHEDULE_STRIP_TRAVEL : AllGuiTextures.SCHEDULE_STRIP_ACTION)
                .render(graphics, 4, 6);

        if (supportsConditions)
            AllGuiTextures.SCHEDULE_STRIP_WAIT.render(graphics, 4, 28);

        Pair<ItemStack, Component> destination = entry.instruction.getSummary();
        renderInput(graphics, destination, 26, 5, false, 100);
        entry.instruction.renderSpecialIcon(graphics, 30, 5);

        matrixStack.popPose();

        return cardHeight;
    }
    private int getFieldSize(int minSize, Pair<ItemStack, Component> pair) {
        ItemStack stack = pair.getFirst();
        Component text = pair.getSecond();
        boolean hasItem = !stack.isEmpty();
        return Math.max((text == null ? 0 : font.width(text)) + (hasItem ? 20 : 0) + TOP_BORDER_WIDTH, minSize);
    }

    protected int renderInput(GuiGraphics graphics, Pair<ItemStack, Component> pair, int x, int y, boolean clean,
                              int minSize) {
        ItemStack stack = pair.getFirst();
        Component text = pair.getSecond();
        boolean hasItem = !stack.isEmpty();
        int fieldSize = Math.min(getFieldSize(minSize, pair), 150);
        PoseStack matrixStack = graphics.pose();

        matrixStack.pushPose();

        AllGuiTextures left =
                clean ? AllGuiTextures.SCHEDULE_CONDITION_LEFT_CLEAN : AllGuiTextures.SCHEDULE_CONDITION_LEFT;
        AllGuiTextures middle = AllGuiTextures.SCHEDULE_CONDITION_MIDDLE;
        AllGuiTextures item = AllGuiTextures.SCHEDULE_CONDITION_ITEM;
        AllGuiTextures right = AllGuiTextures.SCHEDULE_CONDITION_RIGHT;

        matrixStack.translate(x, y, 0);
        UIRenderHelper.drawStretched(graphics, 0, 0, fieldSize, TOP_BORDER_WIDTH, -100, middle);
        left.render(graphics, clean ? 0 : -3, 0);
        right.render(graphics, fieldSize - 2, 0);
        if (hasItem)
            item.render(graphics, 3, 0);
        if (hasItem) {
            item.render(graphics, 3, 0);
            if (stack.getItem() != Items.STRUCTURE_VOID)
                GuiGameElement.of(stack)
                        .at(4, 0)
                        .render(graphics);
        }

        if (text != null)
            graphics.drawString(font, font.substrByWidth(text, 120)
                    .getString(), hasItem ? 28 : 8, 4, 0xff_f2f2ee);

        matrixStack.popPose();
        return fieldSize;
    }*/
}
