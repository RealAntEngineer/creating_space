package com.rae.creatingspace.api.rendering;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.AllSpecialTextures;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class CSRenderTypes extends RenderStateShard {
    //maybe use something like that : https://github.com/bernie-g/geckolib/blob/main/common/src/main/java/software/bernie/geckolib/cache/texture/AutoGlowingTexture.java#L36
    private static final RenderType TRANSLUCENT_NO_TEXT = RenderType.create(createLayerName("translucent"),
            DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
            256, true, true, RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_TRANSLUCENT_SHADER)
                    .setTextureState(RenderStateShard.NO_TEXTURE)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .setWriteMaskState(COLOR_WRITE)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .createCompositeState(false));

    public static RenderType getTranslucentAtmo() {
        return RenderType.create(createLayerName("translucent_atmo"),
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS,
                256, false, true, RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(AllSpecialTextures.BLANK.getLocation(), false, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        //.setOverlayState(OVERLAY)
                        //.setTexturingState(RenderStateShard.GLINT_TEXTURING)
                        .setWriteMaskState(COLOR_WRITE)
                        .setDepthTestState(NO_DEPTH_TEST)
                        .createCompositeState(false));//TRANSLUCENT_NO_TEXT;
    }
    public static RenderType getTranslucentPlanet(ResourceLocation location){
        return RenderType.create(createLayerName("translucent_planet"),
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS,
                256, false, true, RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                        .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                        //.setLightmapState(RenderStateShard.NO_LIGHTMAP)
                        //.setOverlayState(OVERLAY)
                        //.setTexturingState(RenderStateShard.GLINT_TEXTURING)e
                        .setWriteMaskState(COLOR_WRITE)
                        //.setDepthTestState(NO_DEPTH_TEST)
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
