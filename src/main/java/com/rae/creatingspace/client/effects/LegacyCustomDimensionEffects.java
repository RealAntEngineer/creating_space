package com.rae.creatingspace.client.effects;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.rae.creatingspace.CreatingSpace;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class LegacyCustomDimensionEffects extends DimensionSpecialEffects {

    private static final ResourceLocation SPACE_SKY_LOCATION = new ResourceLocation(CreatingSpace.MODID, "textures/environment/space_sky.png");
    private static final ResourceLocation EARTH_LOCATION = new ResourceLocation(CreatingSpace.MODID, "textures/environment/earth.png");
    private static final ResourceLocation MOON_LOCATION = new ResourceLocation(CreatingSpace.MODID, "textures/environment/moon.png");

    private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation MOON_PHASES_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");

    public LegacyCustomDimensionEffects(float cloudLevel, boolean hasGround, DimensionSpecialEffects.SkyType skyType, boolean forceBrightLightmap, boolean constantAmbientLight) {
        super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 vec, float brightness) {
        return vec;
    }

    @Override
    public boolean isFoggyAt(int p_108874_, int p_108875_) {
        return false;
    }


    @OnlyIn(Dist.CLIENT)
    public static class EarthOrbitEffects extends LegacyCustomDimensionEffects {

        public EarthOrbitEffects() {
            super(Float.NaN, false, DimensionSpecialEffects.SkyType.NONE, false, false);
        }

        @Override
        public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
            return true;
        }

        @Override
        public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
            return true;
        }

        @Override
        public boolean tickRain(ClientLevel level, int ticks, Camera camera) {
            return true;
        }

        @Override
        public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {

            BufferBuilder bufferbuilder = renderSpaceSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);

            //render sun and moon
            poseStack.pushPose();
            float size = 30.0F;
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(camera.getEntity().getLevel().getTimeOfDay(partialTick) * 360.0F));

            Matrix4f matrix4f = poseStack.last().pose();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SUN_LOCATION);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, -size, 100.0F, -size).uv(0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, 100.0F, -size).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, 100.0F, size).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(matrix4f, -size, 100.0F, size).uv(0.0F, 1.0F).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());

            size = 20.0F;

            RenderSystem.setShaderTexture(0, MOON_PHASES_LOCATION);
            int k = camera.getEntity().getLevel().getMoonPhase();
            int l = k % 4;
            int i1 = k / 4 % 2;
            float f13 = (float) (l) / 4.0F;
            float f14 = (float) (i1) / 2.0F;
            float f15 = (float) (l + 1) / 4.0F;
            float f16 = (float) (i1 + 1) / 2.0F;
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, -size, -100.0F, size).uv(f15, f16).endVertex();
            bufferbuilder.vertex(matrix4f, size, -100.0F, size).uv(f13, f16).endVertex();
            bufferbuilder.vertex(matrix4f, size, -100.0F, -size).uv(f13, f14).endVertex();
            bufferbuilder.vertex(matrix4f, -size, -100.0F, -size).uv(f15, f14).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
            matrix4f = poseStack.last().pose();
            RenderSystem.setShaderTexture(0, EARTH_LOCATION);

            BlockPos pos = camera.getEntity().getOnPos();
            int height = pos.getY();
            int minHeight = -64;
            int maxHeight = 384;

            size = 150;
            float distance = ((float) (height - minHeight) / (maxHeight - minHeight)) * 40;

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, -size, 60.0F + distance, -size).uv(0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, 60.0F + distance, -size).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, 60.0F + distance, size).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(matrix4f, -size, 60.0F + distance, size).uv(0.0F, 1.0F).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
            poseStack.popPose();

            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();

            return true;
        }


    }

    @OnlyIn(Dist.CLIENT)
    public static class MoonOrbitEffect extends LegacyCustomDimensionEffects {
        public MoonOrbitEffect() {
            super(Float.NaN, false, DimensionSpecialEffects.SkyType.NONE, false, false);
        }

        @Override
        public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
            return true;
        }

        @Override
        public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
            return true;
        }

        @Override
        public boolean tickRain(ClientLevel level, int ticks, Camera camera) {
            return true;
        }

        @Override
        public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {

            BufferBuilder bufferbuilder = renderSpaceSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);

            //render sun and moon
            poseStack.pushPose();
            float size = 30.0F;
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(camera.getEntity().getLevel().getTimeOfDay(partialTick) * 360.0F));

            Matrix4f matrix4f = poseStack.last().pose();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SUN_LOCATION);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, -size, 100.0F, -size).uv(0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, 100.0F, -size).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, 100.0F, size).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(matrix4f, -size, 100.0F, size).uv(0.0F, 1.0F).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());

            size = 18.0F;

            RenderSystem.setShaderTexture(0, EARTH_LOCATION);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, -size, -100.0F, size).uv(0.0F, 1.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, -100.0F, size).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, -100.0F, -size).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, -size, -100.0F, -size).uv(0.0F, 0.0F).endVertex();

            BufferUploader.drawWithShader(bufferbuilder.end());
            poseStack.popPose();

            poseStack.pushPose();

            poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
            matrix4f = poseStack.last().pose();
            RenderSystem.setShaderTexture(0, MOON_LOCATION);

            BlockPos pos = camera.getEntity().getOnPos();
            int height = pos.getY();
            int minHeight = -64;
            int maxHeight = 384;

            size = 150;
            float distance = ((float) (height - minHeight) / (maxHeight - minHeight)) * 40;

            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, -size, 60.0F + distance, -size).uv(0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, 60.0F + distance, -size).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, 60.0F + distance, size).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(matrix4f, -size, 60.0F + distance, size).uv(0.0F, 1.0F).endVertex();


            BufferUploader.drawWithShader(bufferbuilder.end());
            poseStack.popPose();

            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();

            return true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class MoonEffect extends LegacyCustomDimensionEffects {
        public MoonEffect() {
            super(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, false);
        }

        @Override
        public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
            return true;
        }

        @Override
        public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
            return true;
        }

        @Override
        public boolean tickRain(ClientLevel level, int ticks, Camera camera) {
            return true;
        }

        @Override
        public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {

            BufferBuilder bufferbuilder = renderSpaceSky(level, ticks, partialTick, poseStack, camera, projectionMatrix, isFoggy, setupFog);

            //render sun and moon
            poseStack.pushPose();
            float size = 30.0F;
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(camera.getEntity().getLevel().getTimeOfDay(partialTick) * 360.0F));

            Matrix4f matrix4f = poseStack.last().pose();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SUN_LOCATION);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, -size, 100.0F, -size).uv(0.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, 100.0F, -size).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, 100.0F, size).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(matrix4f, -size, 100.0F, size).uv(0.0F, 1.0F).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());

            size = 18.0F;

            RenderSystem.setShaderTexture(0, EARTH_LOCATION);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, -size, -100.0F, size).uv(0.0F, 1.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, -100.0F, size).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(matrix4f, size, -100.0F, -size).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(matrix4f, -size, -100.0F, -size).uv(0.0F, 0.0F).endVertex();

            BufferUploader.drawWithShader(bufferbuilder.end());
            poseStack.popPose();

            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();

            return true;
        }
    }

    private static BufferBuilder renderSpaceSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, SPACE_SKY_LOCATION);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();


        for (int i = 0; i < 6; ++i) {
            poseStack.pushPose();
            //make all the face
            if (i == 1) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            }

            if (i == 2) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
            }

            if (i == 4) {
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
            }

            if (i == 5) {
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
            }

            int l = i % 3;
            int i1 = i / 4 % 2;
            float col_begin = (float) (l) / 3.0F;
            float l_begin = (float) (i1) / 2.0F;
            float col_end = (float) (l + 1) / 3.0F;
            float l_end = (float) (i1 + 1) / 2.0F;

            float size = 100.0F;
            float distance = 100.0F;
            Matrix4f matrix4f = poseStack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(matrix4f, -size, -distance, -size).uv(col_end, l_end).color(255, 255, 255, 255).endVertex();
            bufferbuilder.vertex(matrix4f, -size, -distance, size).uv(col_begin, l_end).color(255, 255, 255, 255).endVertex();
            bufferbuilder.vertex(matrix4f, size, -distance, size).uv(col_begin, l_begin).color(255, 255, 255, 255).endVertex();
            bufferbuilder.vertex(matrix4f, size, -distance, -size).uv(col_end, l_begin).color(255, 255, 255, 255).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
            //tesselator.end();
            poseStack.popPose();
        }


        return bufferbuilder;
    }
}
