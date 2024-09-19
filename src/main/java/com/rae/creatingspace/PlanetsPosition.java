package com.rae.creatingspace;

import com.mojang.math.Quaternion;
import net.minecraft.resources.ResourceLocation;

public class PlanetsPosition {


    public static SkyPos getRelativeSkyPos(ResourceLocation planet, float time){
        return SkyPos.ZENITH;
    }


    public static class SkyPos {
        Quaternion rot;
        float r;
        public static SkyPos ZENITH = new SkyPos(100, Quaternion.ONE);


        public SkyPos(float distance, Quaternion rot) {

        }
    }
}
