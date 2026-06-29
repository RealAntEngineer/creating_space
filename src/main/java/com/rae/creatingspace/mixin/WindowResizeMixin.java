package com.rae.creatingspace.mixin;

import com.rae.creatingspace.content.life_support.spacesuit.RemainingO2Overlay;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Window;

import net.createmod.catnip.gui.UIRenderHelper;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class WindowResizeMixin {
    @Shadow
    @Final
    private Window window;

    @Inject(method = "resizeDisplay", at = @At("TAIL"))
    private void catnip$updateWindowSize(CallbackInfo ci) {
        try {
            RemainingO2Overlay.reload();
        } catch (Exception ignore){

        }
    }
}
