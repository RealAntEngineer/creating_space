package com.rae.creatingspace.content.planets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.rae.creatingspace.api.planets.OrbitParameter;
import com.rae.creatingspace.configs.CSConfigs;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.NonnullDefault;

import java.util.List;

import static com.rae.creatingspace.api.rendering.GeometryRendering.renderPolyTex;

@NonnullDefault
public class SmartPlanetsEffect extends DimensionSpecialEffects {
    public SmartPlanetsEffect(float cloudLevel, boolean hasGround, DimensionSpecialEffects.SkyType skyType, boolean forceBrightLightmap, boolean constantAmbientLight) {
        super(cloudLevel, hasGround, skyType, forceBrightLightmap, constantAmbientLight);
    }
    private static final int dayLength = 24000;

    public SmartPlanetsEffect() {
        super(Float.NaN, false, SkyType.NONE, false, false);
    }
    private static final ResourceLocation SPACE_SKY_LOCATION = new ResourceLocation("creatingspace", "textures/environment/space_sky.png");

    @Override
    public @NotNull Vec3 getBrightnessDependentFogColor(@NotNull Vec3 vec, float brightness) {
        return vec;//.multiply(brightness * 0.9f, brightness *0.9f, brightness*0.9f);
    }

    @Override
    public boolean isFoggyAt(int p_108874_, int p_108875_) {
        return false;
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

    //TODO implement alpha channel for the fog
    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        float[] fogColor = RenderSystem.getShaderFogColor();
        float aspectRatio = (float) Minecraft.getInstance().getWindow().getWidth() /
                (float) Minecraft.getInstance().getWindow().getHeight();

        // Retrieve the dynamic FOV
        Color color = new Color(level.getBiomeManager().getBiome(camera.getBlockPosition()).value().getSkyColor());

        RenderSystem.setShaderFogStart(-1);
        RenderSystem.setShaderFogEnd(0);
        RenderSystem.setShaderFogColor(fogColor[0], fogColor[1], fogColor[2], color.getAlphaAsFloat());

        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        ResourceLocation location = level.dimension().location();
        OrbitParameter orbitParameter = PlanetsPositionsHandler.getOrbitParam(location);
        float time = (level.getDayTime() + partialTick) / dayLength;

        // Fix projection matrix handling for 1.20.1
        Matrix4f customProjection = new Matrix4f().perspective(
                (float) Math.toRadians(FovHandler.getCurrentFov()), // Convert FOV to radians
                aspectRatio,
                0.0001f, // Near plane
                1000.0f   // Far plane
        );
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(customProjection,VertexSorting.DISTANCE_TO_ORIGIN);

        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new BufferBuilder(256));

        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

        float timeOfDay = level.getTimeOfDay(time * dayLength);

        renderSpaceSky(poseStack, new Quaternionf().rotationAxis((float) (-2 * time / orbitParameter.rotT() * Math.PI),orbitParameter.rotationAxis().toVector3f()), bufferSource);

        PlanetsPositionsHandler.renderForAll(time, poseStack, bufferSource, location, false, Color.WHITE);
        poseStack.popPose();

        bufferSource.endBatch();
        RenderSystem.restoreProjectionMatrix();

        if (CSConfigs.CLIENT.render_fog.get()) {
            FogRenderer.levelFogColor();
            setupFog.run();
        }

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_LEQUAL); // Updated for 1.20.1
        RenderSystem.disableBlend();

        return true;
    }
   //doesn't work right
    private static void renderSpaceSky(PoseStack poseStack, Quaternionf planetRotation, MultiBufferSource buffer) {
        // Prepare the vertex consumer for rendering
        VertexConsumer planetBuffer = buffer.getBuffer(RenderTypes.getGlowingSolid(SPACE_SKY_LOCATION));

        // Base size and distance values
        float size = 200.0F;
        float distance = 200.0F;

        // Color to be used for rendering
        Color color = Color.WHITE;

        poseStack.mulPose(planetRotation);

        for (int i = 0; i < 6; ++i) {
            // Apply rotation for each face
            switch (i) {
                case 1 -> poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                case 2 -> poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                case 3 -> poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
                case 4 -> poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                case 5 -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }

            // UV mapping for the current face
            int l = i % 3;      // Column index in the texture
            int i1 = i / 3;     // Row index in the texture
            float colBegin = (float) l / 3.0F;         // Start of the column
            float rowBegin = (float) i1 / 2.0F;        // Start of the row
            float colEnd = (float) (l + 1) / 3.0F;     // End of the column
            float rowEnd = (float) (i1 + 1) / 2.0F;    // End of the row

            List<Vec2> uvVector = List.of(
                    new Vec2(colEnd, rowEnd),    // Top-right
                    new Vec2(colBegin, rowEnd), // Top-left
                    new Vec2(colBegin, rowBegin), // Bottom-left
                    new Vec2(colEnd, rowBegin)  // Bottom-right
            );

            // Define the vertices of the current face
            List<Vec3> pos = List.of(
                    new Vec3(-size, -distance, -size), // Bottom-left
                    new Vec3(-size, -distance, size),  // Top-left
                    new Vec3(size, -distance, size),   // Top-right
                    new Vec3(size, -distance, -size)   // Bottom-right
            );

            // Render the face
            renderPolyTex(pos, uvVector, planetBuffer, poseStack.last(), LightTexture.FULL_BRIGHT, color);

            // Revert rotation for the next face
            switch (i) {
                case 1 -> poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
                case 2 -> poseStack.mulPose(Axis.XN.rotationDegrees(-90.0F));
                case 3 -> poseStack.mulPose(Axis.XN.rotationDegrees(180.0F));
                case 4 -> poseStack.mulPose(Axis.ZN.rotationDegrees(90.0F));
                case 5 -> poseStack.mulPose(Axis.ZN.rotationDegrees(-90.0F));
            }
        }

        planetRotation.conjugate();
        poseStack.mulPose(planetRotation);
        planetRotation.conjugate();
    }


    private static void renderColoredSky(PoseStack poseStack, Color skyColor) {
        // Bind no texture
        RenderSystem.enableBlend();//needed because the call to other shaders disables it
        RenderSystem.defaultBlendFunc();

        // Get the Tesselator and BufferBuilder
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();

        int slices = 16; // Number of longitude segments
        float radius = 100.0F;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);

        poseStack.pushPose();
        Matrix4f matrix4f = poseStack.last().pose();


        poseStack.mulPose(Axis.XP.rotationDegrees(-180));
        // Render the top half of the sphere (northern hemisphere)
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, 0.0F, -radius, 0.0F) // Top pole
                .color(skyColor.getRed(), skyColor.getGreen(), skyColor.getBlue(), skyColor.getAlpha())
                .endVertex();

        for (int i = 0; i <= slices; ++i) {
            float theta = (float) (i * 2 * Math.PI / slices);
            float x = Mth.sin(theta) * radius;
            float z = Mth.cos(theta) * radius;
            bufferBuilder.vertex(matrix4f, x, 0.0F, z) // Edge of equator
                    .color(skyColor.getRed(), skyColor.getGreen(), skyColor.getBlue(), skyColor.getAlpha())
                    .endVertex();
        }

        BufferUploader.drawWithShader(bufferBuilder.end());



        // Render the bottom half of the sphere (southern hemisphere)
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, 0.0F, -radius, 0.0F) // Bottom pole
                .color(skyColor.getRed(), skyColor.getGreen(), skyColor.getBlue(), skyColor.getAlpha())
                .endVertex();

        for (int i = 0; i <= slices; ++i) {
            float theta = (float) (i * 2 * Math.PI / slices);
            float x = Mth.sin(theta) * radius;
            float z = Mth.cos(theta) * radius;
            bufferBuilder.vertex(matrix4f, x, 0.0F, z) // Edge of equator
                    .color(skyColor.getRed(), skyColor.getGreen(), skyColor.getBlue(), skyColor.getAlpha())
                    .endVertex();
        }

        BufferUploader.drawWithShader(bufferBuilder.end());

        poseStack.popPose();


    }

}
