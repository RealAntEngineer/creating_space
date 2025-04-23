package com.rae.creatingspace.content.planets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rae.creatingspace.api.planets.OrbitParameter;
import com.rae.creatingspace.api.rendering.PlanetsRendering;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static com.rae.creatingspace.api.planets.OrbitParameter.BASE_BODY;

/**
 * Handles the positions and rendering of planets in the game.
 * It calculates the position of planets based on orbital parameters and renders them.
 */
public class PlanetsPositionsHandler {
    // Stores the orbit parameters for planets
    private static final Map<ResourceLocation, OrbitParameter> positions = new HashMap<>();

    static {
        // Initialize decorative planets
        positions.put(BASE_BODY, new OrbitParameter(BASE_BODY, 20, 0, 0, 0, 10,0, new Vec3(0, 1, 0), 10,0));
    }

    /**
     * Gets the orbit parameter for a given planet.
     *
     * @param location The location (ID) of the planet.
     * @return The orbit parameter of the planet.
     */
    public static OrbitParameter getOrbitParam(ResourceLocation location) {
        return positions.get(location);
    }

    /**
     * Sets the orbit parameter for a given planet.
     *
     * @param planet The location (ID) of the planet.
     * @param param The orbit parameter to set.
     */
    public static void setOrbitParam(@Nonnull ResourceLocation planet, OrbitParameter param) {
        positions.put(planet, param);
    }

    /**
     * Calculates the position of one planet relative to another at a given time.
     * The time is in Overworld days.
     *
     * @param planet The central planet.
     * @param toRender The planet to render.
     * @param time The current time in Overworld days.
     * @return The relative position of the planet to render.
     */
    public static SkyPos getSkyPos(ResourceLocation planet, ResourceLocation toRender, float time) {
        if (!planet.equals(toRender)) {
            Vec3 planetCartCoord = calculateCartesianCoordinate(planet, time);
            Vec3 toRenderCartCoord = calculateCartesianCoordinate(toRender, time);
            return SkyPos.fromXYZ(toRenderCartCoord, planetCartCoord);
        }
        return SkyPos.ZERO;
    }

    /**
     * Calculates the Cartesian coordinates of a planet based on its orbital parameters.
     * The time is in Overworld days.
     *
     * @param planet The location (ID) of the planet.
     * @param time The current time in Overworld days.
     * @return The Cartesian coordinates of the planet.
     */
    private static Vec3 calculateCartesianCoordinate(ResourceLocation planet, float time) {
        Vec3 cartesian = Vec3.ZERO;
        OrbitParameter current = positions.get(planet);
        if (current == null) {
            return Vec3.ZERO;
        }
        int depth = 0;

        // Traverse through orbital bodies up to a certain depth to calculate the position
        while (!current.getOrbitedBody().equals(planet) && depth < 10) {
            float d = current.getR();
            float theta = current.getOrbAngle(time);
            float omega = current.getOmega() * 2 * Mth.PI; // assuming omega is in [0, 1)
            float inclination = current.getI() / 180f * Mth.PI;

            float cosOmega = Mth.cos(omega);
            float sinOmega = Mth.sin(omega);
            float cosTheta = Mth.cos(theta);
            float sinTheta = Mth.sin(theta);
            float cosI = Mth.cos(inclination);
            float sinI = Mth.sin(inclination);
            if (planet.equals(new ResourceLocation("creatingspace:the_moon"))){
                //System.out.println("sinTheta : "+sinTheta+ "cosTheta : "+cosTheta);
            }
            // Correct 3D orbital position
            float x = d * (-sinOmega * cosTheta + cosOmega * sinTheta * cosI);
            float y = d * (sinTheta * sinI);
            float z = d * (cosOmega * cosTheta +  sinOmega * sinTheta * cosI);

            cartesian = cartesian.add(x, y, z);
            if (current.getOrbitedBody().equals(BASE_BODY)) break;
            current = positions.get(current.getOrbitedBody());
            if (current == null) break;
            depth++;
        }
        return cartesian;
    }

    /**
     * Renders the planets for all the necessary celestial bodies in the game.
     * This includes applying rotations and drawing the planets at the correct positions.
     * The time is in Overworld days.
     *
     * @param skyColor     The color of the sky
     * @param time         The current time in Overworld days.
     * @param ms           The matrix stack used for transformations.
     * @param bufferSource The buffer source to store the rendered planets.
     * @param center       The location of the center planet.
     * @param renderCenter Whether to render the center planet or not.
     */
    public static void renderForAll(float time, PoseStack ms, MultiBufferSource bufferSource, ResourceLocation center, boolean renderCenter, Color skyColor, boolean squeezeMode) {
        if (positions.containsKey(center)) {
            applyRotation(ms, positions.get(center), -time);
        }
        try {
            // Collect the positions of all planets that need to be rendered
            Map<ResourceLocation, SkyPos> collectedPos = positions.entrySet().stream()
                    .filter(entry -> shouldRender(entry.getKey(), center, renderCenter))
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> getSkyPos(center, entry.getKey(), time)));

            // Sort planets by distance and render them in order
            List<ResourceLocation> sortedByDistance = collectedPos.entrySet().stream()
                    .sorted(Comparator.comparingDouble(e -> -e.getValue().radius))
                    .map(Map.Entry::getKey)
                    .toList();
            Vector3f viewDir = Minecraft.getInstance().gameRenderer.getMainCamera().getLookVector();
            sortedByDistance.forEach(location -> {
                if (location.equals(BASE_BODY)) renderSun(ms, bufferSource, time, location, collectedPos.get(location));
                else renderPlanet(ms, bufferSource, time, location, collectedPos.get(location), skyColor, squeezeMode);
            });
        } catch (NullPointerException ignored) {//catch if center is not on the

        }
        if (positions.containsKey(center)) {
            applyRotation(ms, positions.get(center), time);
        }
    }

    /**
     * Checks if a planet should be rendered based on its position and the center planet.
     *
     * @param location The location (ID) of the planet.
     * @param center The location (ID) of the center planet.
     * @param renderCenter Whether to render the center planet or not.
     * @return True if the planet should be rendered, false otherwise.
     */
    private static boolean shouldRender(ResourceLocation location, ResourceLocation center, boolean renderCenter) {
        return (CSDimensionUtil.shouldRenderAsPlanet(location) && (!location.equals(center) || renderCenter)||location.equals(BASE_BODY) && positions.containsKey(location));
    }

    /**
     * Applies a rotational transformation to the matrix stack based on the planet's rotation.
     * The time is in Overworld days.
     *
     * @param ms The matrix stack to apply the transformation to.
     * @param orbitParameter The orbit parameter of the planet.
     * @param time The time factor to determine the rotation angle.
     */
    private static void applyRotation(PoseStack ms, @NotNull OrbitParameter orbitParameter, float time) {
        ms.mulPose(orbitParameter.getRotQuad(time));
    }

    /**
     * Renders a planet at its calculated position with the correct rotation.
     * The time is in Overworld days.
     *
     * @param ms           The matrix stack to apply transformations.
     * @param bufferSource The buffer source to store the rendered planet.
     * @param time         The current time in Overworld days.
     * @param location     The location (ID) of the planet.
     * @param pos          The calculated position of the planet.
     * @param skyColor     The color of the sky
     */
    private static void renderPlanet(PoseStack ms, MultiBufferSource bufferSource, float time, ResourceLocation location, SkyPos pos,
                                     Color skyColor, boolean squeezeMode) {
        OrbitParameter orbitParameter = positions.get(location);

        PlanetsRendering.renderPlanet(
                new ResourceLocation(location.getNamespace(), "textures/environment/planets/" + location.getPath() + ".png"),
                bufferSource, ms, LightTexture.FULL_BRIGHT, orbitParameter.getSize(), pos, orbitParameter.getRotQuad(time), skyColor, squeezeMode);


    }

    private static void renderSun(PoseStack ms, MultiBufferSource bufferSource, float time, ResourceLocation location, SkyPos pos) {
        OrbitParameter orbitParameter = positions.get(location);

        PlanetsRendering.renderSun(
                bufferSource, ms, new Color(250,239,11,128), orbitParameter.getSize(), pos, orbitParameter.getRotQuad(time));


    }
    /**
     * Represents a position in 3D space using cylindrical coordinates.
     * Y-axis is assumed to be pointing upwards.
     */
    public static class SkyPos {
        float radius;  // Radial distance from the center
        float theta;   // Azimuthal angle in the XZ plane
        float phi;     // Polar angle

        public static final SkyPos ZERO = new SkyPos(0, 0, 0);

        /**
         * Constructs a new SkyPos instance.
         *
         * @param radius The radial distance from the center.
         * @param theta The azimuthal angle in the XZ plane.
         * @param phi The polar angle.
         */
        public SkyPos(float radius, float theta, float phi) {
            this.radius = radius;
            this.theta = theta;
            this.phi = phi;
        }

        /**
         * Converts 3D Cartesian coordinates to cylindrical coordinates (SkyPos).
         *
         * @param satellite The coordinates of the satellite.
         * @param center The coordinates of the center point.
         * @return The corresponding cylindrical coordinates (SkyPos).
         */
        public static SkyPos fromXYZ(@Nonnull Vec3 satellite, @Nonnull Vec3 center) {
            Vec3 diff = satellite.subtract(center);
            double distance = diff.length();
            double phi = Math.PI / 2 - Math.acos(diff.y / distance);
            double theta = Math.atan2(diff.x, diff.z);
            return new SkyPos((float) distance, (float) theta, (float) phi);
        }

        /**
         * Converts cylindrical coordinates (SkyPos) back to 3D Cartesian coordinates.
         *
         * @param pos The cylindrical coordinates to convert.
         * @param center The coordinates of the center point.
         * @return The corresponding 3D Cartesian coordinates.
         */
        public static Vec3 toXYZ(@Nonnull SkyPos pos, @Nonnull Vec3 center) {
            return center.add(
                    pos.radius * Math.cos(pos.phi) * Math.sin(pos.theta),
                    pos.radius * Math.sin(pos.phi),
                    pos.radius * Math.cos(pos.phi) * Math.cos(pos.theta)
            );
        }

        public float getTheta() {
            return theta;
        }

        public float getRadius() {
            return radius;
        }
    }
}
