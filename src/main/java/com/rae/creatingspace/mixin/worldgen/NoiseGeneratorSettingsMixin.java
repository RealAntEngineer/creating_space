package com.rae.creatingspace.mixin.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.worldgen.ImprovedLavaGen;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin implements ImprovedLavaGen {

    @Mutable
    @Shadow
    @Final
    public static Codec<NoiseGeneratorSettings> DIRECT_CODEC;
    @Unique
    private boolean cS_1_19_2$improvedGen = false;

    @Override
    public void setImprovedGen(boolean improvedGen) {
        this.cS_1_19_2$improvedGen = improvedGen;
    }

    @Override
    public boolean isImprovedGen() {
        return this.cS_1_19_2$improvedGen;
    }

    @Unique
    private static NoiseGeneratorSettings cS_1_19_2$createNoiseRouter(NoiseSettings noiseSettings, BlockState defaultBlock, BlockState defaultFluid,
                                                                      NoiseRouter noiseRouterSurfaceRules,SurfaceRules.RuleSource surfaceRule, List<Climate.ParameterPoint> spawnTarget,
                                                                      int seaLevel, boolean disableMobGeneration, boolean aquifersEnabled, boolean oreVeinsEnabled, boolean useLegacyRandomSource,
            boolean improvedGen) {

        NoiseGeneratorSettings result = new NoiseGeneratorSettings(noiseSettings, defaultBlock, defaultFluid, noiseRouterSurfaceRules,
                surfaceRule, spawnTarget, seaLevel, disableMobGeneration, aquifersEnabled, oreVeinsEnabled, useLegacyRandomSource);

        // Try setting improvedGen safely
        try {
            ((ImprovedLavaGen) (Object) result).setImprovedGen(improvedGen);
        } catch (ClassCastException e) {
            CreatingSpace.LOGGER.error("Failed to cast NoiseRouter to ImprovedLavaGen! Mixin might not be applied correctly.");
            e.printStackTrace();
        }

        return result;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void modifyCodec(CallbackInfo ci) {
        Codec<NoiseGeneratorSettings> modifiedCodec = RecordCodecBuilder.create(instance ->
                instance.group(
                        NoiseSettings.CODEC.fieldOf("noise").forGetter(NoiseGeneratorSettings::noiseSettings),
                        BlockState.CODEC.fieldOf("default_block").forGetter(NoiseGeneratorSettings::defaultBlock),
                        BlockState.CODEC.fieldOf("default_fluid").forGetter(NoiseGeneratorSettings::defaultFluid),
                        NoiseRouter.CODEC.fieldOf("noise_router").forGetter(NoiseGeneratorSettings::noiseRouter),
                        SurfaceRules.RuleSource.CODEC.fieldOf("surface_rule").forGetter(NoiseGeneratorSettings::surfaceRule),
                        Climate.ParameterPoint.CODEC.listOf().fieldOf("spawn_target").forGetter(NoiseGeneratorSettings::spawnTarget),
                        Codec.INT.fieldOf("sea_level").forGetter(NoiseGeneratorSettings::seaLevel),
                        Codec.BOOL.fieldOf("disable_mob_generation").forGetter(NoiseGeneratorSettings::disableMobGeneration),
                        Codec.BOOL.fieldOf("aquifers_enabled").forGetter(NoiseGeneratorSettings::isAquifersEnabled),
                        Codec.BOOL.fieldOf("ore_veins_enabled").forGetter(NoiseGeneratorSettings::oreVeinsEnabled),
                        Codec.BOOL.fieldOf("legacy_random_source").forGetter(NoiseGeneratorSettings::useLegacyRandomSource),
                        Codec.BOOL.optionalFieldOf("improved_lava_gen", false).forGetter(n -> {
                            try {
                                return ((ImprovedLavaGen) (Object) n).isImprovedGen();
                            } catch (ClassCastException e) {
                                CreatingSpace.LOGGER.error("Failed to cast NoiseRouter to ImprovedLavaGen! Mixin might not be applied correctly.");
                                e.printStackTrace();
                                return false; // Default to false if cast fails
                            }
                        })
                ).apply(instance, NoiseGeneratorSettingsMixin::cS_1_19_2$createNoiseRouter)
        );
        DIRECT_CODEC = modifiedCodec;
    }
}