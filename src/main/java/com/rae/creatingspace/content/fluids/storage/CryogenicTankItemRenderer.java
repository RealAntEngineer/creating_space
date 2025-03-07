package com.rae.creatingspace.content.fluids.storage;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CryogenicTankItemRenderer extends CustomRenderedItemModelRenderer {
    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        renderer.render(model.getOriginalModel(), light);
        if (transformType == ItemDisplayContext.GUI) {
            ms.pushPose();
            ms.translate(-0.25, -0.25, 1.0);
            ms.scale(0.5F, 0.5F, 0.5F);
            ms.mulPose(Axis.YP.rotationDegrees(-34));
            itemRenderer.renderStatic(Items.BUCKET.getDefaultInstance(), ItemDisplayContext.GUI, light, OverlayTexture.NO_OVERLAY, ms, buffer,null, 0);
            ms.popPose();
        }
    }
}
