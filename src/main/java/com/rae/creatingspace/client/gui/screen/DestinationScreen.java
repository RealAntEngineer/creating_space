package com.rae.creatingspace.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.client.gui.screen.elements.DimSelectBoxWidget;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import com.rae.creatingspace.server.blockentities.RocketControlsBlockEntity;
import com.rae.creatingspace.utilities.packet.RocketAssemblePacket;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.Theme;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Vector;

public class DestinationScreen extends AbstractSimiScreen {

    private Button launchButton;
    private final List<ResourceKey<Level>> listOfAccessibleDimension;
    private final RocketControlsBlockEntity blockEntity;
    private final GuiTexturesInit background;
    private final ResourceKey<Level> currentDimension;
    private ResourceKey<Level> destination;

    private final Vector<DimSelectBoxWidget> buttonVector;
    Couple<Color> red = Theme.p(Theme.Key.BUTTON_FAIL);
    Couple<Color> green = Theme.p(Theme.Key.BUTTON_SUCCESS);


    public DestinationScreen(RocketControlsBlockEntity be) {
        super(Lang.translateDirect("gui.destination_screen.title"));
        this.blockEntity = be;
        this.background = GuiTexturesInit.ROCKET_CONTROLS;
        this.currentDimension = be.getLevel().dimension();
        this.listOfAccessibleDimension = DimensionInit.accessibleFrom(this.currentDimension);
        this.buttonVector = new Vector<>(this.listOfAccessibleDimension.size());
    }

    @Override
    protected void init() {
        setWindowSize(226,226);
        super.init();
        int x = guiLeft;
        int y = guiTop;

        launchButton = new Button(x + 141,y + 198,16*4,20,
                Component.translatable(CreatingSpace.MODID+".launch"),
        ($) -> {
            if(destination ==null){
                return;
            }
            PacketInit.getChannel()
                    .sendToServer(RocketAssemblePacket.tryAssemble(blockEntity.getBlockPos(),destination));
            onClose();
        });

        addRenderableWidget(launchButton);

        Component text;
        for (int row = 0; row < listOfAccessibleDimension.size(); row++) {
            ResourceKey<Level> dim = listOfAccessibleDimension.get(row);
            text = Component.translatable(dim.location().toString());
            DimSelectBoxWidget widget = new DimSelectBoxWidget(x+7,y+20+26*row,150,16,text,dim);
            widget.withCallback(
                    ()-> destination = widget.getDim()

            );
            buttonVector.add(
                    row,
                    widget);
            addRenderableWidget(buttonVector.get(row));
        }

    }



    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(ms, x, y, this);

        launchButton.active = destination != null;

        for (int row = 0; row < listOfAccessibleDimension.size(); row++) {
            DimSelectBoxWidget widget = buttonVector.get(row);
            if (destination == widget.getDim()){
                widget.withBorderColors(green);
            } else if(destination!=null){
                widget.withBorderColors(red);
            }
        }
    }


}
