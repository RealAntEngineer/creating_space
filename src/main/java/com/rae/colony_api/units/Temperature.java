package com.rae.colony_api.units;

public enum Temperature {
    KELVIN(1,0, "K"),
    CELSIUS(1,-273.15f,"°C"),
    FAHRENHEIT(9f/5 , -273.15f * 9f/5 +32,"°F");

    private final float a;
    private final float b;
    private final String symbol;

    Temperature(float a, float b, String symbol) {
        this.a = a;
        this.b = b;
        this.symbol = symbol;
    }

    public float convert(float kelvin) {
        return kelvin * a + b;
    }

    public String getSymbol() {
        return symbol;
    }
}
