package com.rae.creatingspace.content.worldgen.debug;

import com.rae.creatingspace.content.worldgen.noise.PhacelleErosionNoise;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.io.File;

import static com.rae.creatingspace.content.worldgen.debug.DensityFunctionVisualizer.render2D;

public class Test {

    public static void main(String[] args) throws Exception {
// 1. Base heightmap
        DensityFunction base = new FixedMountainDF();

        DensityFunction erosion = new PhacelleErosionNoise(
                base,

                // erosion
                10f,  // scale
                0.8f,  // strength
                0.5f,   // gully weight
                1.5f,   // detail

                // rounding
                0.1f,   // ridge rounding
                0.0f,   // crease rounding
                0.1f,   // rounding mult initial
                2.0f,   // rounding mult per octave

                // onset
                0.7f,   // onset initial
                1.25f,  // onset per octave
                2.8f,   // ridge map onset initial
                1.5f,   // ridge map onset per octave

                // slope
                0.7f,   // assumed slope
                1.0f,   // slope mix

                // fractal
                5,      // octaves
                2.0f,   // lacunarity
                0.5f,   // gain

                // noise
                10f,   // cell scale
                0.5f    // normalization
        );

        // 3. Render
        render2D(erosion, 512, 0, new File("erosion.png"));

        System.out.println("Done.");
    }




    private static class FixedMountainDF implements DensityFunction.SimpleFunction {

        @Override
        public double compute(FunctionContext context) {

            float d = Mth.sqrt(context.blockX() * context.blockX() + context.blockZ() * context.blockZ())/30;
            //cos of distance to 0
            return Mth.sin( 1 / (1 + d) * Mth.HALF_PI);
        }

        @Override
        public double minValue() {
            return 0;
        }

        @Override
        public double maxValue() {
            return 1;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            return null;
        }
    }
}
