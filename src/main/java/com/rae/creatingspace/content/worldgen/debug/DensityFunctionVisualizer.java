package com.rae.creatingspace.content.worldgen.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.DensityFunction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DensityFunctionVisualizer {

    public static void render2D(DensityFunction function, BlockPos center, int size, int yLevel, File output) throws Exception {
        File parent = output.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        double[][] values = new double[size][size];

        // First pass: sample + track min/max
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {

                int worldX = x - size / 2;
                int worldZ = z - size / 2;

                double v = function.compute(new DensityFunction.SinglePointContext(center.getX() + worldX, yLevel, center.getZ() + worldZ));
                values[x][z] = v;

                if (v < min) min = v;
                if (v > max) max = v;
            }
        }

        System.out.printf("range : %f, %f", min, max);
        // Avoid divide-by-zero
        double range = max - min;
        if (range == 0) range = 1;

        // Second pass: normalize → grayscale
        for (int x = 0; x < size; x++) {
            for (int z = 0; z < size; z++) {
                double norm = (values[x][z] - min) / range;

                int gray = (int)(norm * 255);
                int rgb = (gray << 16) | (gray << 8) | gray;

                image.setRGB(x, z, rgb);
            }
        }

        ImageIO.write(image, "png", output);
    }
}