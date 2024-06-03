package com.rae.creatingspace.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.server.blockentities.RocketControlsBlockEntity;
import com.rae.creatingspace.utilities.packet.NewRocketAssemblePacket;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class RocketAssembleScreen extends AbstractSimiScreen {
    private Button assembleButton;
    private final RocketControlsBlockEntity blockEntity;
    private final GuiTexturesInit background;

    public RocketAssembleScreen(RocketControlsBlockEntity be) {
        super(Lang.translateDirect("gui.destination_screen.title"));
        this.blockEntity = be;
        this.background = GuiTexturesInit.ROCKET_CONTROLS;
    }

    @Override
    protected void init() {
        setWindowSize(226, 226);
        super.init();
        int x = guiLeft;
        int y = guiTop;

        assembleButton = new Button(x + 141, y + 198, 16 * 4, 20,
                Component.translatable("creatingspace.gui.rocket_controls.assemble"),
                ($) -> {
                    PacketInit.getChannel()
                            .sendToServer(NewRocketAssemblePacket.tryAssemble(blockEntity.getBlockPos()));
                    onClose();
                });
        addRenderableWidget(assembleButton);
    }


    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(ms, x, y, this);
    }
}
