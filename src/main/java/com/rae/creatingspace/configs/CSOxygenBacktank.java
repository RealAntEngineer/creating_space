package com.rae.creatingspace.configs;

public class CSOxygenBacktank extends CSConfigBase {
    public final ConfigInt xOffset = i(0, Integer.MIN_VALUE, "xOffset", Comments.xOffset);
    public final ConfigInt yOffset = i(0, Integer.MIN_VALUE, "yOffset", Comments.yOffset);
    public final ConfigEnum<PlaceSelection> sliderPlace = e(PlaceSelection.BOTTOM_LEFT, "place", Comments.place);
    public final ConfigEnum<ColorSelection> sliderColor = e(ColorSelection.WHITE, "color", Comments.color);
    @Override
    public String getName() {
        return "oxygenBacktank";
    }

    private static class Comments {
        static String xOffset = "the horizontal offset compared to default location";
        static String yOffset = "the vertical offset compared to default location";
        static String color = "the color of the oxygen gauge";
        static String place = "the corner from where the offset is calculated";
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

    public enum PlaceSelection {
        BOTTOM_RIGHT(false, false),
        BOTTOM_LEFT(false, true),
        TOP_RIGHT(true, false),
        TOP_LEFT(true, true);

        final boolean top;
        final boolean left;

        PlaceSelection(boolean top, boolean left) {
            this.top = top;
            this.left = left;
        }

        //valid for bottom left
        public int getX(int width) {
            return (left ? 0 : width) + (left ? 0 : -32) + (left ? 1 : -1) * (32 + CSConfigs.CLIENT.oxygenBacktankN.xOffset.get());
        }

        public int getY(int height) {
            return (top ? 0 : height) + (top ? 0 : -64) + (top ? 1 : -1) * (20 + CSConfigs.CLIENT.oxygenBacktankN.yOffset.get());
        }
    }
}
