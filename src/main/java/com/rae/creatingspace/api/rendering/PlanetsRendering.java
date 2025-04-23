package com.rae.creatingspace.api.rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rae.creatingspace.content.planets.PlanetsPositionsHandler;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.List;

import static com.rae.creatingspace.api.rendering.GeometryRendering.renderCube;
import static com.rae.creatingspace.api.rendering.GeometryRendering.renderPolyTex;

public class PlanetsRendering {
    // non standard winding (top-left, bottom-left, bottom-right, top-right)
    static List<Vec2> cubeUV(int col, int row) {
        // UV TODO make the step configurable.
        float uStep = 1f / 3f;
        float vStep = 1f / 2f;

        float u0 = col * uStep;
        float u1 = u0 + uStep;
        float v0 = row * vStep;
        float v1 = v0 + vStep;
        return List.of(
                new Vec2(u1, v1), // Bottom-right
                new Vec2(u1, v0),  // Top-right
                new Vec2(u0, v0), // Top-left
                new Vec2(u0, v1) // Bottom-left
        );
    }
    /**
     * @param texture        the texture of the planet
     * @param buffer         the buffer source
     * @param matrixStack    the stack
     * @param packedLight    the light
     * @param planetPos      spherical coord for the planet
     * @param planetRotation rotation of the planet.
     */
    public static void renderPlanet(ResourceLocation texture, MultiBufferSource buffer, PoseStack matrixStack,
                                    int packedLight, float size, PlanetsPositionsHandler.SkyPos planetPos, Quaternionf planetRotation, Color skyColor, boolean squeezeMode) {

        VertexConsumer planetBuffer =  buffer.getBuffer(CSRenderTypes.getPlanetSolid(texture));


        Vec3 translation = PlanetsPositionsHandler.SkyPos.toXYZ(planetPos, Vec3.ZERO);
        matrixStack.translate(translation.x(),translation.y(), translation.z());
        float halfSize = size / 2.0F;
        matrixStack.mulPose(planetRotation);

        // Define the eight vertices of the cube
        Vec3 v0 = new Vec3(-halfSize, -halfSize, -halfSize);
        Vec3 v1 = new Vec3(halfSize, -halfSize, -halfSize);
        Vec3 v2 = new Vec3(halfSize, halfSize, -halfSize);
        Vec3 v3 = new Vec3(-halfSize, halfSize, -halfSize);
        Vec3 v4 = new Vec3(-halfSize, -halfSize, halfSize);
        Vec3 v5 = new Vec3(halfSize, -halfSize, halfSize);
        Vec3 v6 = new Vec3(halfSize, halfSize, halfSize);
        Vec3 v7 = new Vec3(-halfSize, halfSize, halfSize);

// Create the six faces of the cube
        List<Vec3> face1 = List.of(v0, v3, v2, v1); // Front -> bottom right, top right,
        List<Vec3> face2 = List.of(v5, v6, v7, v4); // Back
        List<Vec3> face3 = List.of(v1, v2, v6, v5); // Right
        List<Vec3> face4 = List.of(v4, v7, v3, v0); // Left
        List<Vec3> face5 = List.of(v3, v7, v6, v2); // Top
        List<Vec3> face6 = List.of(v0, v1, v5, v4); // Bottom





        // Assign unique UVs
        List<Vec2> uvs1 = cubeUV(0, 0); // Front
        List<Vec2> uvs2 = cubeUV(1, 0); // Back
        List<Vec2> uvs3 = cubeUV(2, 0); // Right
        List<Vec2> uvs4 = cubeUV(0, 1); // Left
        List<Vec2> uvs5 = cubeUV(1, 1); // Top
        List<Vec2> uvs6 = cubeUV(2, 1); // Bottom

        // Render each face using renderPolyTex
        PoseStack.Pose entry = matrixStack.last();
        renderPolyTex(face1, uvs1, planetBuffer, entry, packedLight, skyColor);
        renderPolyTex(face2, uvs2, planetBuffer, entry, packedLight, skyColor);
        renderPolyTex(face3, uvs3, planetBuffer, entry, packedLight, skyColor);
        renderPolyTex(face4, uvs4, planetBuffer, entry, packedLight, skyColor);
        renderPolyTex(face5, uvs5, planetBuffer, entry, packedLight, skyColor);
        renderPolyTex(face6, uvs6, planetBuffer, entry, packedLight, skyColor);

        //undo transformation

        planetRotation.conjugate();
        matrixStack.mulPose(planetRotation);
        planetRotation.conjugate();
        matrixStack.translate(-translation.x(),-translation.y(),- translation.z());

    }

    /**
     * to use when no access to the MultiSourceBuffer (DimensionSpecialEffect)
     */
    public static void renderPlanet(ResourceLocation texture, PoseStack matrixStack,
                                    int packedLight, float size, PlanetsPositionsHandler.SkyPos planetPos, Quaternionf planetRotation,Color skyColor) {
        renderPlanet(texture, MultiBufferSource.immediate(new BufferBuilder(256)), matrixStack, packedLight, size,planetPos,planetRotation,skyColor,false);

    }

    public static void renderSun(MultiBufferSource buffer, PoseStack matrixStack, Color color, float size, PlanetsPositionsHandler.SkyPos planetPos, Quaternionf planetRotation) {
        VertexConsumer vertexBuilder = buffer.getBuffer(CSRenderTypes.additiveNoText());


        Vec3 translation = PlanetsPositionsHandler.SkyPos.toXYZ(planetPos, Vec3.ZERO);
        matrixStack.translate(translation.x(),translation.y(), translation.z());
        matrixStack.mulPose(planetRotation);
        float coreSize = size / 1.8f;
        for (int i = 0; i < 8; i++) {
            renderCube(vertexBuilder, matrixStack, Vec3.ZERO, LightTexture.FULL_SKY, coreSize + i/5f * (size-coreSize), color);
        }
        planetRotation.conjugate();
        matrixStack.mulPose(planetRotation);
        planetRotation.conjugate();

        matrixStack.translate(-translation.x(),-translation.y(), -translation.z());

    }
    public static void renderAtmosphere(MultiBufferSource buffer, PoseStack matrixStack, Color color,
                                        int packedLight, float size, PlanetsPositionsHandler.SkyPos planetPos, Quaternionf planetRotation) {
        VertexConsumer vertexBuilder = buffer.getBuffer(CSRenderTypes.additiveNoText());//RenderTypes.getGlowingTranslucent(AllSpecialTextures.BLANK.getLocation()));
        matrixStack.mulPose(planetRotation);

        Vec3 translation = PlanetsPositionsHandler.SkyPos.toXYZ(planetPos, Vec3.ZERO);
        matrixStack.translate(translation.x(),translation.y(), translation.z());
        renderCube(vertexBuilder, matrixStack, Vec3.ZERO, packedLight, size, color);
        matrixStack.translate(-translation.x(),-translation.y(), -translation.z());

        planetRotation.conjugate();
        matrixStack.mulPose(planetRotation);
        planetRotation.conjugate();
    }


}
