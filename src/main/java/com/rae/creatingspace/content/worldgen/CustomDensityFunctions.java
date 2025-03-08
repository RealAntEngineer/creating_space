package com.rae.creatingspace.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public class CustomDensityFunctions {

    // TODO maybe do an abstraction for the simplex noise directly ?? -> no I can't, I will need to input the parameters into it
    //  with amplitudes and all of that

    //direct copy of endIsland noise to understand what it does
   public static final class WorleyDensityFunction implements DensityFunction.SimpleFunction {
        WorleyNoise noise;

        public static final MapCodec<WorleyDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.FLOAT.fieldOf("xz_size").forGetter(i -> i.noise.getXZSize()),
                Codec.FLOAT.fieldOf("y_size").forGetter(i -> i.noise.getYSize()))
                .apply(instance, WorleyDensityFunction::new));

        public static final KeyDispatchDataCodec<WorleyDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

        public WorleyDensityFunction(float xz_size,float y_size){
            noise = new WorleyNoise(xz_size,y_size);//scale factor is useless. maybe octaves ?
        }

        @Override
        public double compute(@NotNull FunctionContext context) {
            return noise.getValue(context.blockX(),context.blockY(),context.blockZ());
        }

        @Override
        public double minValue() {
            return -1;
        }

        @Override
        public double maxValue() {
            return 1;
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        public void setSeed(long seed) {
            noise.setSeed(seed);
        }
    }
}