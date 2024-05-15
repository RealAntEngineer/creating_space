package com.rae.creatingspace.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.client.gui.menu.EngineerTableMenu;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.server.design.ExhaustPackType;
import com.rae.creatingspace.server.design.PowerPackType;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.rae.creatingspace.init.MiscInit.DEFERRED_EXHAUST_PACK_TYPE;
import static com.rae.creatingspace.init.MiscInit.DEFERRED_POWER_PACK_TYPE;
import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public class EngineerTableScreen extends AbstractSimiContainerScreen<EngineerTableMenu> {
    //go to SelectionSrollInput (SchematicTableScreen)
    private final Component availableExhaustTypeTitle = Lang.translateDirect("gui.schematicTable.availableSchematics");
    private final Component availablePowerTypeTitle = Lang.translateDirect("gui.schematicTable.availableSchematics");
    private List<PowerPackType> powerPackTypes;
    private List<ExhaustPackType> exhaustPackTypes;
    private ScrollInput setPowerPackType;
    private Label powerPackLabel;
    private ScrollInput setExhaustPackType;
    private Label exhaustPackLabel;
    private final GuiTexturesInit background;

    public EngineerTableScreen(EngineerTableMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        background = GuiTexturesInit.ROCKET_ENGINEER_TABLE;
    }

    @Override
    protected void init() {
        setWindowSize(background.width, (background.height + 4 + AllGuiTextures.PLAYER_INVENTORY.height));
        //setWindowOffset(11, 8);
        super.init();

        int x = leftPos;
        int y = topPos;

        exhaustPackLabel = new Label(x + 292, y + 20, Components.immutableEmpty()).withShadow();
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
        setExhaustPackType = new SelectionScrollInput(x + 292, y + 20, 120, 18).forOptions(availableExhaustType)
                .titled(availableExhaustTypeTitle.plainCopy())
                .writingTo(exhaustPackLabel)
                .calling((state) -> {
                });
        addRenderableWidget(setExhaustPackType);
        addRenderableWidget(exhaustPackLabel);

        powerPackLabel = new Label(x + 140, y + 20, Components.immutableEmpty()).withShadow();
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

        setPowerPackType = new SelectionScrollInput(x + 140, y + 20, 120, 18).forOptions(availablePowerType)
                .titled(availablePowerTypeTitle.plainCopy())
                .writingTo(powerPackLabel);
        addRenderableWidget(setPowerPackType);
        addRenderableWidget(powerPackLabel);
    }

    @Override
    protected void renderBg(@NotNull PoseStack ms, float partialTicks, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(PLAYER_INVENTORY.width);
        int invY = topPos + background.height + 4;
        renderPlayerInventory(ms, invX, invY);

        int x = leftPos;
        int y = topPos;

        background.renderNotStandardSheetSize(ms, x, y, Color.WHITE);
    }
}
