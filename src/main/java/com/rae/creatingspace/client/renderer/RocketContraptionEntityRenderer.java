package com.rae.creatingspace.client.renderer;

import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;

public class RocketContraptionEntityRenderer extends ContraptionEntityRenderer<RocketContraptionEntity> {
    public RocketContraptionEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    /*public ResourceLocation getTextureLocation(RocketContraptionEntity entity) {
        return null;
    }*/

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
