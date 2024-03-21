package com.rae.creatingspace.configs;

public class CSOxygenBacktank extends CSConfigBase {
    public final ConfigInt xOffset = i(0, Integer.MIN_VALUE, "xOffset", Comments.xOffset);
    public final ConfigInt yOffset = i(0, Integer.MIN_VALUE, "yOffset", Comments.yOffset);
    public final ConfigEnum<ColorSelection> sliderColor = e(ColorSelection.WHITE, "color", Comments.color);

    @Override
    public String getName() {
        return "oxygenBacktank";
    }

    private static class Comments {
        static String xOffset = "the horizontal offset compared to default location";
        static String yOffset = "the vertical offset compared to default location";
        static String color = "the color of the oxygen gauge";
    }

    public enum ColorSelection {
        WHITE(0xFFFFFF), BLACK(0x131313), RED(0xc42430), ORANGE(0xFF9933), GREEN(0x10bc0a), BLUE(0x2d0abc);
        final int color;

        ColorSelection(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }
}
