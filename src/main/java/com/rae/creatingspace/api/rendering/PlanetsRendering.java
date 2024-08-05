package com.rae.creatingspace.api.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.rae.creatingspace.api.rendering.GeometryRendering.renderCube;
import static com.rae.creatingspace.api.rendering.GeometryRendering.renderPolyTex;

public class PlanetsRendering {
    /**
     * @param texture     the texture of the planet
     * @param buffer      the buffer source
     * @param matrixStack the stack
     * @param packedLight the light
     * @param distance    the distance from the player
     * @param theta       the angle from the horizon
     * @param phi         the angle from north
     * @param alpha       the rotation of the planet
     */
    public static void renderPlanet(ResourceLocation texture, MultiBufferSource buffer, PoseStack matrixStack,
                                    int packedLight, float size, float distance, float theta, float phi, float alpha) {
        VertexConsumer planetBuffer = buffer.getBuffer(RenderType.entityTranslucent(texture));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(phi));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(theta));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(alpha));
        matrixStack.translate(distance, 0, 0);
        float halfSize = size / 2.0F;

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
        List<Vec3> face1 = List.of(v0, v3, v2, v1); // Front face
        List<Vec3> face2 = List.of(v5, v6, v7, v4); // Back face
        List<Vec3> face3 = List.of(v1, v2, v6, v5); // Right face
        List<Vec3> face4 = List.of(v4, v7, v3, v0); // Left face
        List<Vec3> face5 = List.of(v3, v7, v6, v2); // Top face
        List<Vec3> face6 = List.of(v0, v1, v5, v4); // Bottom face

        List<Vec2> uvs = List.of(new Vec2(0, 0), new Vec2(0, 1),
                new Vec2(1, 1), new Vec2(1, 0));
        // Render each face using renderPoly
        PoseStack.Pose entry = matrixStack.last();
        renderPolyTex(face1, uvs, planetBuffer, entry, packedLight);
        renderPolyTex(face2, uvs, planetBuffer, entry, packedLight);
        renderPolyTex(face3, uvs, planetBuffer, entry, packedLight);
        renderPolyTex(face4, uvs, planetBuffer, entry, packedLight);
        renderPolyTex(face5, uvs, planetBuffer, entry, packedLight);
        renderPolyTex(face6, uvs, planetBuffer, entry, packedLight);
    }

    /**
     * to use when no access to the MultiSourceBuffer (DimensionSpecialEffect)
     */
    public static void renderPlanet(ResourceLocation texture, PoseStack matrixStack,
                                    int packedLight, float size, float distance, float theta, float phi, float alpha) {
        renderPlanet(texture, SuperRenderTypeBuffer.getInstance(), matrixStack, packedLight, size, distance, theta, phi, alpha);

    }

    public static void renderAtmosphere(MultiBufferSource buffer, PoseStack matrixStack, Color color,
                                        int packedLight, float size, float distance, float theta, float phi, float alpha) {
        VertexConsumer vertexBuilder = buffer.getBuffer(RenderTypes.getGlowingTranslucent(AllSpecialTextures.BLANK.getLocation()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(phi));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(theta));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(alpha));
        matrixStack.translate(distance, 0, 0);
        renderCube(vertexBuilder, matrixStack, Vec3.ZERO, packedLight, size + 10, color);
    }
}
