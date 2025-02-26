package com.rae.creatingspace.mixin.worldgen;

import com.rae.creatingspace.content.worldgen.ImprovedLavaGen;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseChunk.class)
public class NoiseChunkMixin {
    @Shadow @Final private Aquifer aquifer;

    @Inject(method = "<init>",at = @At("RETURN"))
    private void onCtor(int p_224343_, RandomState p_224344_, int p_224345_, int p_224346_, NoiseSettings p_224347_, DensityFunctions.BeardifierOrMarker p_224348_, NoiseGeneratorSettings p_224349_, Aquifer.FluidPicker p_224350_, Blender p_224351_, CallbackInfo ci){
        if (aquifer instanceof ImprovedLavaGen lg){
            lg.setImprovedGen(((ImprovedLavaGen)(Object)p_224349_).isImprovedGen());
        }
    }
}