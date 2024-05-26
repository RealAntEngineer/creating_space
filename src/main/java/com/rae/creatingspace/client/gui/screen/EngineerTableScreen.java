package com.rae.creatingspace.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.client.gui.menu.EngineerTableMenu;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.server.design.ExhaustPackType;
import com.rae.creatingspace.server.design.PowerPackType;
import com.rae.creatingspace.server.design.PropellantType;
import com.rae.creatingspace.server.items.engine.SuperEngineItem;
import com.rae.creatingspace.utilities.CSUtil;
import com.rae.creatingspace.utilities.packet.EngineerTableCraft;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.rae.creatingspace.init.MiscInit.DEFERRED_EXHAUST_PACK_TYPE;
import static com.rae.creatingspace.init.MiscInit.DEFERRED_POWER_PACK_TYPE;
import static com.rae.creatingspace.init.ingameobject.PropellantTypeInit.DEFERRED_PROPELLANT_TYPE;
import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public class EngineerTableScreen extends AbstractSimiContainerScreen<EngineerTableMenu> {
    //go to SelectionSrollInput (SchematicTableScreen)
    private final Component availableExhaustTypeTitle =
            Component.translatable("gui.engineer_table_screen.available_exhaust");
    private final Component availablePowerTypeTitle =
            Component.translatable("gui.engineer_table_screen.available_power");
    private final Component availablePropellantTypeTitle =
            Component.translatable("gui.engineer_table_screen.available_propellants");
    private final Component thrustTitle =
            Component.translatable("gui.engineer_table_screen.thrust_selection");
    private final Component sizeTitle =
            Component.translatable("gui.engineer_table_screen.size_selection");
    private List<PowerPackType> powerPackTypes;
    private List<ExhaustPackType> exhaustPackTypes;
    private List<PropellantType> propellantTypes;
    private ScrollInput setPowerPackType;
    private Label powerPackLabel;
    private ScrollInput setExhaustPackType;
    private Label exhaustPackLabel;
    private ScrollInput setPropellantType;
    private Label propellantLabel;
    private ScrollInput engineSizeInput;
    private Label engineSizeLabel;

    private ScrollInput engineThrustInput;
    private Label engineThrustLabel;
    private ForgeSlider expansionRatioSlider;
    private final GuiTexturesInit background;
    private final GuiTexturesInit input;
    private IconButton confirmButton;
    float engineIsp;
    float engineMass;

    public EngineerTableScreen(EngineerTableMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        background = GuiTexturesInit.ROCKET_ENGINEER_TABLE;
        input = GuiTexturesInit.ROCKET_ENGINEER_TABLE_INPUT;
        engineIsp = 0;
        engineMass = 0;
    }

    @Override
    protected void init() {

        setWindowSize(background.width, (background.height + 4 + AllGuiTextures.PLAYER_INVENTORY.height));
        setWindowOffset(0, -8);

        super.init();

        int x = leftPos;
        int y = topPos;

        exhaustPackLabel = new Label(x + 133, y + 20, Components.immutableEmpty()).withShadow();
        exhaustPackLabel.text = Components.immutableEmpty();
        exhaustPackTypes = new ArrayList<>();
        List<MutableComponent> availableExhaustType = new ArrayList<>();
        DEFERRED_EXHAUST_PACK_TYPE.getEntries().forEach((ro) -> {
                    availableExhaustType.add(Component.translatable(
                            "exhaust_pack_type." +
                                    ro.getId().getNamespace() + "." + ro.getId().getPath()));
                    exhaustPackTypes.add(ro.get());
                }
        );
        setExhaustPackType = new SelectionScrollInput(x + 133, y + 20, 100, 18)
                .forOptions(availableExhaustType)
                .titled(availableExhaustTypeTitle.plainCopy())
                .writingTo(exhaustPackLabel)
                .calling((state) -> {
                    ExhaustPackType type = exhaustPackTypes.get(state);
                    removeWidgets(expansionRatioSlider);
                    expansionRatioSlider = new ForgeSlider(x + 10, y + 220, 110, 20,
                            Component.literal("expansion ratio : "),
                            Component.empty(), type.getMinExpansionRatio(), type.getMaxExpansionRatio(), 50, true);
                    addRenderableWidget(expansionRatioSlider);
                });
        addRenderableWidget(setExhaustPackType);
        addRenderableWidget(exhaustPackLabel);

        powerPackLabel = new Label(x + 7, y + 20, Components.immutableEmpty()).withShadow();
        powerPackLabel.text = Components.immutableEmpty();
        powerPackTypes = new ArrayList<>();

        List<MutableComponent> availablePowerType = new ArrayList<>();
        DEFERRED_POWER_PACK_TYPE.getEntries().forEach((ro) -> {
                    availablePowerType.add(Component.translatable(
                            "power_pack_type." +
                                    ro.getId().getNamespace() + "." + ro.getId().getPath()));
                    powerPackTypes.add(ro.get());
                }
        );

        setPowerPackType = new SelectionScrollInput(x + 7, y + 20, 100, 18)
                .forOptions(availablePowerType)
                .titled(availablePowerTypeTitle.plainCopy())
                .writingTo(powerPackLabel);
        addRenderableWidget(setPowerPackType);
        addRenderableWidget(powerPackLabel);

        propellantLabel = new Label(x + 7, y + 135, Components.immutableEmpty()).withShadow();
        propellantLabel.text = Components.immutableEmpty();
        propellantTypes = new ArrayList<>();

        List<MutableComponent> availablePropellantType = new ArrayList<>();
        DEFERRED_PROPELLANT_TYPE.getEntries().forEach((ro) -> {
                    availablePropellantType.add(Component.translatable(
                            "propellant_type." +
                                    ro.getId().getNamespace() + "." + ro.getId().getPath()).append(
                            Component.literal("  max isp : " + ro.get().getMaxISP())));
                    propellantTypes.add(ro.get());
                }
        );
        setPropellantType = new SelectionScrollInput(x + 7, y + 135, 100, 18)
                .forOptions(availablePropellantType)
                .titled(availablePropellantTypeTitle.plainCopy())
                .writingTo(propellantLabel)
                .addHint(Component.literal("using a better propellant will mean using better materials"));
        addRenderableWidget(setPropellantType);
        addRenderableWidget(propellantLabel);

        expansionRatioSlider = new ForgeSlider(x + 10, y + 220, 110, 20,
                Component.literal("expansion ratio : "),
                Component.empty(), 2, 100, 50, true);
        addRenderableWidget(expansionRatioSlider);

        engineSizeLabel = new Label(x + 7, y + 156, Components.immutableEmpty()).withShadow();
        engineSizeLabel.text = Components.immutableEmpty();
        engineSizeInput = new ScrollInput(x + 7, y + 156,
                50, 18)
                .withRange(1, Integer.MAX_VALUE)
                .titled(sizeTitle.plainCopy())
                .writingTo(engineSizeLabel)
                .addHint(Component.literal("heavier but decrease material requirement"))
                .setState(100);
        engineThrustLabel = new Label(x + 7, y + 178, Components.immutableEmpty()).withShadow();
        engineThrustLabel.text = Components.immutableEmpty();
        engineThrustInput = new ScrollInput(x + 7, y + 178,
                50, 18)
                .withRange(1, Integer.MAX_VALUE)
                .titled(thrustTitle.plainCopy())
                .writingTo(engineThrustLabel)
                .addHint(Component.literal("more thrust but increase material requirement"))
                .withShiftStep(10000)
                .setState(100000);
        addRenderableWidget(engineSizeLabel);
        addRenderableWidget(engineThrustLabel);
        addRenderableWidget(engineSizeInput);
        addRenderableWidget(engineThrustInput);
        confirmButton = new IconButton(x + 258, y + 100, AllIcons.I_CONFIRM)
                .withCallback(() ->
                        craftEngine(getMenu().contentHolder.getBlockPos(), propellantTypes.get(setPropellantType.getState()), engineIsp, engineMass, engineThrustInput.getState()));
        addRenderableWidget(confirmButton);
    }

    @Override
    protected void renderBg(@NotNull PoseStack ms, float partialTicks, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(PLAYER_INVENTORY.width) + 50;
        int invY = topPos + background.height + 4;
        renderPlayerInventory(ms, invX, invY);

        int x = leftPos;
        int y = topPos;

        background.render(ms, x, y, Color.WHITE);
        input.render(ms, x, y + background.height, Color.WHITE);
        PropellantType prop = propellantTypes.get(setPropellantType.getState());
        PowerPackType powerPack = powerPackTypes.get(setPowerPackType.getState());
        ExhaustPackType exhaustPackType = exhaustPackTypes.get(setExhaustPackType.getState());
        //replace by labels
        font.draw(ms, "P : " + CSUtil.scientificNbrFormatting(prop.getChamberPressure(
                        engineThrustInput.getState(),
                        (float) engineSizeInput.getState() / 1000,
                        powerPack.getCombustionEfficiency(), expansionRatioSlider.getValueInt()) / 100000, 3) + "bar",
                x + 260, y + 20 + 6,
                Theme.c(Theme.Key.TEXT).scaleAlpha(.75f).getRGB());
        engineIsp = prop.getRealIsp(
                powerPack.getCombustionEfficiency(), expansionRatioSlider.getValueInt());
        font.draw(ms, "ISP : " + (int) engineIsp + "s",
                x + 260, y + 35 + 6,
                Theme.c(Theme.Key.TEXT).scaleAlpha(.75f).getRGB());
        font.draw(ms, "T : " + prop.getCombustionTemperature(
                        powerPack.getCombustionEfficiency()).intValue() + "°C",
                x + 260, y + 50 + 6,
                Theme.c(Theme.Key.TEXT).scaleAlpha(.75f).getRGB());
        engineMass = exhaustPackType.getMass((float) engineSizeInput.getState() / 1000,
                expansionRatioSlider.getValueInt());
        font.draw(ms, "M : " + CSUtil.scientificNbrFormatting(engineMass / 1000, 3) + "t",
                x + 260, y + 65 + 6,
                Theme.c(Theme.Key.TEXT).scaleAlpha(.75f).getRGB());
    }

    private void craftEngine(BlockPos blockEntityPos, PropellantType propellantType, float isp, float mass, float thrust) {
        //send a packet to the BE
        float efficiency = isp / propellantType.getMaxISP();
        ItemStack newEngine = ((SuperEngineItem) BlockInit.SUPER_ROCKET_ENGINE.get().asItem())
                .getItemStackFromInfo((int) thrust, efficiency, propellantType);
        PacketInit.getChannel()
                .sendToServer(
                        EngineerTableCraft
                                .sendCraft(blockEntityPos, newEngine));
    }
}