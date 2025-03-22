package com.rae.creatingspace.legacy.client.gui.screen;

import com.rae.creatingspace.api.gui.elements.BackgroundScrollInput;
import com.rae.creatingspace.api.gui.elements.SliderWidget;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.legacy.server.blockentities.atmosphere.SealerBlockEntity;
import com.rae.creatingspace.legacy.utilities.packet.SealerSettings;
import com.rae.creatingspace.legacy.utilities.packet.SealerTrySealing;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class SealerScreen extends AbstractSimiScreen {

    private Button startButton;
    private boolean isSetting = false;
    private Indicator sealedIndicator;
    private SliderWidget o2Gauge;
    private IconButton settingButton;
    private Indicator settingIndicator;
    private ScrollInput setRangeInput;
    private Label setRangeLabel;
    private IconButton setRetryButton;
    private Indicator retryIndicator;
    private final SealerBlockEntity blockEntity;
    private final GuiTexturesInit background;

    //for settings

    private int rangeSetting = 10;
    private boolean retrySetting = false;


    public SealerScreen(SealerBlockEntity be) {
        super(Lang.translateDirect("gui.destination_screen.title"));
        this.blockEntity = be;
        this.retrySetting = be.isAutomaticRetry();
        this.rangeSetting = be.getRange();
        this.background = GuiTexturesInit.SEALER_BACKGROUND;
    }

    @Override
    protected void init() {
        setWindowSize(226,124);
        super.init();
        int x = guiLeft;
        int y = guiTop;

        startButton = new ExtendedButton(x + 153,y + 100,16*4,20,
                Component.translatable("creatingspace.gui.sealer.try_seal"),
        ($) -> {
            PacketInit.getChannel().sendToServer(SealerTrySealing.trySealing(blockEntity.getBlockPos()));
        });

        addRenderableWidget(startButton);

        settingButton = new IconButton(x+6,y+100, AllIcons.I_PLACEMENT_SETTINGS);
        settingButton.withCallback(this::switchSetting);
        settingButton.setToolTip(Component.translatable("creatingspace.gui.sealer.settings"));

        setRangeLabel = new Label(x + 80, y + 100+9-4, Components.immutableEmpty()).withShadow();


        setRangeInput = new BackgroundScrollInput(x+70,y+100,64,18);
        setRangeInput
                .withRange(0,1000)
                .writingTo(setRangeLabel)
                .titled(Component.translatable("creatingspace.gui.sealer.range"))
                .calling(
                value -> {
                    rangeSetting = value;
                    sendSettings(blockEntity.getBlockPos());
                })
                .setState(rangeSetting);

        setRangeInput.visible = false;
        setRangeInput.active = false;
        setRangeLabel.visible = false;
        setRangeInput.onChanged();
        addRenderableWidget(setRangeInput);
        addRenderableWidget(setRangeLabel);

        setRetryButton = new IconButton(x+48,y+100, AllIcons.I_PLACEMENT_SETTINGS);
        setRetryButton.withCallback(
                () -> {
                    retrySetting = !retrySetting;
                    sendSettings(blockEntity.getBlockPos());
                }
        );
        setRetryButton.setToolTip(Component.translatable("creatingspace.gui.sealer.automatic_retry"));

        setRetryButton.visible = false;

        addRenderableWidget(settingButton);
        //addRenderableWidget(setRangeButton);
        addRenderableWidget(setRetryButton);

        settingIndicator = new Indicator(x+6,y+94,Component.translatable("creatingspace.gui.sealer.settings"));

        retryIndicator = new Indicator(x+48,y+94,Component.translatable("creatingspace.gui.sealer.automatic_retry"));

        retryIndicator.visible = false;

        addRenderableWidget(settingIndicator);
        addRenderableWidget(retryIndicator);

        o2Gauge = new SliderWidget(x+6,y+19,32,64);

        addRenderableWidget(o2Gauge);

        sealedIndicator = new Indicator(x+199,y+94,Component.translatable("creatingspace.gui.sealer.isSealed"));
        addRenderableWidget(sealedIndicator);
        //add boolean in SealerBlockEntity -> trySealing
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);

        o2Gauge.setValues(blockEntity.o2amount,blockEntity.prevO2amount);

        boolean roomIsSealed = blockEntity.roomIsSealed;
        sealedIndicator.state = roomIsSealed ? Indicator.State.GREEN : Indicator.State.RED;
        retryIndicator.state = retrySetting ? Indicator.State.GREEN : Indicator.State.RED;


        startButton.active = !blockEntity.isTrying();

        int nbrOfBlock = blockEntity.lastRoomSize;

        float speed = blockEntity.getSpeed();

        graphics.pose().pushPose();

        graphics.drawString(font,
                String.valueOf(nbrOfBlock),
                x + 139, y + 55, 0xFFFFFF, false);
        graphics.drawString(font,
                String.valueOf(SealerBlockEntity.o2consumption(nbrOfBlock)),
                x + 73, y + 43, 0xFFFFFF, false);
        int speedRequirement = SealerBlockEntity.speedRequirement(nbrOfBlock);
        graphics.drawString(font,
                String.valueOf(speedRequirement),
                x + 73, y + 66, Math.abs(speed) >= speedRequirement ? 0x00FF55: 0xFF5500, false);
        graphics.pose().popPose();

    }


    public void switchSetting() {
        isSetting = !isSetting;

        settingIndicator.state = isSetting ? Indicator.State.ON : Indicator.State.OFF;

        //setRangeButton.visible = isSetting;
        setRangeInput.visible = isSetting;
        setRangeInput.active = isSetting;
        setRangeLabel.visible = isSetting;

        setRetryButton.visible = isSetting;
        retryIndicator.visible = isSetting;

    }

    public void sendSettings(BlockPos blockEntityPos){
        PacketInit.getChannel()
                .sendToServer(
                        SealerSettings
                                .sendSettings(blockEntityPos, rangeSetting, retrySetting));
    }
}
