package com.rae.creatingspace.content.worldgen.noise;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class WorleyNoise {
    //TODO Optimise it further
    //TODO make a 2d version of it -> makes no sens to use 3d version for a 2D map
    private static final float K    = 1.0f / 8.0f;    // 0.125f — scale permuted into 8 buckets
    private static final float Ko   = 0.375f;         // center of [-0.375, 0.375] ( = 3 * K)
    private static final float K2   = 1.0f / 32.0f;   // 0.03125f — second-level bucket (for Z)
    private static final float Kz   = 1.0f / 6.0f;    // 0.166666...
    private static final float Kzo  = 0.5f - Kz;      // = 0.333333... — to center Z
    private static final float jitter = 0.8f;
    private final float YMin;
    private final float YMax;

    public float getXZSize() {
        return XZSize;
    }

    public float getYSize() {
        return YSize;
    }


    private final float XZSize;
    private final float YSize;
    private float x0,y0,z0;
    private static final int permutations_bits = 8;
    private static final int permutations_amount = 1 << permutations_bits; // = 256

    private final int[] xp = new int[permutations_amount];

    // Offsets for the 27 neighbor cells in a 3×3×3 cube
    private static final int[][] OFFSETS_3D = new int[27][3];

    static {
        int index = 0;
        for (int xi = -1; xi <= 1; xi++) {
            for (int yi = -1; yi <= 1; yi++) {
                for (int zi = -1; zi <= 1; zi++) {
                    OFFSETS_3D[index][0] = xi;
                    OFFSETS_3D[index][1] = yi;
                    OFFSETS_3D[index][2] = zi;
                    index++;
                }
            }
        }
    }

    //TODO go to 256 rather than 289, now that we use the seed it doesn't make sens anymore
    //TODO have 3 permutations for x, y, z for improved performance ? need to do  profiling and see if it changes anything.
    public WorleyNoise(float XZSize, float YSize, float YMin, float YMax) {
        this.XZSize = XZSize;
        this.YSize = YSize;
        this.YMin = YMin/YSize;
        this.YMax = YMax/YSize;
        setSeed(0L);
    }
    public void setSeed(long seed){
        RandomSource random = new XoroshiroRandomSource(seed);
        this.x0 = random.nextFloat() * permutations_amount;//TODO is this needed ?
        this.y0 = random.nextFloat() * permutations_amount;
        this.z0 = random.nextFloat() * permutations_amount;

        for (int i = 0; i < permutations_amount; i++) {
            this.xp[i] = i;
        }

        for(int l = 0; l < permutations_amount; ++l) {
            int j = random.nextInt(permutations_amount - l);
            int k = this.xp[l];
            this.xp[l] = this.xp[j + l];
            this.xp[j + l] = k;
        }

    }
    private int permute(int x){
        return xp[x&(permutations_amount - 1)];
    }
    public static float frac(float x) {
        return x - Mth.floor(x);
    }

    public float cellular3x3x3(float px, float py, float pz) {
        //here a cell is 1 large (because in getValue we
        int Pix = Mth.floor(px+x0), Piy = Mth.floor(py+y0), Piz = Mth.floor(pz+z0); // Integer part
        float Pfx = frac(px+x0),Pfy = frac(py+y0),Pfz = frac(pz+z0); // Fractional part
        float minDist = Float.MAX_VALUE;
        //if we do it with a nested loop we have a big overhead
        float yMaxOffset = YMax + y0;
        float yMinOffset = YMin + y0;
        for (int[] offset : OFFSETS_3D) {
            int xi = offset[0];
            int yi = offset[1];
            int zi = offset[2];

            int cellX = Pix + xi;
            int cellY = Piy + yi;
            int cellZ = Piz + zi;

            float dx = xi - Pfx;
            float dy = yi - Pfy;
            float dz = zi - Pfz;

            float permuted = permute(permute(permute(cellX) + cellY) + cellZ);

            float fk = permuted * K;
            float jitterX = (fk - Mth.floor(fk) - Ko) * jitter;
            float jitterY = ((Mth.floor(fk) % 8) * K - Ko) * jitter;
            float jitterZ = ((Mth.floor(permuted * K2)) * Kz - Kzo) * jitter;

            float ytest = cellY + jitterY;

            if (ytest  < yMinOffset || ytest > yMaxOffset) continue;

            float distX = dx + jitterX;
            float distY = dy + jitterY;
            float distZ = dz + jitterZ;
            float dist = distX * distX + distY * distY + distZ * distZ;
            minDist = Math.min(minDist, dist);
        }
        return Mth.sqrt(minDist); // Return the actual distance
    }
    //use int, that's lighter
    public double getValue(int x, int y, int z) {
        float F = cellular3x3x3((x) / XZSize,(y) / YSize,(z) / XZSize);//give a result bwn 0 and 1
        return 1 - (F * 2);  // Mapping to range [-1, 1]
    }

    public float getYMin() {
        return this.YMin;
    }
    public float getYMax() {
        return this.YMax;
    }
}

