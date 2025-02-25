package com.rae.creatingspace.content.rocket;

import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.rae.creatingspace.api.squedule.RocketSchedule;
import com.rae.creatingspace.api.squedule.ScheduleEntry;
import com.rae.creatingspace.api.squedule.condition.ScheduleWaitCondition;
import com.rae.creatingspace.api.squedule.condition.ScheduledDelay;
import com.rae.creatingspace.api.squedule.instruction.DestinationInstruction;
import com.rae.creatingspace.api.squedule.instruction.ScheduleInstruction;
import com.rae.creatingspace.api.gui.elements.LabeledBoxWidget;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import com.rae.creatingspace.content.rocket.network.RocketContraptionDisassemblePacket;
import com.rae.creatingspace.content.rocket.network.RocketScheduleEditPacket;
import com.simibubi.create.content.trains.schedule.IScheduleInput;
import com.simibubi.create.foundation.gui.*;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.*;
import com.simibubi.create.foundation.utility.*;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.gui.ScreenUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.*;
import java.util.function.Consumer;

public class ScheduleMakingScreen extends AbstractSimiContainerScreen<RocketMenu> {
    //TODO transform hard coded schedule stuff to widgets
    //beginning of schedule logic
    private static final int CARD_HEADER = 22;
    private static final int CARD_WIDTH = 195;
    private final int TOP_BORDER_WIDTH = 16;
    private final int SIDE_BORDER_WIDTH = 33;
    private final int CHEST_BORD_HEIGHT = 173;
    private final int CHEST_BORD_WIDTH = 220;
    private LerpedFloat scroll = LerpedFloat.linear()
            .startWithValue(0);
    private List<LerpedFloat> horizontalScrolls = new ArrayList<>();
    private RocketSchedule schedule;
    private IconButton cyclicButton;
    private Indicator cyclicIndicator;
    private IconButton resetProgress;
    private IconButton skipProgress;
    private ScheduleInstruction editingDestination;
    private ScheduleWaitCondition editingCondition;
    private SelectionScrollInput scrollInput;//only for conditions
    private Label scrollInputLabel;
    private IconButton editorConfirm, editorDelete;
    private ModularGuiLine editorSubWidgets;//only for conditions not for destination
    private Consumer<Boolean> onEditorClose;
    //end of schedule logic
    private boolean destinationChanged;
    private Button disassembleButton;
    //HashMap<String, BlockPos> initialPosMap;
    private final RocketContraptionEntity rocketContraption;
    private final ResourceLocation currentDimension;
    private ResourceLocation destination;
    private LabeledBoxWidget destinationCost;
    //private EditBox Xinput;
    //private EditBox Zinput;
    //private IconButton validateSetting;

    public ScheduleMakingScreen(RocketMenu container, Inventory inv, Component title) {
        //TODO this screen will swith bwn normal selection (single trip), schedule and rocket overview.
        super(container, inv, Component.translatable("gui.destination_screen.title"));
        this.rocketContraption = container.contentHolder;
        //this.initialPosMap = new HashMap<>(container.contentHolder.getInitialPosMap());
        this.currentDimension = container.contentHolder.getLevel().dimension().location();
        this.destinationChanged = false;
        // schedule
        this.schedule = container.contentHolder.schedule.getSchedule() == null ? new RocketSchedule() : container.contentHolder.schedule.getSchedule();
        this.editorSubWidgets = new ModularGuiLine();
    }

    @Override
    protected void init() {
        setWindowSize(width, height);
        clearWidgets();
        super.init();
        int x = leftPos;
        int y = topPos;

        //everything else
        disassembleButton = new Button(width - 110, y + 120, TOP_BORDER_WIDTH * 4, 20,
                Component.translatable("creatingspace.gui.rocket_controls.disassemble"),
                ($) -> {

                    PacketInit.getChannel()
                            .sendToServer(new RocketContraptionDisassemblePacket(rocketContraption.getId()));
                    onClose();
                });

        addRenderableWidget(disassembleButton);
        destinationCost = new LabeledBoxWidget(width - 97, y + 20, Component.literal("  500 "));
        destinationCost.setToolTip(Component.translatable("creatingspace.gui.rocket_controls.destination_cost"));

        addRenderableWidget(destinationCost);

        cyclicIndicator = new Indicator(x + 21, y + 196, Components.immutableEmpty());
        cyclicIndicator.state = schedule.cyclic ? Indicator.State.ON : Indicator.State.OFF;
        addRenderableWidget(cyclicIndicator);

        cyclicButton = new IconButton(x + 21, y + 202, AllIcons.I_REFRESH);
        cyclicButton.withCallback(() -> {
            schedule.cyclic = !schedule.cyclic;
            cyclicIndicator.state = schedule.cyclic ? Indicator.State.ON : Indicator.State.OFF;
        });

        List<Component> tip = cyclicButton.getToolTip();
        tip.add(Lang.translateDirect("schedule.loop"));
        tip.add(Lang.translateDirect("schedule.loop1")
                .withStyle(ChatFormatting.GRAY));
        tip.add(Lang.translateDirect("schedule.loop2")
                .withStyle(ChatFormatting.GRAY));

        addRenderableWidget(cyclicButton);

        resetProgress = new IconButton(x + 45, y + 202, AllIcons.I_PRIORITY_VERY_HIGH);
        resetProgress.withCallback(() -> {
            schedule.savedProgress = 0;
            resetProgress.active = false;
        });
        resetProgress.active = schedule.savedProgress > 0 && !schedule.entries.isEmpty();
        resetProgress.setToolTip(Lang.translateDirect("schedule.reset"));
        addRenderableWidget(resetProgress);

        skipProgress = new IconButton(x + 63, y + 202, AllIcons.I_PRIORITY_LOW);
        skipProgress.withCallback(() -> {
            schedule.savedProgress++;
            schedule.savedProgress %= schedule.entries.size();
            resetProgress.active = schedule.savedProgress > 0;
        });
        skipProgress.active = schedule.entries.size() > 1;
        skipProgress.setToolTip(Lang.translateDirect("schedule.skip"));
        addRenderableWidget(skipProgress);

        stopEditing();
        horizontalScrolls.clear();
        for (int i = 0; i < schedule.entries.size(); i++)
            horizontalScrolls.add(LerpedFloat.linear()
                    .startWithValue(0));
    }


    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        partialTicks = minecraft.getFrameTime();

        renderBackground(matrixStack);
        renderBg(matrixStack, partialTicks, mouseX, mouseY);
        for (Widget widget : this.renderables)
            widget.render(matrixStack, mouseX, mouseY, partialTicks);
        renderForeground(matrixStack, mouseX, mouseY, partialTicks);

    }

    @Override
    protected void renderForeground(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        if (editingDestination != null) {

            destinationCost.visible = true;
            destinationCost.setTextAndTrim(
                    Component.literal(
                            String.valueOf(CSDimensionUtil.cost(currentDimension,
                                    destination != null ? destination : ResourceLocation.tryParse(editingDestination.getData().getString("Text"))))),
                    true, 112);

        } else {
            destinationCost.visible = false;
        }

        destinationChanged = false;
        //schedule


        super.renderForeground(ms, mouseX, mouseY, partialTicks);
        action(ms, mouseX, mouseY, -1);

    }

    @Override
    protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY) {
        //super.renderBg(ms, partialTicks, mouseX, mouseY);
        AllGuiTextures.SCHEDULE.render(ms, leftPos, topPos);
        GuiTexturesInit.ROCKET_INFO.render(ms, width - 130, 10);
        renderSchedule(ms, partialTicks);
        //render the background of the
        if (editingDestination!=null ) {
            GuiTexturesInit.INSTRUCTION_BACKGROUND.render(ms, leftPos +1, topPos + 40);
        }
        if (editingCondition!=null){
            AllGuiTextures.SCHEDULE_EDITOR.render(ms, leftPos - 2, topPos + 40);
        }
        ms.pushPose();
        ms.translate(0, topPos + 87, 0);
        editorSubWidgets.renderWidgetBG(leftPos + 77, ms);
        ms.popPose();

        //background.render(ms, 0, 0, this);
    }

    @Override
    public void containerTick() {
        //handleTooltips();
        //copied from schedule screen
        scroll.tickChaser();
        for (LerpedFloat lerpedFloat : horizontalScrolls)
            lerpedFloat.tickChaser();

        schedule.savedProgress =
                schedule.entries.isEmpty() ? 0 : Mth.clamp(schedule.savedProgress, 0, schedule.entries.size() - 1);
        resetProgress.active = schedule.savedProgress > 0;
        skipProgress.active = schedule.entries.size() > 1;
    }

    //schedule logic
    protected void renderSchedule(PoseStack matrixStack, float partialTicks) {
        UIRenderHelper.swapAndBlitColor(minecraft.getMainRenderTarget(), UIRenderHelper.framebuffer);
        UIRenderHelper.drawStretched(matrixStack, leftPos + SIDE_BORDER_WIDTH, topPos + TOP_BORDER_WIDTH, 3, CHEST_BORD_HEIGHT, -100,
                AllGuiTextures.SCHEDULE_STRIP_DARK);

        int yOffset = 25;
        List<ScheduleEntry> entries = schedule.entries;
        float scrollOffset = -scroll.getValue(partialTicks);

        for (int i = 0; i <= entries.size(); i++) {

            if (schedule.savedProgress == i && !schedule.entries.isEmpty()) {
                matrixStack.pushPose();
                float expectedY = scrollOffset + topPos + yOffset + 4;
                float actualY = Mth.clamp(expectedY, topPos + 18, topPos + 170);
                matrixStack.translate(0, actualY, 0);
                (expectedY == actualY ? AllGuiTextures.SCHEDULE_POINTER : AllGuiTextures.SCHEDULE_POINTER_OFFSCREEN)
                        .render(matrixStack, leftPos, 0);
                matrixStack.popPose();
            }

            startStencil(matrixStack, leftPos + TOP_BORDER_WIDTH, topPos + TOP_BORDER_WIDTH, CHEST_BORD_WIDTH, CHEST_BORD_HEIGHT);
            matrixStack.pushPose();
            matrixStack.translate(0, scrollOffset, 0);
            if (i == 0 || entries.size() == 0)
                UIRenderHelper.drawStretched(matrixStack, leftPos + SIDE_BORDER_WIDTH, topPos + TOP_BORDER_WIDTH, 3, 10, -100,
                        AllGuiTextures.SCHEDULE_STRIP_LIGHT);

            if (i == entries.size()) {
                if (i > 0)
                    yOffset += 9;
                AllGuiTextures.SCHEDULE_STRIP_END.render(matrixStack, leftPos + 29, topPos + yOffset);
                AllGuiTextures.SCHEDULE_CARD_NEW.render(matrixStack, leftPos + 43, topPos + yOffset);
                matrixStack.popPose();
                endStencil();
                break;
            }

            ScheduleEntry scheduleEntry = entries.get(i);
            int cardY = yOffset;
            int cardHeight = renderScheduleEntry(matrixStack, scheduleEntry, cardY);
            yOffset += cardHeight;

            if (i + 1 < entries.size()) {
                AllGuiTextures.SCHEDULE_STRIP_DOTTED.render(matrixStack, leftPos + 29, topPos + yOffset - 3);
                yOffset += 10;
            }

            matrixStack.popPose();
            endStencil();

            if (!scheduleEntry.instruction.supportsConditions())
                continue;

            float h = cardHeight - 26;
            float y1 = cardY + 24 + scrollOffset;
            float y2 = y1 + h;
            if (y2 > 189)
                h -= y2 - 189;
            if (y1 < TOP_BORDER_WIDTH) {
                float correction = TOP_BORDER_WIDTH - y1;
                y1 += correction;
                h -= correction;
            }

            if (h <= 0)
                continue;

            startStencil(matrixStack, leftPos + 43, topPos + y1, 161, h);
            matrixStack.pushPose();
            matrixStack.translate(0, scrollOffset, 0);
            renderScheduleConditions(matrixStack, scheduleEntry, cardY, partialTicks, cardHeight, i);
            matrixStack.popPose();
            endStencil();

            if (isConditionAreaScrollable(scheduleEntry)) {
                startStencil(matrixStack, leftPos + TOP_BORDER_WIDTH, topPos + TOP_BORDER_WIDTH, CHEST_BORD_WIDTH, CHEST_BORD_HEIGHT);
                matrixStack.pushPose();
                matrixStack.translate(0, scrollOffset, 0);
                int center = (cardHeight - 8 + CARD_HEADER) / 2;
                float chaseTarget = horizontalScrolls.get(i)
                        .getChaseTarget();
                if (!Mth.equal(chaseTarget, 0))
                    AllGuiTextures.SCHEDULE_SCROLL_LEFT.render(matrixStack, leftPos + 40, topPos + cardY + center);
                if (!Mth.equal(chaseTarget, scheduleEntry.conditions.size() - 1))
                    AllGuiTextures.SCHEDULE_SCROLL_RIGHT.render(matrixStack, leftPos + 203, topPos + cardY + center);
                matrixStack.popPose();
                endStencil();
            }
        }
        int zLevel = 200;
        Matrix4f mat = matrixStack.last()
                .pose();
        ScreenUtils.drawGradientRect(mat, zLevel, leftPos + TOP_BORDER_WIDTH, topPos + TOP_BORDER_WIDTH, leftPos + TOP_BORDER_WIDTH + CHEST_BORD_WIDTH, topPos + TOP_BORDER_WIDTH + 10,
                0x77000000, 0x00000000);
        ScreenUtils.drawGradientRect(mat, zLevel, leftPos + TOP_BORDER_WIDTH, topPos + 179, leftPos + TOP_BORDER_WIDTH + CHEST_BORD_WIDTH, topPos + 179 + 10,
                0x00000000, 0x77000000);

        UIRenderHelper.swapAndBlitColor(UIRenderHelper.framebuffer, minecraft.getMainRenderTarget());

    }

    public int renderScheduleEntry(PoseStack matrixStack, ScheduleEntry entry, int yOffset) {
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

        matrixStack.pushPose();
        matrixStack.translate(leftPos + 25, topPos + yOffset, 0);

        UIRenderHelper.drawStretched(matrixStack, 0, 1, cardWidth, cardHeight - 2, zLevel, light);
        UIRenderHelper.drawStretched(matrixStack, 1, 0, cardWidth - 2, cardHeight, zLevel, light);
        UIRenderHelper.drawStretched(matrixStack, 1, 1, cardWidth - 2, cardHeight - 2, zLevel, dark);
        UIRenderHelper.drawStretched(matrixStack, 2, 2, cardWidth - 4, cardHeight - 4, zLevel, medium);
        UIRenderHelper.drawStretched(matrixStack, 2, 2, cardWidth - 4, cardHeader, zLevel,
                supportsConditions ? light : medium);

        AllGuiTextures.SCHEDULE_CARD_REMOVE.render(matrixStack, cardWidth - 14, 2);
        AllGuiTextures.SCHEDULE_CARD_DUPLICATE.render(matrixStack, cardWidth - 14, cardHeight - 14);

        int i = schedule.entries.indexOf(entry);
        if (i > 0)
            AllGuiTextures.SCHEDULE_CARD_MOVE_UP.render(matrixStack, cardWidth, cardHeader - 14);
        if (i < schedule.entries.size() - 1)
            AllGuiTextures.SCHEDULE_CARD_MOVE_DOWN.render(matrixStack, cardWidth, cardHeader);

        UIRenderHelper.drawStretched(matrixStack, 8, 0, 3, cardHeight + 10, zLevel,
                AllGuiTextures.SCHEDULE_STRIP_LIGHT);
        (supportsConditions ? AllGuiTextures.SCHEDULE_STRIP_TRAVEL : AllGuiTextures.SCHEDULE_STRIP_ACTION)
                .render(matrixStack, 4, 6);

        if (supportsConditions)
            AllGuiTextures.SCHEDULE_STRIP_WAIT.render(matrixStack, 4, 28);

        Pair<ItemStack, Component> destination = entry.instruction.getSummary();
        renderInput(matrixStack, destination, 26, 5, false, 100);
        entry.instruction.renderSpecialIcon(matrixStack, 30, 5);

        matrixStack.popPose();

        return cardHeight;
    }

    public void renderScheduleConditions(PoseStack matrixStack, ScheduleEntry entry, int yOffset,
                                         float partialTicks, int cardHeight, int entryIndex) {
        int cardWidth = CARD_WIDTH;
        int cardHeader = CARD_HEADER;

        matrixStack.pushPose();
        matrixStack.translate(leftPos + 25, topPos + yOffset, 0);
        int xOffset = 26;
        float scrollOffset = getConditionScroll(entry, partialTicks, entryIndex);

        matrixStack.pushPose();
        matrixStack.translate(-scrollOffset, 0, 0);

        for (List<ScheduleWaitCondition> list : entry.conditions) {
            int maxWidth = getConditionColumnWidth(list);
            for (int i = 0; i < list.size(); i++) {
                ScheduleWaitCondition scheduleWaitCondition = list.get(i);
                Math.max(maxWidth, renderInput(matrixStack, scheduleWaitCondition.getSummary(), xOffset, 29 + i * 18,
                        i != 0, maxWidth));
                scheduleWaitCondition.renderSpecialIcon(matrixStack, xOffset + 4, 29 + i * 18);
            }

            AllGuiTextures.SCHEDULE_CONDITION_APPEND.render(matrixStack, xOffset + (maxWidth - 10) / 2,
                    29 + list.size() * 18);
            xOffset += maxWidth + 10;
        }

        AllGuiTextures.SCHEDULE_CONDITION_NEW.render(matrixStack, xOffset - 3, 29);
        matrixStack.popPose();

        if (xOffset + TOP_BORDER_WIDTH > cardWidth - 26) {
            TransformStack.cast(matrixStack)
                    .rotateZ(-90);
            Matrix4f m = matrixStack.last()
                    .pose();
            ScreenUtils.drawGradientRect(m, 200, -cardHeight + 2, 18, -2 - cardHeader, 28, 0x44000000, 0x00000000);
            ScreenUtils.drawGradientRect(m, 200, -cardHeight + 2, cardWidth - 26, -2 - cardHeader, cardWidth - TOP_BORDER_WIDTH,
                    0x00000000, 0x44000000);
        }

        matrixStack.popPose();
    }

    private boolean isConditionAreaScrollable(ScheduleEntry entry) {
        int xOffset = 26;
        for (List<ScheduleWaitCondition> list : entry.conditions)
            xOffset += getConditionColumnWidth(list) + 10;
        return xOffset + TOP_BORDER_WIDTH > CARD_WIDTH - 26;
    }

    private float getConditionScroll(ScheduleEntry entry, float partialTicks, int entryIndex) {
        float scrollOffset = 0;
        float scrollIndex = horizontalScrolls.get(entryIndex)
                .getValue(partialTicks);
        for (List<ScheduleWaitCondition> list : entry.conditions) {
            int maxWidth = getConditionColumnWidth(list);
            float partialOfThisColumn = Math.min(1, scrollIndex);
            scrollOffset += (maxWidth + 10) * partialOfThisColumn;
            scrollIndex -= partialOfThisColumn;
        }
        return scrollOffset;
    }

    private int getConditionColumnWidth(List<ScheduleWaitCondition> list) {
        int maxWidth = 0;
        for (ScheduleWaitCondition scheduleWaitCondition : list)
            maxWidth = Math.max(maxWidth, getFieldSize(32, scheduleWaitCondition.getSummary()));
        return maxWidth;
    }

    private int getFieldSize(int minSize, Pair<ItemStack, Component> pair) {
        ItemStack stack = pair.getFirst();
        Component text = pair.getSecond();
        boolean hasItem = !stack.isEmpty();
        return Math.max((text == null ? 0 : font.width(text)) + (hasItem ? 20 : 0) + TOP_BORDER_WIDTH, minSize);
    }

    protected int renderInput(PoseStack matrixStack, Pair<ItemStack, Component> pair, int x, int y, boolean clean,
                              int minSize) {
        ItemStack stack = pair.getFirst();
        Component text = pair.getSecond();
        boolean hasItem = !stack.isEmpty();
        int fieldSize = Math.min(getFieldSize(minSize, pair), 150);
        matrixStack.pushPose();

        AllGuiTextures left =
                clean ? AllGuiTextures.SCHEDULE_CONDITION_LEFT_CLEAN : AllGuiTextures.SCHEDULE_CONDITION_LEFT;
        AllGuiTextures middle = AllGuiTextures.SCHEDULE_CONDITION_MIDDLE;
        AllGuiTextures item = AllGuiTextures.SCHEDULE_CONDITION_ITEM;
        AllGuiTextures right = AllGuiTextures.SCHEDULE_CONDITION_RIGHT;

        matrixStack.translate(x, y, 0);
        UIRenderHelper.drawStretched(matrixStack, 0, 0, fieldSize, TOP_BORDER_WIDTH, -100, middle);
        left.render(matrixStack, clean ? 0 : -3, 0);
        right.render(matrixStack, fieldSize - 2, 0);
        if (hasItem)
            item.render(matrixStack, 3, 0);
        if (hasItem) {
            item.render(matrixStack, 3, 0);
            if (stack.getItem() != Items.STRUCTURE_VOID)
                GuiGameElement.of(stack)
                        .at(4, 0)
                        .render(matrixStack);
        }

        if (text != null)
            font.drawShadow(matrixStack, font.substrByWidth(text, 120)
                    .getString(), hasItem ? 28 : 8, 4, 0xff_f2f2ee);

        matrixStack.popPose();
        return fieldSize;
    }

    //stencil on the outside of the schedule list -> this is really dirty
    protected void startStencil(PoseStack matrixStack, float x, float y, float w, float h) {
        RenderSystem.clear(GL30.GL_STENCIL_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);

        GL11.glDisable(GL11.GL_STENCIL_TEST);
        RenderSystem.stencilMask(~0);
        RenderSystem.clear(GL11.GL_STENCIL_BUFFER_BIT, Minecraft.ON_OSX);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        RenderSystem.stencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);
        RenderSystem.stencilMask(0xFF);
        RenderSystem.stencilFunc(GL11.GL_NEVER, 1, 0xFF);

        matrixStack.pushPose();
        matrixStack.translate(x, y, 0);
        matrixStack.scale(w, h, 1);
        ScreenUtils.drawGradientRect(matrixStack.last()
                .pose(), -100, 0, 0, 1, 1, 0xff000000, 0xff000000);
        matrixStack.popPose();

        GL11.glEnable(GL11.GL_STENCIL_TEST);
        RenderSystem.stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        RenderSystem.stencilFunc(GL11.GL_EQUAL, 1, 0xFF);
    }

    protected void endStencil() {
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    protected void startEditing(IScheduleInput field, Consumer<Boolean> onClose, boolean allowDeletion) {
        onEditorClose = onClose;
        cyclicButton.visible = false;
        cyclicIndicator.visible = false;
        skipProgress.visible = false;
        resetProgress.visible = false;

        scrollInput = new SelectionScrollInput(leftPos + 56, topPos + 65, 143, TOP_BORDER_WIDTH);
        scrollInputLabel = new Label(leftPos + 59, topPos + 69, Components.immutableEmpty()).withShadow();
        editorConfirm = new IconButton(leftPos + 56 - 45, topPos + 65, AllIcons.I_CONFIRM);
        if (allowDeletion)
            editorDelete = new IconButton(leftPos + 56 - 45, topPos + 65 + 22, AllIcons.I_TRASH);


        if (field instanceof ScheduleInstruction instruction) {
            int startIndex = 0;
            for (int i = 0; i < RocketSchedule.INSTRUCTION_TYPES.size(); i++)
                if (RocketSchedule.INSTRUCTION_TYPES.get(i)
                        .getFirst()
                        .equals(instruction.getId()))
                    startIndex = i;
            editingDestination = instruction;
            updateEditorSubwidgets(editingDestination);
            //TODO change this to a selection in the map
            //System.out.println("start edit");

            scrollInput.forOptions(RocketSchedule.getTypeOptions(RocketSchedule.INSTRUCTION_TYPES))
                    .titled(Lang.translateDirect("schedule.instruction_type"))
                    .writingTo(scrollInputLabel)
                    .calling(index -> {
                        ScheduleInstruction newlyCreated = RocketSchedule.INSTRUCTION_TYPES.get(index)
                                .getSecond()
                                .get();
                        if (editingDestination.getId()
                                .equals(newlyCreated.getId()))
                            return;
                        editingDestination = newlyCreated;
                        updateEditorSubwidgets(editingDestination);
                    })
                    .setState(startIndex);
        }

        if (field instanceof ScheduleWaitCondition cond) {
            int startIndex = 0;
            for (int i = 0; i < RocketSchedule.CONDITION_TYPES.size(); i++)
                if (RocketSchedule.CONDITION_TYPES.get(i)
                        .getFirst()
                        .equals(cond.getId()))
                    startIndex = i;
            editingCondition = cond;
            updateEditorSubwidgets(editingCondition);
            scrollInput.forOptions(RocketSchedule.getTypeOptions(RocketSchedule.CONDITION_TYPES))
                    .titled(Lang.translateDirect("schedule.condition_type"))
                    .writingTo(scrollInputLabel)
                    .calling(index -> {
                        ScheduleWaitCondition newlyCreated = RocketSchedule.CONDITION_TYPES.get(index)
                                .getSecond()
                                .get();
                        if (editingCondition.getId()
                                .equals(newlyCreated.getId()))
                            return;
                        editingCondition = newlyCreated;
                        updateEditorSubwidgets(editingCondition);
                    })
                    .setState(startIndex);
        }

        addRenderableWidget(scrollInput);
        addRenderableWidget(scrollInputLabel);
        addRenderableWidget(editorConfirm);
        if (allowDeletion)
            addRenderableWidget(editorDelete);
    }

    protected void updateEditorSubwidgets(IScheduleInput field) {
        //destinationSuggestions = null;
        //TODO look at the released version to find the bug free version of this function
        editorSubWidgets.forEach(this::removeWidget);
        editorSubWidgets.clear();
        field.initConfigurationWidgets(
                new ModularGuiLineBuilder(font, editorSubWidgets, leftPos + 77, topPos + 92).speechBubble());
        editorSubWidgets.loadValues(field.getData(), this::addRenderableWidget, this::addRenderableOnly);

        if (!(field instanceof DestinationInstruction))
            return;
        editorSubWidgets.forEach(
                e -> {
                    if (!(e instanceof ScrollInput))
                        return;
                    ((ScrollInput) e).calling(i -> {
                                destination = CSDimensionUtil.getPlanets().get(i);
                                destinationCost.setTextAndTrim(
                                        Component.literal(
                                                String.valueOf(CSDimensionUtil.cost(currentDimension, destination))),
                                        true, 112);
                                destinationChanged = true;
                            }
                    );

                }
        );


        /*editorSubWidgets.forEach(e -> {
            if (!(e instanceof EditBox destinationBox))
                return;
            destinationSuggestions = new DestinationSuggestions(this.minecraft, this, destinationBox, this.font,
                    getViableDestination(field), topPos + 33);
            destinationSuggestions.setAllowSuggestions(true);
            destinationSuggestions.updateCommandInfo();
            destinationBox.setResponder(this::onDestinationEdited);
        });*/
    }

    private Component clickToEdit = Lang.translateDirect("gui.schedule.lmb_edit")
            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);
    private Component rClickToDelete = Lang.translateDirect("gui.schedule.rmb_remove")
            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC);

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (editorConfirm != null && editorConfirm.isMouseOver(pMouseX, pMouseY) && onEditorClose != null) {
            onEditorClose.accept(true);
            stopEditing();
            return true;
        }
        if (editorDelete != null && editorDelete.isMouseOver(pMouseX, pMouseY) && onEditorClose != null) {
            onEditorClose.accept(false);
            stopEditing();
            return true;
        }
        if (action(new PoseStack(), pMouseX, pMouseY, pButton))
            return true;

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (editingCondition != null || editingDestination != null)
            return super.mouseScrolled(pMouseX, pMouseY, pDelta);

        if (hasShiftDown()) {
            List<ScheduleEntry> entries = schedule.entries;
            int y = (int) (pMouseY - topPos - 25 + scroll.getValue());
            for (int i = 0; i < entries.size(); i++) {
                ScheduleEntry entry = entries.get(i);
                int maxRows = 0;
                for (List<ScheduleWaitCondition> list : entry.conditions)
                    maxRows = Math.max(maxRows, list.size());
                int cardHeight = CARD_HEADER + 24 + maxRows * 18;

                if (y >= cardHeight) {
                    y -= cardHeight + 9;
                    if (y < 0)
                        break;
                    continue;
                }

                if (!isConditionAreaScrollable(entry))
                    break;
                if (y < 24)
                    break;
                if (pMouseX < leftPos + 25)
                    break;
                if (pMouseX > topPos + 205)
                    break;
                float chaseTarget = horizontalScrolls.get(i)
                        .getChaseTarget();
                if (pDelta > 0 && !Mth.equal(chaseTarget, 0)) {
                    horizontalScrolls.get(i)
                            .chase(chaseTarget - 1, 0.5f, LerpedFloat.Chaser.EXP);
                    return true;
                }
                if (pDelta < 0 && !Mth.equal(chaseTarget, entry.conditions.size() - 1)) {
                    horizontalScrolls.get(i)
                            .chase(chaseTarget + 1, 0.5f, LerpedFloat.Chaser.EXP);
                    return true;
                }
                return false;
            }
        }

        float chaseTarget = scroll.getChaseTarget();
        float max = 40 - CHEST_BORD_HEIGHT;
        for (ScheduleEntry scheduleEntry : schedule.entries) {
            int maxRows = 0;
            for (List<ScheduleWaitCondition> list : scheduleEntry.conditions)
                maxRows = Math.max(maxRows, list.size());
            max += CARD_HEADER + 24 + maxRows * 18 + 10;
        }
        if (max > 0) {
            chaseTarget -= pDelta * 12;
            chaseTarget = Mth.clamp(chaseTarget, 0, max);
            scroll.chase((int) chaseTarget, 0.7f, LerpedFloat.Chaser.EXP);
        } else
            scroll.chase(0, 0.7f, LerpedFloat.Chaser.EXP);

        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    //used for mouse clicking (hard coded widgets)
    public boolean action(PoseStack ms, double mouseX, double mouseY, int click) {

        if (editingCondition != null)
            return false;

        Component empty = Components.immutableEmpty();

        int mx = (int) mouseX;
        int my = (int) mouseY;
        int x = mx - leftPos - 25;
        int y = my - topPos - 25;
        int x2 = width - mx;


        if (editingDestination != null) {
            if (x2 > 50 && x2 < 100) {
                List<Component> components = new ArrayList<>();
                if (y > 36 && y < 52) {
                    components.add(Component.translatable("creatingspace.gui.rocket_controls.x_entry_coord"));
                    renderTooltip(ms, components, Optional.empty(), mx, my);
                }
                if (y > 56 && y < 73) {
                    components.add(Component.translatable("creatingspace.gui.rocket_controls.z_entry_coord"));
                    renderTooltip(ms, components, Optional.empty(), mx, my);
                }
            }
            return false;
        }

        if (x < 0 || x >= 205)
            return false;
        if (y < 0 || y >= CHEST_BORD_HEIGHT)
            return false;
        y += scroll.getValue(0);

        List<ScheduleEntry> entries = schedule.entries;
        for (int i = 0; i < entries.size(); i++) {
            ScheduleEntry entry = entries.get(i);
            int maxRows = 0;
            for (List<ScheduleWaitCondition> list : entry.conditions)
                maxRows = Math.max(maxRows, list.size());
            int cardHeight = CARD_HEADER + (entry.instruction.supportsConditions() ? 24 + maxRows * 18 : 4);

            if (y >= cardHeight + 5) {
                y -= cardHeight + 10;
                if (y < 0)
                    return false;
                continue;
            }

            int fieldSize = getFieldSize(100, entry.instruction.getSummary());
            if (x > 25 && x <= 25 + fieldSize && y > 4 && y <= 20) {
                List<Component> components = new ArrayList<>();
                components.addAll(entry.instruction.getTitleAs("instruction"));
                components.add(empty);
                components.add(clickToEdit);
                renderTooltip(ms, components, Optional.empty(), mx, my);
                if (click == 0)
                    startEditing(entry.instruction, confirmed -> {
                        if (confirmed)
                            entry.instruction = editingDestination;
                    }, false);
                return true;
            }
            // this should be in the screen class
            if (x > 180 && x <= 192) {
                if (y > 0 && y <= 14) {
                    renderTooltip(ms, ImmutableList.of(Lang.translateDirect("gui.schedule.remove_entry")), Optional.empty(),
                            mx, my);
                    if (click == 0) {
                        entries.remove(entry);
                        init();
                    }
                    return true;
                }
                if (y > cardHeight - 14) {
                    renderTooltip(ms, ImmutableList.of(Lang.translateDirect("gui.schedule.duplicate")), Optional.empty(), mx,
                            my);
                    if (click == 0) {
                        entries.add(entries.indexOf(entry), entry.clone());
                        init();
                    }
                    return true;
                }
            }

            if (x > 194) {
                if (y > 7 && y <= 20 && i > 0) {
                    renderTooltip(ms, ImmutableList.of(Lang.translateDirect("gui.schedule.move_up")), Optional.empty(), mx,
                            my);
                    if (click == 0) {
                        entries.remove(entry);
                        entries.add(i - 1, entry);
                        init();
                    }
                    return true;
                }
                if (y > 20 && y <= SIDE_BORDER_WIDTH && i < entries.size() - 1) {
                    renderTooltip(ms, ImmutableList.of(Lang.translateDirect("gui.schedule.move_down")), Optional.empty(), mx,
                            my);
                    if (click == 0) {
                        entries.remove(entry);
                        entries.add(i + 1, entry);
                        init();
                    }
                    return true;
                }
            }

            int center = (cardHeight - 8 + CARD_HEADER) / 2;
            if (y > center - 1 && y <= center + 7 && isConditionAreaScrollable(entry)) {
                float chaseTarget = horizontalScrolls.get(i)
                        .getChaseTarget();
                if (x > 12 && x <= 19 && !Mth.equal(chaseTarget, 0)) {
                    if (click == 0)
                        horizontalScrolls.get(i)
                                .chase(chaseTarget - 1, 0.5f, LerpedFloat.Chaser.EXP);
                    return true;
                }
                if (x > 177 && x <= 184 && !Mth.equal(chaseTarget, entry.conditions.size() - 1)) {
                    if (click == 0)
                        horizontalScrolls.get(i)
                                .chase(chaseTarget + 1, 0.5f, LerpedFloat.Chaser.EXP);
                    return true;
                }
            }

            x -= 18;
            y -= 28;
            if (x < 0 || y < 0 || x > 160)
                return false;
            x += getConditionScroll(entry, 0, i) - 8;

            List<List<ScheduleWaitCondition>> columns = entry.conditions;
            for (int j = 0; j < columns.size(); j++) {
                List<ScheduleWaitCondition> conditions = columns.get(j);
                if (x < 0)
                    return false;
                int w = getConditionColumnWidth(conditions);
                if (x >= w) {
                    x -= w + 10;
                    continue;
                }

                int row = y / 18;
                if (row < conditions.size() && row >= 0) {
                    boolean canRemove = conditions.size() > 1 || columns.size() > 1;
                    List<Component> components = new ArrayList<>();
                    components.add(Lang.translateDirect("schedule.condition_type")
                            .withStyle(ChatFormatting.GRAY));
                    ScheduleWaitCondition condition = conditions.get(row);
                    components.addAll(condition.getTitleAs("condition"));
                    components.add(empty);
                    components.add(clickToEdit);
                    if (canRemove)
                        components.add(rClickToDelete);
                    renderTooltip(ms, components, Optional.empty(), mx, my);
                    if (canRemove && click == 1) {
                        conditions.remove(row);
                        if (conditions.isEmpty())
                            columns.remove(conditions);
                    }
                    if (click == 0)
                        startEditing(condition, confirmed -> {
                            conditions.remove(row);
                            if (confirmed) {
                                conditions.add(row, editingCondition);
                                return;
                            }
                            if (conditions.isEmpty())
                                columns.remove(conditions);
                        }, canRemove);
                    return true;
                }

                if (y > 18 * conditions.size() && y <= 18 * conditions.size() + 10 && x >= w / 2 - 5 && x < w / 2 + 5) {
                    renderTooltip(ms, ImmutableList.of(Lang.translateDirect("gui.schedule.add_condition")), Optional.empty(),
                            mx, my);
                    if (click == 0)
                        startEditing(new ScheduledDelay(), confirmed -> {
                            if (confirmed)
                                conditions.add(editingCondition);
                        }, true);
                    return true;
                }

                return false;
            }

            if (x < 0 || x > 15 || y > 20)
                return false;

            renderTooltip(ms, ImmutableList.of(Lang.translateDirect("gui.schedule.alternative_condition")), Optional.empty(),
                    mx, my);
            if (click == 0)
                startEditing(new ScheduledDelay(), confirmed -> {
                    if (!confirmed)
                        return;
                    ArrayList<ScheduleWaitCondition> conditions = new ArrayList<>();
                    conditions.add(editingCondition);
                    columns.add(conditions);
                }, true);
            return true;
        }

        if (x < 18 || x > SIDE_BORDER_WIDTH || y > 14)
            return false;

        renderTooltip(ms, ImmutableList.of(Lang.translateDirect("gui.schedule.add_entry")), Optional.empty(), mx, my);
        if (click == 0) {
            startEditing(new DestinationInstruction(), confirmed -> {
                if (!confirmed)
                    return;

                ScheduleEntry entry = new ScheduleEntry();
                ScheduledDelay delay = new ScheduledDelay();
                ArrayList<ScheduleWaitCondition> initialConditions = new ArrayList<>();
                initialConditions.add(delay);
                entry.instruction = editingDestination;
                entry.conditions.add(initialConditions);
                schedule.entries.add(entry);
            }, true);
        }
        return true;
    }

    //maybe on save data ? (menu logic rather than screen logic)
    //do a ::setSchedule on the menu and then on save data send the schedule to the rocket
    // (there is a need for a sync on the entity side : sync data ?)
    @Override
    public void removed() {
        PacketInit.getChannel().sendToServer(new RocketScheduleEditPacket(schedule, getMenu().contentHolder.getId()));
        //set the client side schedule
        getMenu().contentHolder.schedule.setSchedule(schedule, true);
        super.removed();
    }

    protected void stopEditing() {
        cyclicButton.visible = true;
        cyclicIndicator.visible = true;
        skipProgress.visible = true;
        resetProgress.visible = true;

        if (editingCondition == null && editingDestination == null)
            return;

        removeWidget(scrollInput);
        removeWidget(scrollInputLabel);
        removeWidget(editorConfirm);
        removeWidget(editorDelete);

        IScheduleInput editing = editingCondition == null ? editingDestination : editingCondition;

        editorSubWidgets.saveValues(editing.getData());
        editorSubWidgets.forEach(this::removeWidget);
        editorSubWidgets.clear();

        editingCondition = null;
        editingDestination = null;
        editorConfirm = null;
        editorDelete = null;
        init();
    }

}
