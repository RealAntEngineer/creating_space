package com.rae.creatingspace.content.rocket.engine.design.newDesign.testAbstraction.material;


public class UniformMaterial implements MaterialField {

    private final Material material;

    public UniformMaterial(Material material) {
        this.material = material;
    }

    @Override
    public Material get(double x, double y) {
        return material;
    }
}
