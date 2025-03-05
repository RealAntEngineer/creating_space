package com.rae.creatingspace.mixin.worldgen;

import com.rae.creatingspace.init.TagsInit;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SurfaceSystem.class)
public class SurfaceSystemMixin {
    //the goal of this mixin is to allow pocket of water to be affected by the stone depth check normally
    //i2 represent the stone depth for the floor mod, it's not reset when there is water
    //+ tag because why not


    boolean face;
    @Unique
    private BlockState cS_1_20_1$blockstate;
    @Unique
    private int cS_1_20_1$i2 = 0;
    @Unique
    Holder<Biome> cS_1_20_1$holder;
    @ModifyVariable(method = "buildSurface",at = @At(value = "LOAD",ordinal = 0),name ="blockstate")
    public BlockState registerBS(BlockState value){
        cS_1_20_1$blockstate = value;
        return value;
    }
    @ModifyVariable(method = "buildSurface",at = @At(value = "LOAD",ordinal = 0),name ="holder")
    public  Holder<Biome> registerBiome( Holder<Biome> value){
        cS_1_20_1$holder = value;
        return value;
    }
    @ModifyVariable(method = "buildSurface",at = @At(value = "LOAD"),name ="i2")
    public int replaceI2(int value){
        return cS_1_20_1$i2;
    }
    @Inject(method = "buildSurface",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/SurfaceRules$Context;updateXZ(II)V"))
    public void resetI2(RandomState randomState, BiomeManager biomeManager, Registry<Biome> biomeRegistry, boolean legacyNoise,
                        WorldGenerationContext context, ChunkAccess chunkAccess, NoiseChunk noiseChunk, SurfaceRules.RuleSource ruleSource, CallbackInfo ci){
        cS_1_20_1$i2 = 0;
    }
    @Inject(method = "buildSurface", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z"))
    public void deleteWaterInStoneDepth(RandomState randomState, BiomeManager biomeManager, Registry<Biome> biomeRegistry, boolean legacyNoise,
                                        WorldGenerationContext context, ChunkAccess chunkAccess, NoiseChunk noiseChunk, SurfaceRules.RuleSource ruleSource, CallbackInfo ci){
        if (cS_1_20_1$blockstate.isAir() || (!cS_1_20_1$blockstate.getFluidState().isEmpty() && TagsInit.CustomBiomeTags.SPECIAL_WATER_POCKET_HANDLING.matches(cS_1_20_1$holder))){
            cS_1_20_1$i2 = 0;
        }
        else {
            ++cS_1_20_1$i2;
        }
    }
}
