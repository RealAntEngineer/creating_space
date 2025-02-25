package com.rae.creatingspace.content.worldgen;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.phys.Vec3;

public class WorleyNoise {
    private static final double K = 0.142857142857f;
    private static final double Ko = 0.428571428571f;
    private static final double K2 = 0.020408163265306f;
    private static final double Kz = 0.166666666667f;
    private static final double Kzo = 0.416666666667f;
    private static final double jitter = 0.8f;

    public double getXZSize() {
        return XZSize;
    }

    public double getYSize() {
        return YSize;
    }

    public double getScaleFactor() {
        return scaleFactor;
    }

    private final double XZSize;
    private final double YSize;
    private final double scaleFactor;
    private double x0,y0,z0;
    private int[] p = new int[289];
    public WorleyNoise(double XZSize, double YSize, double scaleFactor) {
        this.XZSize = XZSize;
        this.YSize = YSize;
        this.scaleFactor = scaleFactor;
        setSeed(0L);
    }
    public void setSeed(long seed){
        RandomSource random = new XoroshiroRandomSource(seed);
        this.x0 = random.nextDouble() * 289.0D;
        this.y0 = random.nextDouble() * 289.0D;
        this.z0 = random.nextDouble() * 289.0D;

        for(int i = 0; i < 289; this.p[i] = i++) {
        }

        for(int l = 0; l < 289; ++l) {
            int j = random.nextInt(289 - l);
            int k = this.p[l];
            this.p[l] = this.p[j + l];
            this.p[j + l] = k;
        }

    }

    private double permute(double x) {
        return p[(int) (x%289.0d)];
    }

    public static double fract(double x) {
        return x - Math.floor(x);
    }

    public double cellular3x3x3(double px, double py, double pz) {
        double Pix = Math.floor(px+x0), Piy = Math.floor(py+y0), Piz = Math.floor(pz+z0); // Integer part
        double Pfx = fract(px+x0),Pfy = fract(py+y0),Pfz = fract(pz+z0); // Fractional part

        double minDist = Float.MAX_VALUE;

        for (int xi = -1; xi <= 1; xi++) {
            for (int yi = -1; yi <= 1; yi++) {
                for (int zi = -1; zi <= 1; zi++) {
                    // Compute cell coordinates
                    //Vec3 cell = Pi.add(new Vec3(xi, yi, zi));
                    double permuted = permute(permute(permute(Pix + xi)+Piy+yi)+Piz+zi);
                    // Pseudo-random offset inside cell
                    double jitterX = ((permuted*K-Math.floor(permuted*K))-Ko) * jitter;
                    double jitterY = ((Math.floor(permuted*K)%7.0) * K-Ko) * jitter;
                    double jitterZ = ((Math.floor(permuted*K2)) * Kz-Kzo) * jitter;


                    // Compute squared distance
                    double dist = Math.sqrt((jitterX+xi-Pfx)*(jitterX+xi-Pfx)+(jitterY+yi-Pfy)*(jitterY+yi-Pfy)+(jitterZ+zi-Pfz)*(jitterZ+zi-Pfz));

                    // Track minimum distance
                    minDist = Math.min(minDist, dist);
                }
            }
        }
        return Math.sqrt(minDist); // Return the actual distance
    }

    public double getValue(double x, double y, double z) {
        double F = cellular3x3x3((x) / XZSize,(y) / YSize,(z) / XZSize)*scaleFactor;
        return 1- (F * 2);  // Mapping to range [-1, 1]
    }
}

