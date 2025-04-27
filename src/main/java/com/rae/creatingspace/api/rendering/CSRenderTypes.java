package com.rae.creatingspace.api.rendering;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rae.creatingspace.CreatingSpace;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class CSRenderTypes extends RenderStateShard {
    //maybe use something like that : https://github.com/bernie-g/geckolib/blob/main/common/src/main/java/software/bernie/geckolib/cache/texture/AutoGlowingTexture.java#L36
    private static final RenderType ADDITIVE_NO_TEXT = RenderType.create(createLayerName("stars"),
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS,
            256, false, true, RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setCullState(NO_CULL)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false));

    public static RenderType additiveNoText() {
        return ADDITIVE_NO_TEXT;
    }

    public static RenderType translucentAtmosphere(ResourceLocation texture) {
        return RenderType.create(createLayerName("planet_atmosphere"),
                DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS,
                256, false, true, RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                        .setCullState(NO_CULL)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(false));
    }
    public static RenderType getPlanetSolid(ResourceLocation location){
        return RenderType.create(createLayerName("planet_solid"),
                DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS,
                256, false, true, RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.POSITION_TEX_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setCullState(NO_CULL)
                        .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(false));
    }

    private static String createLayerName(String name) {
        return CreatingSpace.MODID + ":" + name;
    }

    // Yummy protected fields
    private CSRenderTypes() {
        super(null, null, null);
    }
}
