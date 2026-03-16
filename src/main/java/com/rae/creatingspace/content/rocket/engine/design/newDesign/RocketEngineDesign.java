package com.rae.creatingspace.content.rocket.engine.design.newDesign;

import com.rae.creatingspace.content.rocket.engine.design.PropellantType;

import java.util.function.Function;


public class RocketEngineDesign {

    public final PropellantType propellant;

    public RocketEngineDesign(PropellantType propellant) {
        this.propellant = propellant;
    }

    public float getChamberPressure(
            float thrust,
            float Ac,
            float combustionEfficiency,
            float expansionRatio) {

        float gamma = propellant.gamma;
        float Rs = propellant.Rs;

        float q = thrust / getRealIsp(combustionEfficiency, expansionRatio);

        return (float) (
                q *
                        (Math.sqrt(propellant.getCombustionTemperature(combustionEfficiency) * Rs / gamma)
                                / Ac
                                * Math.pow((gamma + 1) / 2,
                                (gamma + 1) / (2 * (gamma - 1))))
        );
    }

    public float getRealIsp(float combustionEfficiency, float expansionRatio) {

        return getExhaustVelocity(
                getExitMach(expansionRatio),
                combustionEfficiency
        ) / 9.81f;
    }

    private float getExhaustVelocity(float mach, float combustionEfficiency) {

        float Tc = propellant.getCombustionTemperature(combustionEfficiency);

        float T = (float) (
                Tc * PrandtlMeyer.temperatureRatio(mach, propellant.gamma)
        );

        return (float) (
                mach *
                        Math.sqrt(propellant.gamma * propellant.Rs * T)
        );
    }

    public float getExitMach(float expansionRatio) {
        return dichotomy(
                (mach) -> machErFunc(expansionRatio, mach),
                1f, 1000f, 0.002f
        );
    }

    //this will be found in the formic API
    private float dichotomy(Function<Float, Float> function,
                            float a, float b, float epsilon) {

        float m = (a + b) / 2f;

        while (Math.abs(a - b) > epsilon) {

            if (function.apply(m) == 0f)
                return m;

            if (function.apply(a) * function.apply(m) > 0)
                a = m;
            else
                b = m;

            m = (a + b) / 2f;
        }

        return m;
    }

    private float machErFunc(float expansionRatio, float mach) {
        return (float) (PrandtlMeyer.areaRatio(mach, propellant.gamma) - expansionRatio);
    }
}
