package com.rae.creatingspace.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.api.gui.Orbit;
import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import com.rae.creatingspace.client.gui.screen.elements.LabeledBoxWidget;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.rae.creatingspace.utilities.CSDimensionUtil;
import com.rae.creatingspace.utilities.CSUtil;
import com.rae.creatingspace.utilities.packet.RocketContraptionLaunchPacket;
import com.rae.creatingspace.utilities.packet.RocketControlsSettingsPacket;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class NewDestinationScreen extends AbstractSimiScreen {
    private boolean destinationChanged;
    private Button launchButton;
    //private final HashMap<ResourceLocation, RocketAccessibleDimension.AccessibilityParameter> mapOfAccessibleDimensionAndV;
    HashMap<String, BlockPos> initialPosMap;
    private final RocketContraptionEntity rocketContraption;
    //private final GuiTexturesInit background;
    private final ResourceLocation currentDimension;
    private ResourceLocation destination;
    private Orbit focusedPlanet = null;
    private final Vector<Orbit> buttonVector;
    private LabeledBoxWidget destinationCost;
    // replace by a render schedule thing from ScheduleScreen
    private EditBox Xinput;
    private EditBox Zinput;
    Couple<Color> red = Theme.p(Theme.Key.BUTTON_FAIL);
    Couple<Color> green = Theme.p(Theme.Key.BUTTON_SUCCESS);
    Couple<Color> idle = Theme.p(Theme.Key.BUTTON_IDLE);
    private IconButton validateSetting;
    float zoom = 20f;
    int xShift = 0;
    int yShift = 0;
    private Orbit sun;
    private Label posLabel;
    private Label posX;
    private Label posY;

    public NewDestinationScreen(RocketContraptionEntity rocket) {
        super(Lang.translateDirect("gui.destination_screen.title"));
        this.rocketContraption = rocket;
        this.initialPosMap = new HashMap<>(rocket.getInitialPosMap());
        //this.background = GuiTexturesInit.ROCKET_CONTROLS;
        this.currentDimension = rocket.getLevel().dimension().location();
        //initialise the map in the server side blockEntity to avoid issues
        //this.mapOfAccessibleDimensionAndV = new HashMap<>(CSDimensionUtil.travelMap.get(currentDimension).adjacentDimensions());//rocket.getMapOfAccessibleDimensionAndV() == null ? new HashMap<>() : new HashMap<>(rocket.getMapOfAccessibleDimensionAndV());

        this.buttonVector = new Vector<>(CSDimensionUtil.travelMap.size() + 1);
        this.destinationChanged = false;
    }

    //todo : zoom on double clic
    @Override
    protected void init() {
        setWindowSize(width, height);
        super.init();
        int x = guiLeft;
        int y = guiTop;

        //add the orbits
        sun = new Orbit(x + windowWidth / 2, y + windowHeight / 2, 0, new ResourceLocation("sun"));
        sun.setBodyRadius(20);
        Map<ResourceLocation, Orbit> temp = new HashMap<>();
        temp.put(RocketAccessibleDimension.BASE_BODY, sun);
        addRenderableOnly(sun);
        buttonVector.add(0, sun);
        focusedPlanet = sun;
        //first collect all the planets
        int row = 1;
        //TODO add detection of loops
        ArrayDeque<ResourceLocation> toVisit = new ArrayDeque<>(CSDimensionUtil.travelMap.keySet());
        while (!toVisit.isEmpty()) {
            ResourceLocation dim = toVisit.poll();
            if (temp.containsKey(CSDimensionUtil.travelMap.get(dim).orbitedBody())) {
                Orbit widget = new Orbit(x + windowWidth / 2, y + windowHeight / 2, CSDimensionUtil.travelMap.get(dim).distanceToOrbitedBody(), dim);
                temp.put(dim, widget);
                widget.withCallback(
                        () -> {
                            destination = widget.getDim();
                            destinationChanged = true;
                            focusedPlanet = widget;
                        }
                );
                widget.setWindow(height, width);
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
        restZoom();
        //everything else
        launchButton = new Button(x + 341, y + 198, 16 * 4, 20,
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
        destinationCost = new LabeledBoxWidget(x + 372, y + 20, Component.literal("  500 "));

        validateSetting = new IconButton(x + 392, y + 103, AllIcons.I_CONFIG_SAVE);
        validateSetting.setToolTip(
                Component.translatable("creatingspace.gui.rocket_controls.send_setting"));
        validateSetting.withCallback(() -> {
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

        Xinput = new EditBox(font, x + 369, y + 63,
                50, 14, Component.literal(""));
        /*Yinput = new EditBox(font,x + 169, y + 63,
                50, 14, Component.literal(""));*/
        Zinput = new EditBox(font, x + 369, y + 83,
                50, 14, Component.literal(""));

        addRenderableWidget(Xinput);
        //addRenderableWidget(Yinput);
        addRenderableWidget(Zinput);
        addRenderableWidget(validateSetting);
        addRenderableWidget(destinationCost);
        posLabel = new Label(x + 366, y + 43, Component.empty());
        posX = new Label(x + 356, y + 63, Component.empty());
        posY = new Label(x + 356, y + 83, Component.empty());
        addRenderableOnly(posLabel);
        addRenderableOnly(posX);
        addRenderableOnly(posY);
    }

    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    protected void renderWindowForeground(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        if (focusedPlanet != null) {
            setXShift((int) (x + windowWidth / 2 - focusedPlanet.getPlanetX()));
            setYShift((int) (y + windowHeight / 2 - focusedPlanet.getPlanetY()));
            if (destinationChanged) {
                if (focusedPlanet.getMaxSatelliteDistance() > 0) {
                    zoom = (float) focusedPlanet.getMaxSatelliteDistance() / 80;
                    changeZoom(0);
                }
            }
        }
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
            //TODO use labels
            posLabel.text = Component.translatable("creatingspace.gui.rocket_controls.pos_selection");
            posX.text = Component.literal("X :");
            posY.text = Component.literal("Z :");

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
            posLabel.text = Component.empty();
            posX.text = Component.empty();
            posY.text = Component.empty();
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

        for (int row = 0; row < buttonVector.size(); row++) {
            Orbit widget = buttonVector.get(row);
            if (destination == widget.getDim()) {
                widget.withBorderColors(green);
            } else if (destination != null) {
                widget.withBorderColors(red);
            } else {
                widget.withBorderColors(idle);
            }
        }

        destinationChanged = false;
    }

    @Override
    protected void renderWindowBackground(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        super.renderWindowBackground(ms, mouseX, mouseY, partialTicks);
        fill(ms, 0, 0, width, height, 0x44000000);
        //background.render(ms, 0, 0, this);
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

    //xShift, yShift independent of player input ?
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean flag = super.keyPressed(keyCode, scanCode, modifiers);
        if (GLFW.GLFW_KEY_ESCAPE == keyCode) {
            restZoom();
        }
        if (GLFW.GLFW_KEY_LEFT_CONTROL == keyCode) {
            changeZoom(0.01f);
        }
        if (GLFW.GLFW_KEY_LEFT_SHIFT == keyCode) {
            changeZoom(-0.01f);
        }
        return flag;
    }

    //should reset everything
    private void restZoom() {
        zoom = 20f;
        for (Orbit orbit :
                buttonVector) {
            orbit.setZoom(zoom);
        }
        setFocused(null);
        destination = null;
        focusedPlanet = sun;
    }
    private void changeZoom(float amount) {
        zoom += amount * zoom;
        if (zoom <= 0.01f) {
            zoom = 0.01f;
        }
        for (Orbit orbit :
                buttonVector) {
            orbit.setZoom(zoom);
        }
    }

    private void shiftX(int amount) {
        xShift += amount;
        for (Orbit orbit :
                buttonVector) {
            orbit.shiftX(amount);
        }
    }

    private void setXShift(int xShift) {
        this.xShift = xShift;
        for (Orbit orbit :
                buttonVector) {
            orbit.setxShift(xShift);
        }
    }

    private void shiftY(int amount) {
        yShift += amount;
        for (Orbit orbit :
                buttonVector) {
            orbit.shiftY(amount);
        }
    }

    private void setYShift(int yShift) {
        this.yShift = yShift;
        for (Orbit orbit :
                buttonVector) {
            orbit.setyShift(yShift);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return focusedPlanet == sun;
    }

    private void fillToolTip(IconButton button, String tooltipKey) {
        if (!button.isHoveredOrFocused())
            return;
        List<Component> tip = button.getToolTip();

        tip.addAll(TooltipHelper
                .cutTextComponent(Component.translatable("creatingspace.gui.rocket_controls." + tooltipKey + ".description"), TooltipHelper.Palette.ALL_GRAY));
    }
}
