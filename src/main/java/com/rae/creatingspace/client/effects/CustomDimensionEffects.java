package com.rae.creatingspace.client.effects;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
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

public abstract class CustomDimensionEffects extends DimensionSpecialEffects {
    private static final ResourceLocation SPACE_SKY_LOCATION = new ResourceLocation("creatingspace", "textures/environment/space_sky.png");
    private static final ResourceLocation EARTH_LOCATION = new ResourceLocation("creatingspace", "textures/environment/earth.png");
    private static final ResourceLocation MOON_LOCATION = new ResourceLocation("creatingspace", "textures/environment/moon.png");
    private static final ResourceLocation MARS_LOCATION = new ResourceLocation("creatingspace", "textures/environment/mars.png");
    private static final ResourceLocation SATURN_LOCATION = new ResourceLocation("creatingspace", "textures/environment/saturn.png");
    private static final ResourceLocation SUN_LOCATION = new ResourceLocation("textures/environment/sun.png");
    private static final ResourceLocation MOON_PHASES_LOCATION = new ResourceLocation("textures/environment/moon_phases.png");

    public CustomDimensionEffects(float cloudLevel, boolean hasGround, DimensionSpecialEffects.SkyType skyType, boolean forceBrightLightmap, boolean constantAmbientLight) {
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

    private static BufferBuilder renderSpaceSky(PoseStack poseStack) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, SPACE_SKY_LOCATION);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();


        for(int i = 0; i < 6; ++i) {
            poseStack.pushPose();
            //make all the face
            if (i == 1) {
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            }

            if (i == 2) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            }

            if (i == 3) {
                poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            }

            if (i == 4) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }

            if (i == 5) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }

            int l = i % 3;
            int i1 = i / 4 % 2;
            float col_begin = (float)(l) / 3.0F;
            float l_begin = (float)(i1) / 2.0F;
            float col_end = (float)(l + 1) / 3.0F;
            float l_end = (float)(i1 + 1) / 2.0F;

            float size = 100.0F;
            float distance = 100.0F;
            Matrix4f matrix4f = poseStack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(matrix4f, -size, -distance, -size).uv(col_end, l_end).color(255,255,255,255).endVertex();
            bufferbuilder.vertex(matrix4f, -size, -distance, size).uv(col_begin, l_end).color(255,255,255,255).endVertex();
            bufferbuilder.vertex(matrix4f, size, -distance, size).uv(col_begin, l_begin).color(255,255,255,255).endVertex();
            bufferbuilder.vertex(matrix4f, size, -distance, -size).uv(col_end, l_begin).color(255,255,255,255).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
            //tesselator.end();
            poseStack.popPose();
        }


        return bufferbuilder;
    }

    @OnlyIn(Dist.CLIENT)
    public static class MoonEffect extends GenericCelestialOrbitEffect {
        public MoonEffect() {
            super();
        }

        @Override
        protected void renderAdditionalBody(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, BufferBuilder bufferbuilder, Camera camera, Matrix4f projectionMatrix) {
            super.renderAdditionalBody(level, ticks, partialTick, poseStack, bufferbuilder, camera, projectionMatrix);
            renderAstralBody(poseStack, bufferbuilder, EARTH_LOCATION, camera.getEntity().level().getTimeOfDay(partialTick) * 360.0F + 180F, 20, 100F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class MarsEffect extends GenericCelestialOrbitEffect {
        public MarsEffect() {
            super();
        }

        @Override
        protected void renderAdditionalBody(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, BufferBuilder bufferbuilder, Camera camera, Matrix4f projectionMatrix) {
            super.renderAdditionalBody(level, ticks, partialTick, poseStack, bufferbuilder, camera, projectionMatrix);
            //renderAstralBody(poseStack, bufferbuilder, EARTH_LOCATION, camera.getEntity().getLevel().getTimeOfDay(partialTick) * 360.0F + 180F, 20, 100F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class MoonOrbitEffect extends GenericCelestialOrbitEffect {
        public MoonOrbitEffect() {
            super();
        }

        @Override
        protected void renderAdditionalBody(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, BufferBuilder bufferbuilder, Camera camera, Matrix4f projectionMatrix) {
            super.renderAdditionalBody(level, ticks, partialTick, poseStack, bufferbuilder, camera, projectionMatrix);
            renderAstralBody(poseStack, bufferbuilder, EARTH_LOCATION, camera.getEntity().level().getTimeOfDay(partialTick) * 360.0F + 180F, 18.0F, 100F);
            BlockPos pos = camera.getEntity().getOnPos();
            int height = pos.getY();
            int minHeight = -64;
            int maxHeight = 384;
            renderAstralBody(poseStack, bufferbuilder, MOON_LOCATION, 180F, 150.0F, 60.0F + ((float) (height - minHeight) / (maxHeight - minHeight)) * 40);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class MarsOrbitEffects extends GenericCelestialOrbitEffect {
        public MarsOrbitEffects() {
            super();
        }

        @Override
        protected void renderAdditionalBody(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, BufferBuilder bufferbuilder, Camera camera, Matrix4f projectionMatrix) {
            super.renderAdditionalBody(level, ticks, partialTick, poseStack, bufferbuilder, camera, projectionMatrix);
            BlockPos pos = camera.getEntity().getOnPos();
            int height = pos.getY();
            int minHeight = -64;
            int maxHeight = 384;
            renderAstralBody(poseStack, bufferbuilder, MARS_LOCATION, 180F, 150.0F, ((float) (height - minHeight) / (maxHeight - minHeight)) * 40);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class EarthOrbitEffects extends GenericCelestialOrbitEffect {
        public EarthOrbitEffects() {
            super();
        }

        @Override
        protected void renderAdditionalBody(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, BufferBuilder bufferbuilder, Camera camera, Matrix4f projectionMatrix) {
            super.renderAdditionalBody(level, ticks, partialTick, poseStack, bufferbuilder, camera, projectionMatrix);
            int k = camera.getEntity().level().getMoonPhase();
            int l = k % 4;
            int i1 = k / 4 % 2;
            float f13 = (float) (l) / 4.0F;
            float f14 = (float) (i1) / 2.0F;
            float f15 = (float) (l + 1) / 4.0F;
            float f16 = (float) (i1 + 1) / 2.0F;
            renderAstralBody(poseStack, bufferbuilder, MOON_PHASES_LOCATION, camera.getEntity().level().getTimeOfDay(partialTick) * 360.0F + 180F, 20, 100F, f15, f13, f16, f14);
            BlockPos pos = camera.getEntity().getOnPos();
            int height = pos.getY();
            int minHeight = -64;
            int maxHeight = 384;
            renderAstralBody(poseStack, bufferbuilder, EARTH_LOCATION, 180F, 150.0F, 60F + ((float) (height - minHeight) / (maxHeight - minHeight)) * 40);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class GenericCelestialOrbitEffect extends CustomDimensionEffects {
        private boolean renderSun = true;

        public GenericCelestialOrbitEffect() {
            super(Float.NaN, false, SkyType.NONE, false, false);
        }

        public void setRenderSun(boolean renderSun) {
            this.renderSun = renderSun;
        }

        public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
            return true;
        }

        public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
            return true;
        }

        public boolean tickRain(ClientLevel level, int ticks, Camera camera) {
            return true;
        }

        public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
            BufferBuilder bufferbuilder = renderSpaceSky(poseStack);
            renderAdditionalBody(level, ticks, partialTick, poseStack, bufferbuilder, camera, projectionMatrix);


            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
            return true;
        }

        protected void renderAdditionalBody(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, BufferBuilder bufferbuilder, Camera camera, Matrix4f projectionMatrix) {
            if (this.renderSun) {
                renderAstralBody(poseStack, bufferbuilder, SUN_LOCATION, camera.getEntity().level().getTimeOfDay(partialTick) * 360.0F, 30.0F, 100.0F);
                //replace camera with an angle ?
            }
        }

        protected void renderAstralBody(PoseStack poseStack, BufferBuilder bufferbuilder, ResourceLocation bodyTexture, float rotationAngle, float bodySize, float bodyDistance) {
            renderAstralBody(poseStack, bufferbuilder, bodyTexture, rotationAngle, bodySize, bodyDistance, 0, 1, 1, 0);
        }

        protected void renderAstralBody(PoseStack poseStack, BufferBuilder bufferbuilder, ResourceLocation bodyTexture, float rotationAngle, float bodySize, float bodyDistance, float f15, float f13, float f16, float f14) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(rotationAngle));
            Matrix4f matrix4f = poseStack.last().pose();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, bodyTexture);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, -bodySize, bodyDistance, -bodySize).uv(f15, f14).endVertex();
            bufferbuilder.vertex(matrix4f, bodySize, bodyDistance, -bodySize).uv(f13, f14).endVertex();
            bufferbuilder.vertex(matrix4f, bodySize, bodyDistance, bodySize).uv(f13, f16).endVertex();
            bufferbuilder.vertex(matrix4f, -bodySize, bodyDistance, bodySize).uv(f15, f16).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
            poseStack.popPose();
        }
    }
}
