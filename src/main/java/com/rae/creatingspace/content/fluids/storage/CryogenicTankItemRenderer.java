package com.rae.creatingspace.content.fluids.storage;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CryogenicTankItemRenderer extends CustomRenderedItemModelRenderer {
    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemTransforms.TransformType transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        renderer.render(model.getOriginalModel(), light);
        if (transformType == ItemTransforms.TransformType.GUI) {
            PoseStack localMs = new PoseStack();
            localMs.translate(-0.25, -0.25, 1.0);
            localMs.scale(0.5F, 0.5F, 0.5F);
            TransformStack.cast(localMs).rotateY(-34.0);
            itemRenderer.renderStatic(Items.BUCKET.getDefaultInstance(), ItemTransforms.TransformType.GUI, light, OverlayTexture.NO_OVERLAY, localMs, buffer, 0);
        }
    }
}
