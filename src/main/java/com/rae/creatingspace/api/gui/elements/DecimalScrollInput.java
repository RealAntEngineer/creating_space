package com.rae.creatingspace.api.gui.elements;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public class DecimalScrollInput extends BackgroundScrollInput {

    private final double powerOf10;

    public DecimalScrollInput(int xIn, int yIn, int widthIn, int heightIn, int decimals) {
        super(xIn, yIn, widthIn, heightIn);

        this.powerOf10 = Math.pow(10, decimals);

        this.formatter = i -> {
            double value = i / powerOf10;
            return Component.literal(String.format("%." + decimals + "f", value));
        };
    }

    @Override
    public ScrollInput format(Function<Integer, Component> formatter) {
        // Disable external formatting to keep decimal precision control
        return this;
    }

    public ScrollInput withRange(float min, float max) {
        this.min = (int) (min * powerOf10);
        this.max = (int) (max * powerOf10);
        return this;
    }

    public double getDecimalValue() {
        return state / powerOf10;
    }

    public void setDecimalValue(double value) {
        this.state = (int) Math.round(value * powerOf10);
    }
}