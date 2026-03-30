package com.rae.creatingspace.content.rocket.engine.design.newDesign.testAbstraction.material;

public class LayeredMaterial implements MaterialField {

    private final double splitY;
    private final Material top;
    private final Material bottom;

    public LayeredMaterial(double splitY, Material top, Material bottom) {
        this.splitY = splitY;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public Material get(double x, double y) {
        return y > splitY ? top : bottom;
    }
}
