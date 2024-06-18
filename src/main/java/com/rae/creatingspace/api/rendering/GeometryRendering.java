package com.rae.creatingspace.api.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class GeometryRendering {
    public static void renderCylinder(VertexConsumer vertexBuilder, PoseStack matrixStack, Vec3 offset, int packedLight, float baseRadius, float topRadius, float height, int segments) {
        float angleIncrement = (float) (2 * Math.PI / segments);
        // Define the base vertices
        for (int i = 0; i < segments; i++) {
            float angle1 = i * angleIncrement;
            float angle2 = (i + 1) % segments * angleIncrement;

            List<Vec3> sideFace = new ArrayList<>();
            sideFace.add(new Vec3(topRadius * Mth.cos(angle1), height, topRadius * Mth.sin(angle1)).add(offset));
            sideFace.add(new Vec3(topRadius * Mth.cos(angle2), height, topRadius * Mth.sin(angle2)).add(offset));
            sideFace.add(new Vec3(baseRadius * Mth.cos(angle2), 0, baseRadius * Mth.sin(angle2)).add(offset));
            sideFace.add(new Vec3(baseRadius * Mth.cos(angle1), 0, baseRadius * Mth.sin(angle1)).add(offset));
            // Render the side face using renderPoly
            PoseStack.Pose entry = matrixStack.last();
            renderPoly(sideFace, vertexBuilder, entry, packedLight);
        }
    }

    public static void renderCube(VertexConsumer vertexBuilder, PoseStack matrixStack, Vec3 offset, int packedLight, float size) {
        float halfSize = size / 2.0F;

        // Define the eight vertices of the cube
        Vec3 v0 = new Vec3(-halfSize, -halfSize, -halfSize).add(offset);
        Vec3 v1 = new Vec3(halfSize, -halfSize, -halfSize).add(offset);
        Vec3 v2 = new Vec3(halfSize, halfSize, -halfSize).add(offset);
        Vec3 v3 = new Vec3(-halfSize, halfSize, -halfSize).add(offset);
        Vec3 v4 = new Vec3(-halfSize, -halfSize, halfSize).add(offset);
        Vec3 v5 = new Vec3(halfSize, -halfSize, halfSize).add(offset);
        Vec3 v6 = new Vec3(halfSize, halfSize, halfSize).add(offset);
        Vec3 v7 = new Vec3(-halfSize, halfSize, halfSize).add(offset);

        // Create the six faces of the cube
        List<Vec3> face1 = List.of(v0, v3, v2, v1); // Front face
        List<Vec3> face2 = List.of(v5, v6, v7, v4); // Back face
        List<Vec3> face3 = List.of(v1, v2, v6, v5); // Right face
        List<Vec3> face4 = List.of(v4, v7, v3, v0); // Left face
        List<Vec3> face5 = List.of(v3, v7, v6, v2); // Top face
        List<Vec3> face6 = List.of(v0, v1, v5, v4); // Bottom face

        // Render each face using renderPoly
        PoseStack.Pose entry = matrixStack.last();
        renderPoly(face1, vertexBuilder, entry, packedLight);
        renderPoly(face2, vertexBuilder, entry, packedLight);
        renderPoly(face3, vertexBuilder, entry, packedLight);
        renderPoly(face4, vertexBuilder, entry, packedLight);
        renderPoly(face5, vertexBuilder, entry, packedLight);
        renderPoly(face6, vertexBuilder, entry, packedLight);
    }

    public static void renderPoly(List<Vec3> pos, VertexConsumer vertexBuilder, PoseStack.Pose entry, int packedLight) {
        Vec3 centerPos = new Vec3(0, 0, 0);
        for (Vec3 coord : pos) {
            centerPos = centerPos.add(coord);
        }
        centerPos = centerPos.multiply(1d / pos.size(), 1d / pos.size(), 1d / pos.size());
        for (Vec3 coord : pos) {
            Vec3 normal = coord.subtract(centerPos);
            vertexBuilder.vertex(entry.pose(), (float) coord.x, (float) coord.y, (float) coord.z)
                    .color(255, 255, 255, 254)
                    .uv(0, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(entry.normal(), (float) normal.x, (float) normal.y, (float) normal.z)
                    .endVertex();
        }
    }
}