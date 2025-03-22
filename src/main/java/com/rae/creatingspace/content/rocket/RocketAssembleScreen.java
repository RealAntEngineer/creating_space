package com.rae.creatingspace.content.rocket;

import com.rae.creatingspace.api.gui.elements.TallIconButton;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.content.rocket.rocket_control.RocketControlsBlockEntity;
import com.rae.creatingspace.legacy.utilities.packet.NewRocketAssemblePacket;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class RocketAssembleScreen extends AbstractSimiScreen {
    private IconButton assembleButton;
    private final RocketControlsBlockEntity blockEntity;
    private final GuiTexturesInit background;

    public RocketAssembleScreen(RocketControlsBlockEntity be) {
        super(Lang.translateDirect("gui.destination_screen.title"));
        this.blockEntity = be;
        this.background = GuiTexturesInit.ROCKET_ASSEMBLE;
    }

    @Override
    protected void init() {
        setWindowSize(192, 76);
        super.init();
        int x = guiLeft;
        int y = guiTop;

        assembleButton = new TallIconButton(x + 84, y + 30, GuiTexturesInit.ROCKET_ICON)
                .withCallback(() -> {
                    PacketInit.getChannel()
                            .sendToServer(NewRocketAssemblePacket.tryAssemble(blockEntity.getBlockPos()));
                    onClose();
                });
        assembleButton.setToolTip(Component.translatable("rocket.assemble.new_rocket"));
        addRenderableWidget(assembleButton);
    }


    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);
    }
}
