package com.rae.creatingspace.mixin.worldgen;

import com.rae.creatingspace.content.worldgen.CustomDensityFunctions;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(RandomState.class)
public class RandomStateMixin {
    @Mutable
    @Shadow @Final private NoiseRouter router;

    @Inject(method = "<init>",at = @At(value = "RETURN"))
    private void provideNoise(NoiseGeneratorSettings p_224556_, Registry p_224557_, long seed, CallbackInfo ci){
        class NoiseWiringHelper implements DensityFunction.Visitor {
            private final Map<DensityFunction, DensityFunction> wrapped = new HashMap<>();

            NoiseWiringHelper() {
            }

            public DensityFunction.@NotNull NoiseHolder visitNoise(DensityFunction.@NotNull NoiseHolder p_224594_) {
                return p_224594_;
            }

            private DensityFunction wrapNew(DensityFunction densityFunction) {
                if (densityFunction instanceof CustomDensityFunctions.WorleyDensityFunction wd){
                    wd.setSeed(seed);//give the seed to Worley
                }

                return densityFunction;
            }

            public @NotNull DensityFunction apply(@NotNull DensityFunction p_224598_) {
                return (DensityFunction)this.wrapped.computeIfAbsent(p_224598_, this::wrapNew);
            }
        }
        router = router.mapAll(new NoiseWiringHelper());
    }


}
