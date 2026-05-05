package com.rae.creatingspace.content.worldgen.noise;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class WorleyNoise2D {
    private static final float K = 1.0f / 8.0f;    // 0.125f — scale permuted into 8 buckets
    private static final float Ko = 0.375f;         // center of [-0.375, 0.375] ( = 3 * K)
    private static final float jitter = 0.8f;

    private final float cellSize;
    private float x0, z0;
    private static final int permutations_bits = 8;
    private static final int permutations_amount = 1 << permutations_bits; // = 256

    private final int[] xp = new int[permutations_amount];

    // Offsets for the 9 neighbor cells in a 3×3 grid (2D)
    private static final int[][] OFFSETS_2D = new int[9][2];

    static {
        int index = 0;
        for (int xi = -1; xi <= 1; xi++) {
            for (int zi = -1; zi <= 1; zi++) {
                OFFSETS_2D[index][0] = xi;
                OFFSETS_2D[index][1] = zi;
                index++;
            }
        }
    }

    /**
     * Creates a 2D Worley noise generator
     * @param cellSize The size of each Worley cell
     */
    public WorleyNoise2D(float cellSize) {
        this.cellSize = cellSize;
        setSeed(0L);
    }

    public float getCellSize() {
        return cellSize;
    }

    public void setSeed(long seed) {
        RandomSource random = new XoroshiroRandomSource(seed);
        this.x0 = random.nextFloat() * permutations_amount;
        this.z0 = random.nextFloat() * permutations_amount;

        for (int i = 0; i < permutations_amount; i++) {
            this.xp[i] = i;
        }

        // Fisher-Yates shuffle
        for (int l = 0; l < permutations_amount; ++l) {
            int j = random.nextInt(permutations_amount - l);
            int k = this.xp[l];
            this.xp[l] = this.xp[j + l];
            this.xp[j + l] = k;
        }
    }

    private int permute(int x) {
        return xp[x & (permutations_amount - 1)];
    }

    public static float frac(float x) {
        return x - Mth.floor(x);
    }

    /**
     * Calculates 2D cellular noise with weighted circles
     * Returns the distance to the nearest feature point
     */
    public float cellular3x3(float px, float pz) {
        int Pix = Mth.floor(px + x0);
        int Piz = Mth.floor(pz + z0);
        float Pfx = frac(px + x0);
        float Pfz = frac(pz + z0);

        float minDist = Float.MAX_VALUE;

        // Check 3x3 neighborhood (9 cells instead of 27)
        for (int[] offset : OFFSETS_2D) {
            int xi = offset[0];
            int zi = offset[1];

            int cellX = Pix + xi;
            int cellZ = Piz + zi;

            float dx = xi - Pfx;
            float dz = zi - Pfz;

            // Generate pseudo-random jittered position within cell
            float permuted = permute(permute(cellX) + cellZ);

            float fk = permuted * K;//would probably better to have a
            float jitterX = (fk - Mth.floor(fk) - Ko) * jitter;
            float jitterZ = ((Mth.floor(fk) % 8) * K - Ko) * jitter;

            // Calculate weighted distance (can modify weights here)
            float distX = dx + jitterX;
            float distZ = dz + jitterZ;
            float dist = distX * distX + distZ * distZ;

            minDist = Math.min(minDist, dist);
        }

        return Mth.sqrt(minDist);
    }

    /**
     * Gets the Worley noise value at the given 2D coordinates
     * @param x X coordinate
     * @param z Z coordinate
     * @return Value in range [-1, 1]
     */
    public double getValue(int x, int z) {
        float F = cellular3x3(x / cellSize, z / cellSize);
        return 1 - (F * 2);  // Mapping to range [-1, 1]
    }

    /**
     * Gets the raw cellular distance (before mapping)
     * @param x X coordinate
     * @param z Z coordinate
     * @return Distance value (0 to ~1.4)
     */
    public float getRawDistance(int x, int z) {
        return cellular3x3(x / cellSize, z / cellSize);
    }

    /**
     * Gets a value with custom weighting for anisotropic distance
     * @param x X coordinate
     * @param z Z coordinate
     * @param weightX Weight for X axis (default 1.0)
     * @param weightZ Weight for Z axis (default 1.0)
     * @return Value in range [-1, 1]
     */
    public double getWeightedValue(int x, int z, float weightX, float weightZ) {
        float px = x / cellSize;
        float pz = z / cellSize;

        int Pix = Mth.floor(px + x0);
        int Piz = Mth.floor(pz + z0);
        float Pfx = frac(px + x0);
        float Pfz = frac(pz + z0);

        float minDist = Float.MAX_VALUE;

        for (int[] offset : OFFSETS_2D) {
            int xi = offset[0];
            int zi = offset[1];

            int cellX = Pix + xi;
            int cellZ = Piz + zi;

            float dx = xi - Pfx;
            float dz = zi - Pfz;

            float permuted = permute(permute(cellX) + cellZ);

            float fk = permuted * K;
            float jitterX = (fk - Mth.floor(fk) - Ko) * jitter;
            float jitterZ = ((Mth.floor(fk) % 8) * K - Ko) * jitter;

            // Apply weights to create elliptical/stretched cells
            float distX = (dx + jitterX) * weightX;
            float distZ = (dz + jitterZ) * weightZ;
            float dist = distX * distX + distZ * distZ;

            minDist = Math.min(minDist, dist);
        }

        float F = Mth.sqrt(minDist);
        return 1 - (F * 2);
    }
}
