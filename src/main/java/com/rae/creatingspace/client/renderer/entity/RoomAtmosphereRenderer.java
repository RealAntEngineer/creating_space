package com.rae.creatingspace.client.renderer.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.server.entities.RoomAtmosphere;
import com.simibubi.create.foundation.outliner.AABBOutline;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class RoomAtmosphereRenderer extends EntityRenderer<RoomAtmosphere> {
    private static final ResourceLocation ROOM_ATMOSPHERE_LOCATION = CreatingSpace.resource("textures/entity/room_atmosphere_overlay.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(ROOM_ATMOSPHERE_LOCATION);

    public RoomAtmosphereRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public boolean shouldRender(@NotNull RoomAtmosphere atmosphere, @NotNull Frustum frustum, double p_114493_, double p_114494_, double p_114495_) {
        return CSConfigs.CLIENT.oxygenRoomDebugMode.get();
    }

    @Override
    public void render(@NotNull RoomAtmosphere roomAtmosphere, float cameraX, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int p_114604_) {
        poseStack.pushPose();
        float pt = AnimationTickHolder.getPartialTicks();
        SuperRenderTypeBuffer superBuffer = SuperRenderTypeBuffer.getInstance();
        for (AABB aabb : roomAtmosphere.getShape().getListOfBox()) {
            AABBOutline outline = new AABBOutline(aabb);
            Color outlineColor = roomAtmosphere.breathable()?new Color(30, 50, (int)(200 * roomAtmosphere.getO2concentration()/100 ), 20):new Color(50, 30,0 );
            outline.getParams().colored(outlineColor);
            outline.render(poseStack, superBuffer, roomAtmosphere.position(), pt);
        }
        superBuffer.draw();
        RenderSystem.enableCull();
        poseStack.popPose();
        super.render(roomAtmosphere, cameraX, partialTick, poseStack, bufferSource, p_114604_);
    }

    //TODO use Create's AABBOutline
    private static void renderAABB(Vec3 pos, AABB aabb, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int p_114604_) {
        Vec3 aabbCenter = aabb.getCenter();
        poseStack.pushPose();
        float f = 0;
        float f1 = 1;
        float f2 = 0;
        float f3 = 1;
        poseStack.translate(aabbCenter.x() - pos.x(), aabbCenter.y() - pos.y(), aabb.getZsize() / 2 + aabbCenter.z() - pos.z());
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        vertex(vertexconsumer, matrix4f, matrix3f, -(float) (aabb.getXsize() / 2), -(float) (aabb.getYsize() / 2), 255, 255, 255, f, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) (aabb.getXsize() / 2), -(float) (aabb.getYsize() / 2), 255, 255, 255, f1, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) (aabb.getXsize() / 2), (float) (aabb.getYsize() / 2), 255, 255, 255, f1, f2, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, -(float) (aabb.getXsize() / 2), (float) (aabb.getYsize() / 2), 255, 255, 255, f, f2, p_114604_);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(aabbCenter.x() - pos.x(), aabbCenter.y() - pos.y(), -aabb.getZsize() / 2 + aabbCenter.z() - pos.z());
        vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        posestack$pose = poseStack.last();
        matrix4f = posestack$pose.pose();
        matrix3f = posestack$pose.normal();
        vertex(vertexconsumer, matrix4f, matrix3f, -(float) (aabb.getXsize() / 2), -(float) (aabb.getYsize() / 2), 255, 255, 255, f, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) (aabb.getXsize() / 2), -(float) (aabb.getYsize() / 2), 255, 255, 255, f1, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) (aabb.getXsize() / 2), (float) (aabb.getYsize() / 2), 255, 255, 255, f1, f2, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, -(float) (aabb.getXsize() / 2), (float) (aabb.getYsize() / 2), 255, 255, 255, f, f2, p_114604_);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(aabbCenter.x() - pos.x(), aabbCenter.y() - pos.y() - aabb.getYsize() / 2, aabbCenter.z() - pos.z());
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        posestack$pose = poseStack.last();
        matrix4f = posestack$pose.pose();
        matrix3f = posestack$pose.normal();
        vertex(vertexconsumer, matrix4f, matrix3f, -((float) aabb.getXsize() / 2), -((float) aabb.getZsize() / 2), 255, 255, 255, f, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) aabb.getXsize() / 2, -((float) aabb.getZsize() / 2), 255, 255, 255, f1, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) aabb.getXsize() / 2, (float) aabb.getZsize() / 2, 255, 255, 255, f1, f2, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, -((float) aabb.getXsize() / 2), (float) aabb.getZsize() / 2, 255, 255, 255, f, f2, p_114604_);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(aabbCenter.x() - pos.x(), aabbCenter.y() - pos.y() + aabb.getYsize() / 2, aabbCenter.z() - pos.z());
        poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        posestack$pose = poseStack.last();
        matrix4f = posestack$pose.pose();
        matrix3f = posestack$pose.normal();
        vertex(vertexconsumer, matrix4f, matrix3f, -((float) aabb.getXsize() / 2), -((float) aabb.getZsize() / 2), 255, 255, 255, f, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) aabb.getXsize() / 2, -((float) aabb.getZsize() / 2), 255, 255, 255, f1, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) aabb.getXsize() / 2, (float) aabb.getZsize() / 2, 255, 255, 255, f1, f2, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, -((float) aabb.getXsize() / 2), (float) aabb.getZsize() / 2, 255, 255, 255, f, f2, p_114604_);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(aabbCenter.x() - pos.x() - aabb.getXsize() / 2, aabbCenter.y() - pos.y(), aabbCenter.z() - pos.z());
        poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        posestack$pose = poseStack.last();
        matrix4f = posestack$pose.pose();
        matrix3f = posestack$pose.normal();
        vertex(vertexconsumer, matrix4f, matrix3f, -((float) aabb.getZsize() / 2), -((float) aabb.getYsize() / 2), 255, 255, 255, f, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) aabb.getZsize() / 2, -((float) aabb.getYsize() / 2), 255, 255, 255, f1, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) aabb.getZsize() / 2, (float) aabb.getYsize() / 2, 255, 255, 255, f1, f2, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, -((float) aabb.getZsize() / 2), (float) aabb.getYsize() / 2, 255, 255, 255, f, f2, p_114604_);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(aabbCenter.x() - pos.x() + aabb.getXsize() / 2, aabbCenter.y() - pos.y(), aabbCenter.z() - pos.z());
        poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
        vertexconsumer = bufferSource.getBuffer(RENDER_TYPE);
        posestack$pose = poseStack.last();
        matrix4f = posestack$pose.pose();
        matrix3f = posestack$pose.normal();
        vertex(vertexconsumer, matrix4f, matrix3f, -((float) aabb.getZsize() / 2), -((float) aabb.getYsize() / 2), 255, 255, 255, f, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) aabb.getZsize() / 2, -((float) aabb.getYsize() / 2), 255, 255, 255, f1, f3, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, (float) aabb.getZsize() / 2, (float) aabb.getYsize() / 2, 255, 255, 255, f1, f2, p_114604_);
        vertex(vertexconsumer, matrix4f, matrix3f, -((float) aabb.getZsize() / 2), (float) aabb.getYsize() / 2, 255, 255, 255, f, f2, p_114604_);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f p_114611_, float x, float y, int red, int green, int blue, float texX, float texY, int p_114619_) {
        vertexConsumer.vertex(matrix4f, x, y, 0.0F).color(red, green, blue, 32)
                .uv(texX, texY).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(p_114619_)
                .normal(p_114611_, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
    @Override
    public ResourceLocation getTextureLocation(RoomAtmosphere atmosphere) {
        return ROOM_ATMOSPHERE_LOCATION;
    }
}
