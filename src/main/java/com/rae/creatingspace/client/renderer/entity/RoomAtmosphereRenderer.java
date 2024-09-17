package com.rae.creatingspace.client.renderer.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.server.entities.RoomAtmosphere;
import com.simibubi.create.foundation.outliner.AABBOutline;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class RoomAtmosphereRenderer extends EntityRenderer<RoomAtmosphere> {
    private static final ResourceLocation ROOM_ATMOSPHERE_LOCATION = CreatingSpace.resource("textures/entity/room_atmosphere_overlay.png");

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

    @Override
    public ResourceLocation getTextureLocation(RoomAtmosphere atmosphere) {
        return ROOM_ATMOSPHERE_LOCATION;
    }
}
