package com.rae.creatingspace.api.rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.rae.creatingspace.content.planets.PlanetsPositionsHandler;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import static com.rae.creatingspace.api.rendering.GeometryRendering.renderCube;
import static com.rae.creatingspace.api.rendering.GeometryRendering.renderPolyTex;

public class PlanetsRendering {
    /**
     * @param texture        the texture of the planet
     * @param buffer         the buffer source
     * @param matrixStack    the stack
     * @param packedLight    the light
     * @param planetPos      spherical coord for the planet
     * @param planetRotation rotation of the planet.
     */
    public static void renderPlanet(ResourceLocation texture, MultiBufferSource buffer, PoseStack matrixStack,
                                    int packedLight, float size, PlanetsPositionsHandler.SkyPos planetPos, Quaternion planetRotation, Color skyColor) {
        VertexConsumer planetBuffer =  buffer.getBuffer(RenderTypes.getGlowingSolid(texture));//buffer.getBuffer(RenderType.entityTranslucent(texture));


        Vec3 translation = PlanetsPositionsHandler.SkyPos.toXYZ(planetPos, Vec3.ZERO);
        matrixStack.translate(translation.x(),translation.y(), translation.z());
        float halfSize = size / 2.0F;
        matrixStack.mulPose(planetRotation);

        // Define the eight vertices of the cube
        Vec3 v0 = new Vec3(-halfSize, -halfSize, -halfSize);
        Vec3 v1 = new Vec3(halfSize, -halfSize, -halfSize);
        Vec3 v2 = new Vec3(halfSize, halfSize, -halfSize);
        Vec3 v3 = new Vec3(-halfSize, halfSize, -halfSize);
        Vec3 v4 = new Vec3(-halfSize, -halfSize, halfSize);
        Vec3 v5 = new Vec3(halfSize, -halfSize, halfSize);
        Vec3 v6 = new Vec3(halfSize, halfSize, halfSize);
        Vec3 v7 = new Vec3(-halfSize, halfSize, halfSize);

        // Create the six faces of the cube
        List<Vec3> face1 = List.of(v0, v3, v2, v1); // Front face
        List<Vec3> face2 = List.of(v5, v6, v7, v4); // Back face
        List<Vec3> face3 = List.of(v1, v2, v6, v5); // Right face
        List<Vec3> face4 = List.of(v4, v7, v3, v0); // Left face
        List<Vec3> face5 = List.of(v3, v7, v6, v2); // Top face
        List<Vec3> face6 = List.of(v0, v1, v5, v4); // Bottom face

        List<Vec2> uvs = List.of(new Vec2(0, 0), new Vec2(0, 1),
                new Vec2(1, 1), new Vec2(1, 0));
        // Render each face using renderPoly
        PoseStack.Pose entry = matrixStack.last();
        renderPolyTex(face1, uvs, planetBuffer, entry, packedLight,skyColor);
        renderPolyTex(face2, uvs, planetBuffer, entry, packedLight,skyColor);
        renderPolyTex(face3, uvs, planetBuffer, entry, packedLight,skyColor);
        renderPolyTex(face4, uvs, planetBuffer, entry, packedLight,skyColor);
        renderPolyTex(face5, uvs, planetBuffer, entry, packedLight,skyColor);
        renderPolyTex(face6, uvs, planetBuffer, entry, packedLight,skyColor);
        //undo transformation
        planetRotation.conj();
        matrixStack.mulPose(planetRotation);
        planetRotation.conj();
        matrixStack.translate(-translation.x(),-translation.y(),- translation.z());

    }

    /**
     * to use when no access to the MultiSourceBuffer (DimensionSpecialEffect)
     */
    public static void renderPlanet(ResourceLocation texture, PoseStack matrixStack,
                                    int packedLight, float size, PlanetsPositionsHandler.SkyPos planetPos, Quaternion planetRotation,Color skyColor) {
        renderPlanet(texture, MultiBufferSource.immediate(new BufferBuilder(256)), matrixStack, packedLight, size,planetPos,planetRotation,skyColor);

    }

    public static void renderAtmosphere(MultiBufferSource buffer, PoseStack matrixStack, Color color,
                                        int packedLight, float size, PlanetsPositionsHandler.SkyPos planetPos, Quaternion planetRotation) {
        VertexConsumer vertexBuilder = buffer.getBuffer(CSRenderTypes.getTranslucentAtmo());//RenderTypes.getGlowingTranslucent(AllSpecialTextures.BLANK.getLocation()));
        matrixStack.mulPose(planetRotation);

        Vec3 translation = PlanetsPositionsHandler.SkyPos.toXYZ(planetPos, Vec3.ZERO);
        matrixStack.translate(translation.x(),translation.y(), translation.z());
        renderCube(vertexBuilder, matrixStack, Vec3.ZERO, packedLight, size, color);
        matrixStack.translate(-translation.x(),-translation.y(), -translation.z());
        planetRotation.conj();
        matrixStack.mulPose(planetRotation);
        planetRotation.conj();
    }


}
