package com.rae.creatingspace.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.client.gui.screen.elements.DimSelectBoxWidget;
import com.rae.creatingspace.client.gui.screen.elements.SliderWidget;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import com.rae.creatingspace.server.blockentities.atmosphere.SealerBlockEntity;
import com.rae.creatingspace.utilities.packet.SealerSettings;
import com.rae.creatingspace.utilities.packet.SealerTrySealing;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.gui.widget.*;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.gui.components.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Vector;

public class SealerScreen extends AbstractSimiScreen {

    private Button startButton;
    private boolean isSetting = false;
    private Indicator sealedIndicator;
    private SliderWidget o2Gauge;
    private IconButton settingButton;
    private Indicator settingIndicator;
    private IconButton setRangeButton;
    private ScrollInput setRangeInput;
    private IconButton setRetryButton;
    private Indicator retryIndicator;
    private final SealerBlockEntity blockEntity;
    private final GuiTexturesInit background;

    //for settings

    private int rangeSettings = 10;
    private boolean retrySettings = false;


    public SealerScreen(SealerBlockEntity be) {
        super(Lang.translateDirect("gui.destination_screen.title"));
        this.blockEntity = be;
        this.retrySettings = be.isAutomaticRetry();
        this.rangeSettings = be.getRange();
        this.background = GuiTexturesInit.SEALER_BACKGROUND;
    }

    @Override
    protected void init() {
        setWindowSize(226,124);
        super.init();
        int x = guiLeft;
        int y = guiTop;

        startButton = new Button(x + 153,y + 100,16*4,20,
                Component.translatable(CreatingSpace.MODID+".try_seal"),
        ($) -> {
            PacketInit.getChannel().sendToServer(SealerTrySealing.trySealing(blockEntity.getBlockPos()));
        });

        addRenderableWidget(startButton);

        settingButton = new IconButton(x+6,y+100, AllIcons.I_PLACEMENT_SETTINGS);
        settingButton.withCallback(this::switchSetting);
        settingButton.setToolTip(Component.literal("settings"));

        setRangeButton = new IconButton(x+30,y+100, AllIcons.I_PLACEMENT_SETTINGS);
        setRangeButton.withCallback(
                () -> {
                    rangeSettings = 10;
                    sendSettings(blockEntity.getBlockPos());
                }
        );
        setRangeButton.visible = false;

        setRangeInput = new ScrollInput(x+60,y+100,64,18);
        setRangeInput
                .withRange(0,1000)
                .calling(
                value -> {
                    rangeSettings = value;
                    sendSettings(blockEntity.getBlockPos());
                })
                .writingTo(new Label(x+60,x+100,Component.literal("range")))
                .setState(rangeSettings);
        setRangeInput.visible = false;
        addRenderableWidget(setRangeInput);

        setRetryButton = new IconButton(x+48,y+100, AllIcons.I_PLACEMENT_SETTINGS);
        setRetryButton.withCallback(
                () -> {
                    retrySettings = !retrySettings;
                    sendSettings(blockEntity.getBlockPos());
                }
        );
        setRetryButton.visible = false;

        addRenderableWidget(settingButton);
        addRenderableWidget(setRangeButton);
        addRenderableWidget(setRetryButton);

        settingIndicator = new Indicator(x+6,y+94,Component.literal("isSetting"));

        retryIndicator = new Indicator(x+48,y+94,Component.literal("retry"));

        retryIndicator.visible = false;

        addRenderableWidget(settingIndicator);
        addRenderableWidget(retryIndicator);

        o2Gauge = new SliderWidget(x+6,y+19,32,64);

        addRenderableWidget(o2Gauge);

        sealedIndicator = new Indicator(x+199,y+94,Component.translatable("creatingspace.isSealed"));

        addRenderableWidget(sealedIndicator);
        //add boolean in SealerBlockEntity -> trySealing
    }

    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(ms, x, y, this);

        o2Gauge.setValues(blockEntity.o2amount,blockEntity.prevO2amount);

        boolean roomIsSealed = blockEntity.roomIsSealed;
        sealedIndicator.state = roomIsSealed ? Indicator.State.GREEN : Indicator.State.RED;
        retryIndicator.state = retrySettings ? Indicator.State.GREEN : Indicator.State.RED;



        startButton.active = !blockEntity.isTrying();

        int nbrOfBlock = blockEntity.lastRoomSize;

        float speed = blockEntity.getSpeed();

        ms.pushPose();

        font.draw(ms,
                String.valueOf(nbrOfBlock),
                x + 139, y + 56, 0xFFFFFF);
        font.draw(ms,
                String.valueOf(SealerBlockEntity.o2consumption(nbrOfBlock)),
                x + 73, y + 44, 0xFFFFFF);
        int speedRequirement = SealerBlockEntity.speedRequirement(nbrOfBlock);
        font.draw(ms,
                String.valueOf(speedRequirement),
                x + 73, y + 65, Math.abs(speed) >= speedRequirement ? 0x00FF55: 0xFF5500);

        ms.popPose();

    }


    public void switchSetting() {
        isSetting = !isSetting;

        settingIndicator.state = isSetting ? Indicator.State.ON : Indicator.State.OFF;

        setRangeButton.visible = isSetting;
        setRangeInput.visible = isSetting;
        setRetryButton.visible = isSetting;
        retryIndicator.visible = isSetting;

    }

    public void sendSettings(BlockPos blockEntityPos){
        PacketInit.getChannel()
                .sendToServer(
                        SealerSettings
                                .sendSettings(blockEntityPos,rangeSettings,retrySettings));
    }
}
