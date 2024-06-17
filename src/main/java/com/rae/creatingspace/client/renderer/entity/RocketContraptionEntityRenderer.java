package com.rae.creatingspace.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class RocketContraptionEntityRenderer extends ContraptionEntityRenderer<RocketContraptionEntity> {
    public RocketContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RocketContraptionEntity entity, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int overlay) {
        super.render(entity, yaw, partialTicks, matrixStack, buffers, overlay);
        VertexConsumer vertexBuilder = buffers.getBuffer(RenderType.tripwire());
        matrixStack.pushPose();

        // Translate and rotate the cone to the entity's position and orientation
        matrixStack.translate(0.0D, 2D, 0.0D);
        //matrixStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(-entity.getYawOffset(
        //)));
        //matrixStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(90.0F));

        // Define the cone parameters
        float radius = 0.5F; // Radius of the cone base
        float height = 2F; // Height of the cone
        int segments = 10; // Number of segments for the base circle

        // Render the cone
        renderCylinder(vertexBuilder, matrixStack, overlay, radius, height, segments);
        matrixStack.translate(0.0D, 2D, 0.0D);
        renderCylinder(vertexBuilder, matrixStack, overlay, radius, height, segments);
        matrixStack.popPose();
    }

    private void renderCylinder(VertexConsumer vertexBuilder, PoseStack matrixStack, int packedLight, float radius, float height, int segments) {
        float angleIncrement = (float) (2 * Math.PI / segments);

        // Define the base vertices
        for (int i = 0; i < segments; i++) {
            float angle1 = i * angleIncrement;
            float angle2 = (i + 1) % segments * angleIncrement;

            List<Vec3> sideFace = new ArrayList<>();
            sideFace.add(new Vec3(0, height, 0));
            sideFace.add(new Vec3(0, height, 0));
            //sideFace.add(new Vec3((radius+0.3f) * Mth.cos(angle1), height, (radius+0.3f) * Mth.sin(angle1)));
            //sideFace.add(new Vec3((radius+0.3f) * Mth.cos(angle2), height, (radius+0.3f) * Mth.sin(angle2)));
            sideFace.add(new Vec3(radius * Mth.cos(angle2), 0, radius * Mth.sin(angle2)));
            sideFace.add(new Vec3(radius * Mth.cos(angle1), 0, radius * Mth.sin(angle1)));
            // Render the side face using renderPoly
            renderPoly(sideFace, vertexBuilder, matrixStack, packedLight);
            /*List<Vec3> sideFace2 = new ArrayList<>();
            sideFace2.add(new Vec3(x1, 0, z1));
            sideFace2.add(new Vec3(x2, 0, z2));
            sideFace2.add(new Vec3(0, 0, 0));

            renderPoly(sideFace2, vertexBuilder, matrixStack, packedLight);*/
        }
    }

    //vertex seems to only work with 4
    private void renderPoly(List<Vec3> pos, VertexConsumer vertexBuilder, PoseStack matrixStack, int packedLight) {
        Vec3 centerPos = new Vec3(0, 0, 0);
        for (Vec3 coord : pos) {
            centerPos = centerPos.add(coord);
        }
        centerPos = centerPos.multiply(1d / pos.size(), 1d / pos.size(), 1d / pos.size());
        PoseStack.Pose entry = matrixStack.last();
        for (Vec3 coord : pos) {
            Vec3 normal = coord.subtract(centerPos);
            vertexBuilder.vertex(entry.pose(), (float) coord.x, (float) coord.y, (float) coord.z)
                    .color(255, 255, 255, 255)
                    .uv(0, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(entry.normal(), (float) normal.x, (float) normal.y, (float) normal.z)
                    .endVertex();
        }
    }
    @Override
    public boolean shouldRender(RocketContraptionEntity entity, Frustum clippingHelper, double cameraX, double cameraY,
                                double cameraZ) {
        if (entity.getContraption() == null)
            return false;
        if (!entity.isAliveOrStale())
            return false;
        if (!entity.isReadyForRender())
            return false;

        return super.shouldRender(entity, clippingHelper, cameraX, cameraY, cameraZ);
    }

    @Override
    public Vec3 getRenderOffset(RocketContraptionEntity p_114483_, float p_114484_) {
        return super.getRenderOffset(p_114483_, p_114484_);
    }
}
