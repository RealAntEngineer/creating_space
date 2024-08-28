package com.rae.creatingspace.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import com.rae.creatingspace.client.gui.screen.elements.DimSelectBoxWidget;
import com.rae.creatingspace.client.gui.screen.elements.LabeledBoxWidget;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.server.blockentities.RocketControlsBlockEntity;
import com.rae.creatingspace.utilities.CSDimensionUtil;
import com.rae.creatingspace.utilities.CSUtil;
import com.rae.creatingspace.utilities.packet.RocketAssemblePacket;
import com.rae.creatingspace.utilities.packet.RocketControlsSettingsPacket;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class DestinationScreen extends AbstractSimiScreen {
    private boolean destinationChanged;
    private Button launchButton;
    private final HashMap<ResourceLocation, RocketAccessibleDimension.AccessibilityParameter> mapOfAccessibleDimensionAndV;
    HashMap<String, BlockPos> initialPosMap;
    private final RocketControlsBlockEntity blockEntity;
    private final GuiTexturesInit background;
    private final ResourceLocation currentDimension;
    private ResourceLocation destination;

    private final Vector<DimSelectBoxWidget> buttonVector;
    private LabeledBoxWidget destinationCost;
    private EditBox Xinput;
    private EditBox Zinput;
    Couple<Color> red = Theme.p(Theme.Key.BUTTON_FAIL);
    Couple<Color> green = Theme.p(Theme.Key.BUTTON_SUCCESS);
    private IconButton validateSetting;


    public DestinationScreen(RocketControlsBlockEntity be) {
        super(Lang.translateDirect("gui.destination_screen.title"));
        this.blockEntity = be;
        this.initialPosMap = new HashMap<>(be.initialPosMap);
        this.background = GuiTexturesInit.ROCKET_CONTROLS;
        this.currentDimension = be.getLevel().dimension().location();
        //initialise the map in the server side blockEntity to avoid issues
        this.mapOfAccessibleDimensionAndV = new HashMap<>(CSDimensionUtil.getTravelMap().get(currentDimension).adjacentDimensions());
        this.buttonVector = new Vector<>(this.mapOfAccessibleDimensionAndV.size());
        this.destinationChanged = false;
    }

    @Override
    protected void init() {
        setWindowSize(226,226);
        super.init();
        int x = guiLeft;
        int y = guiTop;

        launchButton = new ExtendedButton(x + 141,y + 198,16*4,20,
                Component.translatable("creatingspace.gui.rocket_controls.launch"),
        ($) -> {
            if(destination ==null){
                return;
            }
            PacketInit.getChannel()
                    .sendToServer(RocketAssemblePacket.tryAssemble(blockEntity.getBlockPos(),destination));
            onClose();
        });

        addRenderableWidget(launchButton);
        destinationCost = new LabeledBoxWidget(x+172,y+20,Component.literal("  500 "));

        validateSetting = new IconButton(x + 192,y + 103, AllIcons.I_CONFIG_SAVE);
        validateSetting.setToolTip(
                Component.translatable("creatingspace.gui.rocket_controls.send_setting"));
        validateSetting.withCallback(() -> {
                    BlockPos pos =  initialPosMap.get(String.valueOf(destination));
                    if (pos == null) {
                        pos =  this.blockEntity.getBlockPos();
                    }
                    String X = Xinput.getValue().replace(" ",""),/*Y = Yinput.getValue().replace(" ",""),*/Z = Zinput.getValue().replace(" ","");
                    if (CSUtil.isInteger(X)){
                        pos =new BlockPos(Integer.parseInt(X),pos.getY(),pos.getZ());
                    }
                    else {
                        Xinput.setValue(String.valueOf(pos.getX()));
                    }
                    /*if (isInteger(Y)){
                        pos = pos.mutable().setY(Integer.parseInt(Y)).immutable();
                    }else {
                        Yinput.setValue(String.valueOf(pos.getY()));
                    }*/
                    if (CSUtil.isInteger(Z)){
                        pos = pos.mutable().setZ(Integer.parseInt(Z)).immutable();
                    }else {
                        Zinput.setValue(String.valueOf(pos.getZ()));
                    }

                    initialPosMap.put(String.valueOf(destination),pos);
                    PacketInit.getChannel().sendToServer(RocketControlsSettingsPacket.sendSettings(this.blockEntity.getBlockPos(),initialPosMap));
                });

        Xinput = new EditBox(font, x + 169, y + 63,
                50, 14, Component.literal(""));
        /*Yinput = new EditBox(font,x + 169, y + 63,
                50, 14, Component.literal(""));*/
        Zinput = new  EditBox(font,x + 169, y + 83,
                50, 14, Component.literal(""));

        addRenderableWidget(Xinput);
        //addRenderableWidget(Yinput);
        addRenderableWidget(Zinput);
        addRenderableWidget(validateSetting);
        addRenderableWidget(destinationCost);

        Component text;
        for (int row = 0; row < mapOfAccessibleDimensionAndV.size(); row++) {
            ResourceLocation dim = mapOfAccessibleDimensionAndV.keySet().stream().toList().get(row);
            text = Component.translatable(dim.toString());
            DimSelectBoxWidget widget = new DimSelectBoxWidget(x+7,y+20+26*row,134,16,text,dim);
            widget.withCallback(
                    ()-> {
                        destination = widget.getDim();
                        destinationChanged = true;
                    }
            );
            buttonVector.add(
                    row,
                    widget);
            addRenderableWidget(buttonVector.get(row));
        }

    }



    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);


        if (destination != null){
            if (destinationChanged) {
                BlockPos pos = initialPosMap.get(String.valueOf(destination));
                if (pos == null) {
                    pos =  this.blockEntity.getBlockPos();
                }
                Xinput.setValue(String.valueOf(pos.getX()));
                //Yinput.setValue(String.valueOf(pos.getY()));
                Zinput.setValue(String.valueOf(pos.getZ()));
            }
            Xinput.visible = true;
            Xinput.active = true;
            graphics.drawString(font,Component.translatable("creatingspace.gui.rocket_controls.pos_selection"),x+166,y+43,0xFFFFFF);
            graphics.drawString(font,Component.literal("X :"),x+156,y+63,0xFFFFFF);
            graphics.drawString(font,Component.literal("Z :"),x+156,y+83,0xFFFFFF);

            //Yinput.visible = true;
            //Yinput.active = true;
            Zinput.visible = true;
            Zinput.active = true;

            launchButton.active = true;
            validateSetting.active = true;
            validateSetting.visible = true;
            destinationCost.visible = true;
            destinationCost.setTextAndTrim(
                    Component.literal(
                            String.valueOf(mapOfAccessibleDimensionAndV.get(destination).deltaV())),
                    true, 112);

        }else {
            Xinput.visible = false;
            Xinput.active = false;
            //Yinput.visible = false;
            //Yinput.active = false;
            Zinput.visible = false;
            Zinput.active = false;
            launchButton.active = false;
            validateSetting.active = false;
            validateSetting.visible = false;
            destinationCost.visible = false;
        }

        for (int row = 0; row < mapOfAccessibleDimensionAndV.size(); row++) {
            DimSelectBoxWidget widget = buttonVector.get(row);
            if (destination == widget.getDim()){
                widget.withBorderColors(green);
            } else if(destination!=null){
                widget.withBorderColors(red);
            }
        }

        destinationChanged = false;
    }

    @Override
    public void tick() {
        super.tick();
        handleTooltips();
    }

    protected void handleTooltips() {
        if (destination == null)
            return;

        if (destinationCost != null) {
            LabeledBoxWidget button = destinationCost;
            Component tooltipText = Component.translatable("creatingspace.gui.rocket_controls.destination_cost");
            // Check if the tooltip already contains the desired text
            if (!button.getToolTip().contains(tooltipText)) {
                button.getToolTip().add(tooltipText);
            }
        }
    }

    private void fillToolTip(IconButton button, String tooltipKey) {
        if (!button.isHoveredOrFocused())
            return;
        List<Component> tip = button.getToolTip();

        tip.addAll(TooltipHelper
                .cutTextComponent(Component.translatable("creatingspace.gui.rocket_controls." + tooltipKey + ".description"), TooltipHelper.Palette.ALL_GRAY));
    }
}
