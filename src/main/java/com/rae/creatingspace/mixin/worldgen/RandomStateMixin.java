package com.rae.creatingspace.mixin.worldgen;

import com.rae.creatingspace.content.worldgen.noise.INeedWorldSeed;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.*;
import org.lwjgl.system.NonnullDefault;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@NonnullDefault
@Mixin(RandomState.class)
public class RandomStateMixin {
    @Mutable
    @Shadow @Final private NoiseRouter router;

    @Inject(method = "<init>",at = @At(value = "RETURN"))
    private void provideNoise(NoiseGeneratorSettings p_255668_, HolderGetter<?> p_256663_, long seed, CallbackInfo ci){
        class NoiseWiringHelper implements DensityFunction.Visitor {
            private final Map<DensityFunction, DensityFunction> wrapped = new HashMap<>();

            NoiseWiringHelper() {
            }

            public DensityFunction.NoiseHolder visitNoise(DensityFunction.NoiseHolder p_224594_) {
                return p_224594_;
            }

            private DensityFunction wrapNew(DensityFunction densityFunction) {
                if (densityFunction instanceof INeedWorldSeed wd){
                    wd.setSeed(seed);//give the seed to Worley
                }

                return densityFunction;
            }

            public DensityFunction apply(DensityFunction p_224598_) {
                return this.wrapped.computeIfAbsent(p_224598_, this::wrapNew);
            }
        }
        router = router.mapAll(new NoiseWiringHelper());
    }


}
