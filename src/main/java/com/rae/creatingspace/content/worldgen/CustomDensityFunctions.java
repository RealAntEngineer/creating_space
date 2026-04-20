package com.rae.creatingspace.content.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rae.creatingspace.content.worldgen.noise.INeedWorldSeed;
import com.rae.creatingspace.content.worldgen.noise.PhacelleErosionNoise;
import com.rae.creatingspace.content.worldgen.noise.WorleyNoise;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
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
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(
                    new FolderDF(input.mapAll(visitor), a, b, c));
        }

        @Override
        public double compute(FunctionContext context) {
            float X = (float) input.compute(context);
            return c * (Mth.abs(Mth.abs(X) - a) - b);
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
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
        public double compute(FunctionContext context) {
            double X = input.compute(context);
            // 16*X**4 - 32 * X**3 + 16 * X**2 not a sin but close enough
            double interpolator = X * X * (16 * X * (X -2) + 16);
            return argument1.compute(context) * interpolator + argument2.compute(context) * (1 - interpolator);
            //always equal to -1. Why ? -> because argument2 = -1 and interpolator = 0 because X = 1
        }

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(
                    new LinearInterpolationDF(
                            input.mapAll(visitor),
                            argument1.mapAll(visitor),
                            argument2.mapAll(visitor)));
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
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
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
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(
                    new LinearInterpolationDF(
                            input.mapAll(visitor),
                    argument1.mapAll(visitor),
                    argument2.mapAll(visitor)));
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
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }
    }

    // TODO maybe do an abstraction for the simplex noise directly ?? -> no I can't, I will need to input the parameters into it
    //  with amplitudes and all of that
    public static final class WorleyDensityFunction implements DensityFunction.SimpleFunction, INeedWorldSeed {
        WorleyNoise noise;

        public static final MapCodec<WorleyDensityFunction> DATA_CODEC = RecordCodecBuilder.mapCodec((instance) ->
                instance.group(
                        Codec.FLOAT.fieldOf("xz_size").forGetter(i -> i.noise.getXZSize()),
                        Codec.FLOAT.fieldOf("y_size").forGetter(i -> i.noise.getYSize()),
                        Codec.FLOAT.optionalFieldOf("y_min", -Float.MAX_VALUE).forGetter(i -> i.noise.getYMin()),
                        Codec.FLOAT.optionalFieldOf("y_max",Float.MAX_VALUE).forGetter(i -> i.noise.getYMax()))
                .apply(instance, WorleyDensityFunction::new));

        public static final KeyDispatchDataCodec<WorleyDensityFunction> CODEC = KeyDispatchDataCodec.of(DATA_CODEC);

        public WorleyDensityFunction(float xz_size,float y_size, float y_min, float y_max){
            noise = new WorleyNoise(xz_size,y_size, y_min, y_max);//scale factor is useless. maybe octaves ?
        }

        @Override
        public double compute(FunctionContext context) {
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
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return CODEC;
        }

        @Override
        public void setSeed(long seed) {
            noise.setSeed(seed);
        }
    }
}