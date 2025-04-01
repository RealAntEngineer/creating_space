package com.rae.colony_api.units;

public enum Pressure {
    PASCALS(1,0, "Pa"),
    BAR(1f/100000,0,"bar"),
    ATMOSPHERES(1f/101300,0,"atm"),
    PSI(1f/6895, 0, "psi");

    private final float a;
    private final float b;
    final String symbol;

    Pressure(float a, float b, String symbol) {
        this.a = a;
        this.b = b;
        this.symbol = symbol;
    }

    public float convert(float pascal) {
        return pascal * a + b;
    }

    public String getSymbol() {
        return symbol;
    }
}
