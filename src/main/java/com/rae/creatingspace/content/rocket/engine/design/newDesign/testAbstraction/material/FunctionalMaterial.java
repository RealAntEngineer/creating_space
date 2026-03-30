package com.rae.creatingspace.content.rocket.engine.design.newDesign.testAbstraction.material;

import java.util.function.BiFunction;

public class FunctionalMaterial implements MaterialField {

    private final BiFunction<Double, Double, Material> function;

    public FunctionalMaterial(BiFunction<Double, Double, Material> function) {
        this.function = function;
    }

    @Override
    public Material get(double x, double y) {
        return function.apply(x, y);
    }
}