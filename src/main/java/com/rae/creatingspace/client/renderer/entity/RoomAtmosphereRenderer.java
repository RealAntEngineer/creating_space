package com.rae.creatingspace.client.renderer.entity;

import com.rae.creatingspace.server.entities.RoomAtmosphere;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RoomAtmosphereRenderer extends EntityRenderer<RoomAtmosphere> {
    public RoomAtmosphereRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public boolean shouldRender(RoomAtmosphere atmosphere, Frustum frustum, double p_114493_, double p_114494_, double p_114495_) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(RoomAtmosphere atmosphere) {
        return null;
    }
}
