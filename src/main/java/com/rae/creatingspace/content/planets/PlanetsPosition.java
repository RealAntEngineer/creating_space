package com.rae.creatingspace.content.planets;

import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class PlanetsPosition {
    //to put into the rocket accessible dim later

    private static Map<ResourceLocation, OrbitParam> positions = new HashMap<>();


    public static SkyPos getXYZPos(ResourceLocation planet,ResourceLocation toRender, float time){
        RocketAccessibleDimension dimension = CSDimensionUtil.getTravelMap().get(planet);
        if (dimension.orbitedBody().equals(RocketAccessibleDimension.BASE_BODY)){

            return SkyPos.HORIZON;//new SkyPos(dimension.distanceToOrbitedBody(), );
        }
        return SkyPos.HORIZON;
    }

    public record OrbitParam(float period, float lengthOfDay, float distanceToOrbitedBody){

    }

    public static class SkyPos {
        float radius;
        float theta;
        float phi;
        public static SkyPos HORIZON = new SkyPos(100, 0,0);
        public SkyPos(float distance, float theta, float phi) {
            this.radius = distance;
            this.theta = theta;
            this.phi = phi;
        }
        public static SkyPos fromXYZ(Vec3 satellite,Vec3 center){
            Vec3 diff = satellite.subtract(center);
            double distance = diff.length();
            double theta = Math.acos(diff.z/distance);
            double phi = Math.signum(diff.z)*Math.acos(diff.x/diff.horizontalDistance());
            return new SkyPos((float) distance, (float) theta, (float) phi);
        }
        public static Vec3 toXYZ(SkyPos pos,Vec3 center){
            return center.add(
                    pos.radius * Math.sin(pos.theta)*Math.cos(pos.phi),
                    pos.radius * Math.cos(pos.theta),
                    pos.radius * Math.sin(pos.theta)*Math.sin(pos.phi));
        }
    }
}
