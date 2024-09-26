package com.rae.creatingspace.content.planets;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlanetsPosition {
    //to put into the rocket accessible dim later

    static {
        positions = new HashMap<>();
        setOrbitParam(CreatingSpace.resource("sun"), null);
        setOrbitParam(CreatingSpace.resource("earth"), new OrbitParam(300, 1, 20, CreatingSpace.resource("sun")));
        setOrbitParam(CreatingSpace.resource("moon"), new OrbitParam(800, 800, 5, CreatingSpace.resource("earth")));
    }
    private static final Map<ResourceLocation, OrbitParam> positions;

    public static void setOrbitParam(@Nonnull ResourceLocation planet,OrbitParam param){
        positions.put(planet, param);
    }
    public static SkyPos getSkyPos(ResourceLocation planet,ResourceLocation toRender, float time) {
        if (!planet.equals(toRender)) {
            ArrayList<OrbitParam> planetParams = new ArrayList<>();
            planetParams.add(positions.getOrDefault(planet, null));
            ArrayList<OrbitParam> toRenderParams = new ArrayList<>();
            toRenderParams.add(positions.getOrDefault(toRender, null));
            int depth = 0;//depth
            while (toRenderParams.get(0) != null && depth < 10) {
                toRenderParams.add(0, positions.get(toRenderParams.get(0).orbitedBody()));//is this really necessary ?
                depth++;

            }
            depth = 0;//depth
            while (planetParams.get(0) != null && depth < 10) {
                planetParams.add(0, positions.get(planetParams.get(0).orbitedBody()));//is this really necessary ?
                depth++;

            }
            Vec3 planetCartCoord = new Vec3(0,0,0);
            Vec3 toRenderCartCoord = new Vec3(0,0,0);
            for (OrbitParam temp: planetParams){
                if (temp !=null) {
                    float d = temp.distanceToOrbitedBody;
                    float theta = (float) (time / temp.period * Math.PI);
                    planetCartCoord = planetCartCoord.add(Math.sin(theta) * d, 0, Math.cos(theta) * d);
                }
            }
            for (OrbitParam temp: toRenderParams){
                if (temp !=null) {
                    float d = temp.distanceToOrbitedBody;
                    float theta = (float) (time / temp.period * Math.PI);
                    toRenderCartCoord = toRenderCartCoord.add(Math.sin(theta) * d, 0, Math.cos(theta) * d);
                }
            }
            return SkyPos.fromXYZ(toRenderCartCoord, planetCartCoord);
        }
        return SkyPos.ZERO;
    }

    public record OrbitParam(float period, float lengthOfDay, float distanceToOrbitedBody, @Nonnull ResourceLocation orbitedBody){

    }

    /**
     * this class is a representation of cylindrical coordinates with math convention and y pointed upward
     */
    public static class SkyPos {
        float radius;
        float theta;
        float phi;
        public static SkyPos ZERO = new SkyPos(0, 0,0);
        public SkyPos(float distance, float theta, float phi) {
            this.radius = distance;
            this.theta = theta;
            this.phi = phi;
        }
        public static SkyPos fromXYZ(@Nonnull Vec3 satellite,@Nonnull Vec3 center){
            Vec3 diff = satellite.subtract(center);
            double distance = diff.length();
            double phi = Math.PI/2 - Math.acos(diff.y/distance);
            double theta = Math.signum(diff.x)*Math.acos(diff.z/diff.horizontalDistance());
            return new SkyPos((float) distance, (float) theta, (float) phi);
        }
        public static Vec3 toXYZ(@Nonnull SkyPos pos,@Nonnull Vec3 center){
            return center.add(
                    pos.radius * Math.sin(pos.phi)*Math.cos(pos.theta),
                    pos.radius * Math.cos(pos.phi),
                    pos.radius * Math.sin(pos.phi)*Math.sin(pos.theta));
        }
        public float getRadius() {
            return radius;
        }

        public float getTheta() {
            return theta;
        }

        public float getPhi() {
            return phi;
        }
        /*public static Quaternion toQuaternion(@Nonnull SkyPos pos){
            return Quaternion.
                    pos.radius * Math.sin(pos.theta)*Math.cos(pos.phi),
                    pos.radius * Math.cos(pos.theta),
                    pos.radius * Math.sin(pos.theta)*Math.sin(pos.phi));
        }*/

    }
}
