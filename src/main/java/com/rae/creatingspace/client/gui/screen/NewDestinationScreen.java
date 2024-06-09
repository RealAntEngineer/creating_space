package com.rae.creatingspace.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.api.gui.Orbit;
import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import com.rae.creatingspace.client.gui.screen.elements.LabeledBoxWidget;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.rae.creatingspace.utilities.CSDimensionUtil;
import com.rae.creatingspace.utilities.CSUtil;
import com.rae.creatingspace.utilities.packet.RocketContraptionLaunchPacket;
import com.rae.creatingspace.utilities.packet.RocketControlsSettingsPacket;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class NewDestinationScreen extends AbstractSimiScreen {
    private boolean destinationChanged;
    private Button launchButton;
    //private final HashMap<ResourceLocation, RocketAccessibleDimension.AccessibilityParameter> mapOfAccessibleDimensionAndV;
    HashMap<String, BlockPos> initialPosMap;
    private final RocketContraptionEntity rocketContraption;
    private final GuiTexturesInit background;
    private final ResourceLocation currentDimension;
    private ResourceLocation destination;

    private final Vector<Orbit> buttonVector;
    private LabeledBoxWidget destinationCost;
    private EditBox Xinput;
    private EditBox Zinput;
    Couple<Color> red = Theme.p(Theme.Key.BUTTON_FAIL);
    Couple<Color> green = Theme.p(Theme.Key.BUTTON_SUCCESS);
    private IconButton validateSetting;


    public NewDestinationScreen(RocketContraptionEntity rocket) {
        super(Lang.translateDirect("gui.destination_screen.title"));
        this.rocketContraption = rocket;
        this.initialPosMap = new HashMap<>(rocket.getInitialPosMap());
        this.background = GuiTexturesInit.ROCKET_CONTROLS;
        this.currentDimension = rocket.getLevel().dimension().location();
        //initialise the map in the server side blockEntity to avoid issues
        //this.mapOfAccessibleDimensionAndV = new HashMap<>(CSDimensionUtil.travelMap.get(currentDimension).adjacentDimensions());//rocket.getMapOfAccessibleDimensionAndV() == null ? new HashMap<>() : new HashMap<>(rocket.getMapOfAccessibleDimensionAndV());

        this.buttonVector = new Vector<>(CSDimensionUtil.travelMap.size());
        this.destinationChanged = false;
    }

    @Override
    protected void init() {
        setWindowSize(226, 226);
        super.init();
        int x = guiLeft;
        int y = guiTop;

        launchButton = new Button(x + 141, y + 198, 16 * 4, 20,
                Component.translatable("creatingspace.gui.rocket_controls.launch"),
                ($) -> {
                    if (destination == null) {
                        return;
                    }
                    PacketInit.getChannel()
                            .sendToServer(new RocketContraptionLaunchPacket(rocketContraption.getId(), destination));
                    //rocketContraption.destination = destination;
                    onClose();
                });

        addRenderableWidget(launchButton);
        destinationCost = new LabeledBoxWidget(x + 172, y + 20, Component.literal("  500 "));

        validateSetting = new IconButton(x + 192, y + 103, AllIcons.I_CONFIG_SAVE);
        validateSetting.setToolTip(
                Component.translatable("creatingspace.gui.rocket_controls.send_setting"));
        validateSetting.withCallback(() -> {
            System.out.println(String.valueOf(destination));
            BlockPos pos = initialPosMap.get(String.valueOf(destination));
            if (pos == null) {
                pos = this.rocketContraption.getOnPos();
            }
            String X = Xinput.getValue().replace(" ", ""),/*Y = Yinput.getValue().replace(" ",""),*/Z = Zinput.getValue().replace(" ", "");
            if (CSUtil.isInteger(X)) {
                pos = new BlockPos(Integer.parseInt(X), pos.getY(), pos.getZ());
            } else {
                Xinput.setValue(String.valueOf(pos.getX()));
            }
                    /*if (isInteger(Y)){
                        pos = pos.mutable().setY(Integer.parseInt(Y)).immutable();
                    }else {
                        Yinput.setValue(String.valueOf(pos.getY()));
                    }*/
            if (CSUtil.isInteger(Z)) {
                pos = pos.mutable().setZ(Integer.parseInt(Z)).immutable();
            } else {
                Zinput.setValue(String.valueOf(pos.getZ()));
            }

            initialPosMap.put(String.valueOf(destination), pos);
            PacketInit.getChannel().sendToServer(RocketControlsSettingsPacket.sendSettings(this.rocketContraption.getOnPos(), initialPosMap));
        });

        Xinput = new EditBox(font, x + 169, y + 63,
                50, 14, Component.literal(""));
        /*Yinput = new EditBox(font,x + 169, y + 63,
                50, 14, Component.literal(""));*/
        Zinput = new EditBox(font, x + 169, y + 83,
                50, 14, Component.literal(""));

        addRenderableWidget(Xinput);
        //addRenderableWidget(Yinput);
        addRenderableWidget(Zinput);
        addRenderableWidget(validateSetting);
        addRenderableWidget(destinationCost);

        Component text;
        //the sun
        Orbit sun = new Orbit(x + windowWidth / 2, y + windowHeight / 2, 0, new ResourceLocation("sun"));
        Map<ResourceLocation, Orbit> temp = new HashMap<>();
        temp.put(RocketAccessibleDimension.BASE_BODY, sun);
        //first collect all the planets
        int row = 0;
        float zoom = 20f;
        //TODO add detection of loops
        ArrayDeque<ResourceLocation> toVisit = new ArrayDeque<>(CSDimensionUtil.travelMap.keySet());
        while (!toVisit.isEmpty()) {
            ResourceLocation dim = toVisit.poll();
            if (temp.containsKey(CSDimensionUtil.travelMap.get(dim).orbitedBody())) {
                Orbit widget = new Orbit(x + windowWidth / 2, y + windowHeight / 2, (int) (CSDimensionUtil.travelMap.get(dim).distanceToOrbitedBody() / zoom), dim);
                temp.put(dim, widget);
                widget.withCallback(
                        () -> {
                            destination = widget.getDim();
                            destinationChanged = true;
                        }
                );
                buttonVector.add(
                        row,
                        widget);
                row++;
                addRenderableWidget(widget);
                temp.get(CSDimensionUtil.travelMap.get(dim).orbitedBody()).addSatellite(widget);
            } else {
                toVisit.addLast(dim);
            }
        }
    }


    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(ms, x, y, this);


        if (destination != null) {
            if (destinationChanged) {
                BlockPos pos = initialPosMap.get(String.valueOf(destination));
                if (pos == null) {
                    pos = this.rocketContraption.getOnPos();
                }
                Xinput.setValue(String.valueOf(pos.getX()));
                //Yinput.setValue(String.valueOf(pos.getY()));
                Zinput.setValue(String.valueOf(pos.getZ()));
            }
            Xinput.visible = true;
            Xinput.active = true;
            font.drawShadow(ms, Component.translatable("creatingspace.gui.rocket_controls.pos_selection"), x + 166, y + 43, 0xFFFFFF);
            font.drawShadow(ms, Component.literal("X :"), x + 156, y + 63, 0xFFFFFF);
            font.drawShadow(ms, Component.literal("Z :"), x + 156, y + 83, 0xFFFFFF);

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
                            String.valueOf(CSDimensionUtil.cost(currentDimension, destination))),
                    true, 112);

        } else {
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

        for (int row = 0; row < CSDimensionUtil.travelMap.size(); row++) {
            Orbit widget = buttonVector.get(row);
            if (destination == widget.getDim()) {
                widget.withBorderColors(green);
            } else if (destination != null) {
                widget.withBorderColors(red);
            }
        }

        destinationChanged = false;
    }

    private void renderBackground(PoseStack ms, int mouseX, int mouseY, float partialTicks) {

    }

    private void renderPlanets(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
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
