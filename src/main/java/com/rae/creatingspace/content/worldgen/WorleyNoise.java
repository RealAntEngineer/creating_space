package com.rae.creatingspace.content.worldgen;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class WorleyNoise {
    private static final float K = 0.142857142857f;
    private static final float Ko = 0.428571428571f;
    private static final float K2 = 0.020408163265306f;
    private static final float Kz = 0.166666666667f;
    private static final float Kzo = 0.416666666667f;
    private static final float jitter = 0.8f;

    public float getXZSize() {
        return XZSize;
    }

    public float getYSize() {
        return YSize;
    }


    private final float XZSize;
    private final float YSize;
    private float x0,y0,z0;
    private final int[] p = new int[289];//go to 256 rather than 289
    public WorleyNoise(float XZSize, float YSize) {
        this.XZSize = XZSize;
        this.YSize = YSize;
        setSeed(0L);
    }
    public void setSeed(long seed){
        RandomSource random = new XoroshiroRandomSource(seed);
        this.x0 = random.nextFloat() * 289.0f;
        this.y0 = random.nextFloat() * 289.0f;
        this.z0 = random.nextFloat() * 289.0f;

        for(int i = 0; i < 289; this.p[i] = i++){

        }

        for(int l = 0; l < 289; ++l) {
            int j = random.nextInt(289 - l);
            int k = this.p[l];
            this.p[l] = this.p[j + l];
            this.p[j + l] = k;
        }

    }

    private int permute(int x) {
        return p[x%289];
    }

    public static float frac(float x) {
        return x - Mth.floor(x);
    }

    public float cellular3x3x3(float px, float py, float pz) {
        int Pix = Mth.floor(px+x0), Piy = Mth.floor(py+y0), Piz = Mth.floor(pz+z0); // Integer part
        float Pfx = frac(px+x0),Pfy = frac(py+y0),Pfz = frac(pz+z0); // Fractional part
        float minDist = Float.MAX_VALUE;

        for (int xi = -1; xi <= 1; xi++) {
            for (int yi = -1; yi <= 1; yi++) {
                for (int zi = -1; zi <= 1; zi++) {
                    // Compute cell coordinates
                    //Vec3 cell = Pi.add(new Vec3(xi, yi, zi));
                    float permuted = permute(permute(permute(Pix + xi)+Piy+yi)+Piz+zi);
                    // Pseudo-random offset inside cell
                    float jitterX = ((permuted*K-Mth.floor(permuted*K))-Ko) * jitter;
                    float jitterY = ((Mth.floor(permuted*K)%7.0f) * K-Ko) * jitter;
                    float jitterZ = ((Mth.floor(permuted*K2)) * Kz-Kzo) * jitter;


                    // Compute squared distance
                    float dist = Mth.sqrt((jitterX+xi-Pfx)*(jitterX+xi-Pfx)+(jitterY+yi-Pfy)*(jitterY+yi-Pfy)+(jitterZ+zi-Pfz)*(jitterZ+zi-Pfz));

                    // Track minimum distance
                    minDist = Math.min(minDist, dist);
                }
            }
        }
        return Mth.sqrt(minDist); // Return the actual distance
    }
    //use int, that's lighter
    public double getValue(int x, int y, int z) {
        float F = cellular3x3x3((x) / XZSize,(y) / YSize,(z) / XZSize);
        return 1- (F * 2);  // Mapping to range [-1, 1]
    }
}

