package com.rae.creatingspace.api.rendering;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rae.creatingspace.CreatingSpace;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class CSRenderTypes extends RenderStateShard {
    private static final RenderType TRANSLUCENT_NO_TEXT = RenderType.create(createLayerName("translucent"),
            DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS,
            256, true, true, RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false));

    public static RenderType getTranslucentNoText() {
        return TRANSLUCENT_NO_TEXT;
    }

    private static String createLayerName(String name) {
        return CreatingSpace.MODID + ":" + name;
    }

    // Yummy protected fields
    private CSRenderTypes() {
        super(null, null, null);
    }
}
