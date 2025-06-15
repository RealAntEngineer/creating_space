package com.rae.creatingspace.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;

public class CustomDensityFunctions {
    public record FolderDF(DensityFunction input, float a, float b, float c, double minValue, double maxValue) implements DensityFunction.SimpleFunction {
        //todo -> rename variable and analyse what it does
        public static final MapCodec<FolderDF> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) ->
                instance.group(
                                DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(FolderDF::input),
                                Codec.FLOAT.fieldOf("a").forGetter(FolderDF::a),
                                Codec.FLOAT.fieldOf("b").forGetter(FolderDF::b),
                                Codec.FLOAT.fieldOf("c").forGetter(FolderDF::c)

                        )
                        .apply(instance, FolderDF::new));

        public static final KeyDispatchDataCodec<FolderDF> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

        public FolderDF(DensityFunction input, float a, float b, float c) {
            this(input, a, b, c,
                    Math.min((Math.max(input.minValue(),input.maxValue()) - a - b) * c,-b*c),
                    Math.max((Math.max(input.minValue(),input.maxValue()) - a - b) * c,-b*c));
        }


        @Override
        public double compute(FunctionContext context) {
            float X = (float) input.compute(context);
            return c * (Mth.abs(Mth.abs(X) - a) - b);
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    public record SinInterpolationDF(DensityFunction input, DensityFunction argument1, DensityFunction argument2) implements DensityFunction.SimpleFunction {

        public static final MapCodec<SinInterpolationDF> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) ->
                instance.group(
                                DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(SinInterpolationDF::input),
                                DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument1").forGetter(SinInterpolationDF::argument1),
                                DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument2").forGetter(SinInterpolationDF::argument2)

                        )
                        .apply(instance, SinInterpolationDF::new));

        public static final KeyDispatchDataCodec<SinInterpolationDF> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

        @Override
        public double compute(@NotNull FunctionContext context) {
            double X = input.compute(context);
            // 16*X**4 - 32 * X**3 + 16 * X**2
            double interpolator = X * X * (16 * X * (X -2) + 16);
            if (interpolator!=0)
                System.out.println("X :"+X+" first function : "+argument1.compute(context)+ " second function : "+ argument2.compute(context) + " interpolator : "+ interpolator + " interpolated : "+ (argument1.compute(context) * interpolator + argument2.compute(context) * (1 - interpolator)));
            return argument1.compute(context) * interpolator + argument2.compute(context) * (1 - interpolator);
            //always equal to -1. Why ? -> because argument2 = -1 and interpolator = 0 because X = 1
        }

        @Override
        public double minValue() {
            return argument1.minValue() + argument2.minValue();
        }

        @Override
        public double maxValue() {
            return argument1.maxValue() + argument2.maxValue();
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    public record LinearInterpolationDF(DensityFunction input, DensityFunction argument1, DensityFunction argument2) implements DensityFunction.SimpleFunction {

        public static final MapCodec<LinearInterpolationDF> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) ->
                instance.group(
                        DensityFunction.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(LinearInterpolationDF::input),
                        DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument1").forGetter(LinearInterpolationDF::argument1),
                        DensityFunction.HOLDER_HELPER_CODEC.fieldOf("argument2").forGetter(LinearInterpolationDF::argument2)

                )
                .apply(instance, LinearInterpolationDF::new));

        public static final KeyDispatchDataCodec<LinearInterpolationDF> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

        @Override
        public double compute(FunctionContext context) {
            double X = input.compute(context);
            return argument1.compute(context) * X + argument2.compute(context) * (1 - X);
        }

        @Override
        public double minValue() {
            return argument1.minValue() + argument2.minValue();
        }

        @Override
        public double maxValue() {
            return argument1.maxValue() + argument2.maxValue();
        }

        @Override
        public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    // TODO maybe do an abstraction for the simplex noise directly ?? -> no I can't, I will need to input the parameters into it
    //  with amplitudes and all of that
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