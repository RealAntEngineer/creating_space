package com.rae.creatingspace.content.rocket.engine.design.newDesign.testAbstraction.material;

public enum Material {
    ALUMINUM(205),
    COPPER(385),
    STEEL(50),
    WATER(0.6),
    AIR(0.025);

    private final double conductivity;

    Material(double conductivity) {
        this.conductivity = conductivity;
    }

    public double getConductivity() {
        return conductivity;
    }
}
