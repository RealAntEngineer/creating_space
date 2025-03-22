package com.rae.creatingspace.api.gui.elements;

import com.rae.creatingspace.init.graphics.GuiTexturesInit;
import com.simibubi.create.foundation.gui.widget.AbstractSimiWidget;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class SliderWidget extends AbstractSimiWidget {
    private final int color;
    //TODO clean up the prevValue, value thing as there already is a lerpedFloat
    // warning there is remaining value everywhere

    //TODO java doc as this should be in the api
    protected Font font;
    public int min;
    public int max;
    public int value;

    public LerpedFloat lerpedValue;
    public int prevValue;
    public SliderWidget(int x, int y, int width, int height) {
        this(x, y, width, height, 0xFFFFFF);
    }

    public SliderWidget(int x, int y, int width, int height, int color) {
        super(x, y, width, height);
        this.color = color;
        font = Minecraft.getInstance().font;
        min = 0;
        value = 0;
        prevValue = 0;
        max = 1000;
        lerpedValue = LerpedFloat.linear().startWithValue(value);

    }

    public void setMin(int min) {
        this.min = min;
    }


    @Override
    public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
        return false;
    }

    public void setMax(int max) {
        this.max = max;
    }
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (visible){
            int progress = (int) Mth.lerp(partialTicks,prevValue, value);

            lerpedValue.chase(value,1, LerpedFloat.Chaser.EXP);
            lerpedValue.tickChaser();

            progress = (int) lerpedValue.getValue();

            int intervalNumber = (max-min)/50;
            int intervalPixel = 10;

            int slidePixel = (int) (((float) progress - (progress / intervalNumber) *intervalNumber)/((float)intervalNumber) * intervalPixel);

            graphics.pose().pushPose();
            GuiTexturesInit slider = GuiTexturesInit.O2_GAUGE_SLIDER;
            slider.render(graphics,getX(),getY(), Color.WHITE);
            graphics.pose().popPose();
            //put numbers
            //System.out.println("progress : "+ prevValue + value + progress);

            float scale = 0.5f;
            for (int i = -3; i <= 3; i++) {
                int yAddition = 31 + (slidePixel) - (i)*intervalPixel;
                int nbr = (progress / intervalNumber + i) * intervalNumber;
                if (yAddition<=60 && 0<=yAddition && nbr<= max && nbr>=min) {
                    graphics.pose().pushPose();
                    graphics.pose().scale(scale, scale, scale);
                    graphics.drawString(font,  nbr + " -",
                            (int) ((getX() + 4) / scale),
                            (int) ((getY() + yAddition) / scale),
                            color, false);
                    graphics.pose().popPose();
                }
            }

            graphics.pose().pushPose();
            GuiTexturesInit shadow = GuiTexturesInit.O2_GAUGE_SHADOW;
            shadow.render(graphics,getX(),getY(),Color.WHITE);
            graphics.pose().popPose();

            graphics.pose().pushPose();
            GuiTexturesInit frame = GuiTexturesInit.O2_GAUGE_FRAME;
            frame.render(graphics,getX(),getY(),Color.WHITE);
            graphics.pose().popPose();

        }
    }

    public void setValues(int value, int prevValue) {
        this.prevValue = prevValue;
        this.value = value;
    }
}
