package com.rae.creatingspace.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rae.creatingspace.api.rendering.GeometryRendering;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class RocketContraptionEntityRenderer extends ContraptionEntityRenderer<RocketContraptionEntity> {
    public RocketContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(RocketContraptionEntity entity, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffers, int overlay) {
        super.render(entity, yaw, partialTicks, matrixStack, buffers, overlay);
        VertexConsumer vertexBuilder = buffers.getBuffer(RenderType.cutoutMipped());
        matrixStack.pushPose();
        // Translate and rotate the cone to the entity's position and orientation
        //matrixStack.mulPose(com.mojang.math.Vector3f.YP.rotationDegrees(-entity.getYawOffset(
        //)));
        //matrixStack.mulPose(com.mojang.math.Vector3f.XP.rotationDegrees(90.0F));

        // Define the cone parameters
        // Radius of the cone base
        float height = 2F; // Height of the cone
        int segments = 10; // Number of segments for the base circle

        // Render the cone
        GeometryRendering.renderCylinder(vertexBuilder, matrixStack, new Vec3(0, 2, 0), overlay, 0.5f, 0.7f, height, segments);
        GeometryRendering.renderCylinder(vertexBuilder, matrixStack, new Vec3(0, 4, 0), overlay, 0.7f, 0.5f, height, segments);
        GeometryRendering.renderCube(vertexBuilder, matrixStack, new Vec3(0, -90, 0), overlay, 10);
        matrixStack.popPose();
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