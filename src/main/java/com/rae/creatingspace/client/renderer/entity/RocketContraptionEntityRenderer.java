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

public class RocketContraptionEntityRenderer extends ContraptionEntityRenderer<RocketContraptionEntity> {
    public RocketContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RocketContraptionEntity entity, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int overlay) {
        super.render(entity, yaw, partialTicks, matrixStack, buffers, overlay);
        VertexConsumer vertexBuilder = buffers.getBuffer(RenderType.cutout());
        matrixStack.pushPose();

        // Translate and rotate the cone to the entity's position and orientation
        matrixStack.translate(0.0D, 2D, 0.0D);
        //matrixStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(-entity.getYawOffset(
        //)));
        //matrixStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(90.0F));

        // Define the cone parameters
        float radius = 1F; // Radius of the cone base
        float height = 1.5F; // Height of the cone
        int segments = 20; // Number of segments for the base circle

        // Render the cone
        renderCone(vertexBuilder, matrixStack, overlay, radius, height, segments);

        matrixStack.popPose();
    }

    private void renderCone(VertexConsumer vertexBuilder, PoseStack matrixStack, int packedLight, float radius, float height, int segments) {
        PoseStack.Pose entry = matrixStack.last();
        float angleIncrement = (float) (2 * Math.PI / segments);

        // Define the base vertices
        for (int i = 0; i < segments; i++) {
            float angle1 = i * angleIncrement;
            float angle2 = (i + 1) % segments * angleIncrement;

            float x1 = radius * Mth.cos(angle1);
            float z1 = radius * Mth.sin(angle1);
            float x2 = radius * Mth.cos(angle2);
            float z2 = radius * Mth.sin(angle2);

            // Calculate side normals
            Vec3 normal1 = new Vec3(x1, 0, z1).normalize();
            Vec3 normal2 = new Vec3(x2, 0, z2).normalize();

            // Draw side quad (x1, 0, z1) -> (x2, 0, z2) -> (x2, height, z2) -> (x1, height, z1)

            vertexBuilder.vertex(entry.pose(), x1, height, z1)
                    .color(255, 255, 255, 255)
                    .uv(0, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(entry.normal(), (float) normal1.x, (float) normal1.y, (float) normal1.z)
                    .endVertex();
            vertexBuilder.vertex(entry.pose(), x2, height, z2)
                    .color(255, 255, 255, 255)
                    .uv(0, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(entry.normal(), (float) normal2.x, (float) normal2.y, (float) normal2.z)
                    .endVertex();

            vertexBuilder.vertex(entry.pose(), x2, 0, z2)
                    .color(255, 255, 255, 255)
                    .uv(0, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(entry.normal(), (float) normal2.x, (float) normal2.y, (float) normal2.z)
                    .endVertex();
            vertexBuilder.vertex(entry.pose(), x1, 0, z1)
                    .color(255, 255, 255, 255)
                    .uv(0, 0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY)
                    .uv2(packedLight)
                    .normal(entry.normal(), (float) normal1.x, (float) normal1.y, (float) normal1.z)
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
