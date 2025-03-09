package com.rae.creatingspace.content.rocket.engine.design;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.lang.Math.abs;

public class PropellantType {
    static final float R = 8.31446261815324f;
    Map<TagKey<Fluid>, Float> propellantRatio;
    Integer maxISP;
    Float Cp;
    Float gamma;
    Float Rs;
    Integer M;

    //for codec use MiscInit.PROPELLANT_TYPE.get().getCodec()
    public static final Codec<Map<TagKey<Fluid>, Float>> MAP_CODEC = Codec.unboundedMap(TagKey.codec(ForgeRegistries.FLUIDS.getRegistryKey()), Codec.FLOAT);
    public static final Codec<PropellantType> DIRECT_CODEC = RecordCodecBuilder.create(
            instance ->
                    instance.group(
                            MAP_CODEC.fieldOf("propellantRatio").forGetter(i -> i.propellantRatio),
                            Codec.INT.fieldOf("maxIsp").forGetter(i -> i.maxISP),
                            Codec.FLOAT.fieldOf("Cp").forGetter(i -> i.Cp),
                            Codec.FLOAT.fieldOf("gamma").forGetter(i -> i.gamma),
                            Codec.INT.fieldOf("M").forGetter(i -> i.M)
                    ).apply(instance, PropellantType::new));

    public PropellantType(Map<TagKey<Fluid>, Float> propellantRatio, Integer maxISP, Float Cp, Float gamma, int M) {
        this.propellantRatio = normalise(propellantRatio);
        this.maxISP = maxISP;
        this.Rs = R / (M / 1000f);
        this.M = M;
        this.gamma = gamma;
        this.Cp = Cp;
    }

    private static Map<TagKey<Fluid>, Float> normalise(Map<TagKey<Fluid>, Float> propellantConsumptions) {
        Float total = 0f;
        HashMap<TagKey<Fluid>, Float> newMap = new HashMap<>(propellantConsumptions);
        for (float value :
                propellantConsumptions.values()) {
            total += value;
        }
        for (TagKey<Fluid> key : propellantConsumptions.keySet()) {
            newMap.put(key, propellantConsumptions.get(key) / total);
        }
        return newMap;
    }

    @Override
    public String toString() {
        return "PropellantType{" +
                "propellantRatio=" + propellantRatio +
                ", maxISP=" + maxISP +
                '}';
    }

    public Map<TagKey<Fluid>, Float> getPropellantRatio() {
        return propellantRatio;
    }

    public Float getCombustionTemperature(float combustionEfficiency) {
        return 300 + (float) (0.5 * Math.pow((maxISP * 9.81), 2) * combustionEfficiency / Cp);
    }

    private float machErFunc(float expansionRatio, float mach) {
        return (float) (Math.pow((gamma + 1) / 2, -(gamma + 1) / (2 * (gamma - 1))) * Math.pow(1 + (gamma - 1) / 2 * Math.pow(mach, 2)
                , (gamma + 1) / (2 * (gamma - 1))) / mach - expansionRatio);
    }

    float getAreaOfThroat(float size) {
        return size / 6;  // L* of 2m and chamber area 3 time the throat
    }

    private float getExhaustTemperature(float mach, float combustionEfficiency) {
        return (float) (Math.pow((1 + (gamma - 1) / 2 * Math.pow(mach, 2)), (-1)) * getCombustionTemperature(combustionEfficiency));
    }

    private float dichotomy(Function<Float, Float> function, float a, float b, float epsilon) {
        try {
            if (function.apply(a) * function.apply(b) > 0) {  //On vérifie l 'encadrement de la fonction
                throw new RuntimeException("Mauvais choix de a ou b.");
            } else {
                float m = (float) ((a + b) / 2.);
                while (abs(a - b) > epsilon) {
                    if (function.apply(m) == 0.0) {
                        return m;
                    } else if (function.apply(a) * function.apply(m) > 0) {
                        a = m;
                    } else {
                        b = m;
                    }
                    m = (a + b) / 2;
                }
                return m;
            }
        } catch (RuntimeException e) {
            System.out.println(e);
            return 0;
        }
    }

    public Float getExitMach(float expansionRatio) {
        return dichotomy((mach) -> machErFunc(expansionRatio, mach), 1F, 1000F, 0.002F);
    }

    public Integer getMaxISP() {
        return maxISP;
    }

    private float getExhaustVelocity(float mach, float combustionEfficiency) {
        return (float) (mach * Math.pow((gamma * Rs * getExhaustTemperature(mach, combustionEfficiency)), 0.5));
    }

    public float getRealIsp(float combustionEfficiency, float expansionRatio) {
        return (float) (getExhaustVelocity(getExitMach(expansionRatio), combustionEfficiency) / 9.81);
    }

    public float getChamberPressure(float thrust, float size, float combustionEfficiency, float expansionRatio) {
        float q = thrust / getRealIsp(combustionEfficiency, expansionRatio);
        return (float) (q * (
                Math.pow(getCombustionTemperature(combustionEfficiency) * Rs / gamma, 0.5) /
                        getAreaOfThroat(size)
                        * Math.pow(((gamma + 1) / 2), ((gamma + 1)) / (2 * (gamma - 1)))));
    }
}