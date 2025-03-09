package com.rae.creatingspace.content.life_support.spacesuit;

import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.simibubi.create.foundation.gui.AllGuiTextures.PLAYER_INVENTORY;

public class UpgradableEquipementScreen extends AbstractSimiContainerScreen<UpgradableEquipmentMenu> {
    //go to SelectionSrollInput (SchematicTableScreen)
    private final GuiTexturesInit background;

    public UpgradableEquipementScreen(UpgradableEquipmentMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        background = GuiTexturesInit.UPGRADABLE_EQUIPMENT;
    }

    @Override
    protected void init() {
        setWindowSize(Math.max(background.width, PLAYER_INVENTORY.width), (background.height + 4 + AllGuiTextures.PLAYER_INVENTORY.height));
        super.init();
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int invX = getLeftOfCentered(PLAYER_INVENTORY.width);
        int invY = topPos + background.height + 4;
        renderPlayerInventory(graphics, invX, invY);

        int x = leftPos;
        int y = topPos;

        background.render(graphics, x, y, Color.WHITE);
    }
}