package com.rae.creatingspace.content.rocket.engine.design.newDesign;

import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class NewDesignerScreen extends AbstractSimiScreen {
    RocketEngineDesign design = new RocketEngineDesign(PropellantTypeInit.METHALOX.get());


    AerospikeWidget aerospikeWidget;

    @Override
    protected void init() {
        setWindowSize(256, (256 + 4 + AllGuiTextures.PLAYER_INVENTORY.getHeight()));
        setWindowOffset(0, 0);

        super.init();

        int x = guiLeft;//is this the center of the screen ?
        int y = guiTop;

        aerospikeWidget = new AerospikeWidget(
                x, y+ 100, design
        );

        addRenderableWidgets(aerospikeWidget);


    }

    @Override
    protected void renderWindow(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

    }
}
