package com.rae.creatingspace.mixin.worldgen;

import com.rae.creatingspace.content.worldgen.ImprovedLavaGen;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.NonnullDefault;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@NonnullDefault
@Mixin(NoiseChunk.class)
public class NoiseChunkMixin {
    @Shadow @Final private Aquifer aquifer;


    @Inject(method = "<init>",at = @At("RETURN"))
    private void onCtor(int p_224343_, RandomState p_224344_, int p_224345_, int p_224346_, NoiseSettings p_224347_, DensityFunctions.BeardifierOrMarker p_224348_, NoiseGeneratorSettings p_224349_, Aquifer.FluidPicker p_224350_, Blender p_224351_, CallbackInfo ci){
        if (aquifer instanceof ImprovedLavaGen lg){
            lg.setImprovedGen(((ImprovedLavaGen)(Object)p_224349_).isImprovedGen());
        }
    }



//    @Final
//    @Shadow
//    int cellCountY;
//
//    @Final
//    @Shadow
//    int cellWidth;
//
//    @Final
//    @Shadow
//    int cellHeight;
//
//    @Shadow
//    boolean interpolating;
//
//    @Shadow
//    int inCellX, inCellY, inCellZ;
//
//    public class HermiteCubicInterpolator extends NoiseChunk.NoiseInterpolator {
//
//        @Shadow
//        double[][] slice0;
//
//        @Shadow
//        double[][] slice1;
//        // 8 corner values from slices
//        private double c000, c001, c010, c011;
//        private double c100, c101, c110, c111;
//
//        // 8 center/midpoint values (computed on demand)
//        private double m000, m001, m010, m011;  // Centers of edges and faces
//        private double m100, m101, m110, m111;
//
//        // Derivatives computed from finite differences
//        private double dx000, dx001, dx010, dx011, dx100, dx101, dx110, dx111;
//        private double dy000, dy001, dy010, dy011, dy100, dy101, dy110, dy111;
//        private double dz000, dz001, dz010, dz011, dz100, dz101, dz110, dz111;
//
//        private double value;
//
//        HermiteCubicInterpolator(DensityFunction filler) {
//            ((NoiseChunk)(Object)NoiseChunkMixin.this).super(filler);
//
//        }
//
//
//        void selectCellYZ(int cellY, int cellZ) {
//            // Load 8 corner values from the two slices
//            c000 = this.slice0[cellZ][cellY];
//            c001 = this.slice0[cellZ + 1][cellY];
//            c010 = this.slice0[cellZ][cellY + 1];
//            c011 = this.slice0[cellZ + 1][cellY + 1];
//
//            c100 = this.slice1[cellZ][cellY];
//            c101 = this.slice1[cellZ + 1][cellY + 1];
//            c110 = this.slice1[cellZ][cellY + 1];
//            c111 = this.slice1[cellZ + 1][cellY + 1];
//
//            // Compute 8 center/midpoint values by sampling the noise function
//            // These are at half-cell offsets
//            computeCenterValues(cellY, cellZ);
//
//            // Estimate derivatives using finite differences with center values
//            computeDerivatives();
//        }
//
//        private void computeCenterValues(int cellY, int cellZ) {
//            // Get cell dimensions
//            int cellWidth = NoiseChunkMixin.this.cellWidth;
//            int cellHeight = NoiseChunkMixin.this.cellHeight;
//
//            // Base positions for this cell (you'll need to track these in NoiseChunk)
//            // For now, assuming we can compute world positions from cell indices
//            int baseX = cellZ * cellWidth;  // Adjust based on actual NoiseChunk structure
//            int baseY = cellY * cellHeight;
//            int baseZ = 0;  // Will need actual Z coordinate from NoiseChunk
//
//            // Sample at center of sub-cells (half-steps)
//            int halfWidth = cellWidth / 2;
//            int halfHeight = cellHeight / 2;
//
//            // Create context for sampling (pseudo-code, adjust to actual API)
//            // These 8 points form the corners of a smaller cube at the center
//            m000 = sampleAt(baseX + halfWidth, baseY, baseZ);
//            m001 = sampleAt(baseX + halfWidth, baseY, baseZ + halfWidth);
//            m010 = sampleAt(baseX + halfWidth, baseY + halfHeight, baseZ);
//            m011 = sampleAt(baseX + halfWidth, baseY + halfHeight, baseZ + halfWidth);
//
//            m100 = sampleAt(baseX + cellWidth + halfWidth, baseY, baseZ);
//            m101 = sampleAt(baseX + cellWidth + halfWidth, baseY, baseZ + halfWidth);
//            m110 = sampleAt(baseX + cellWidth + halfWidth, baseY + halfHeight, baseZ);
//            m111 = sampleAt(baseX + cellWidth + halfWidth, baseY + halfHeight, baseZ + halfWidth);
//        }
//
//        private double sampleAt(int x, int y, int z) {
//            // Create appropriate context and sample the noiseFiller
//            // This is where we actually call the Perlin noise at the center positions
//            // Pseudo-code - adapt to actual NoiseChunk API:
//            // return noiseFiller.compute(new Context(x, y, z));
//            return 0.0; // Placeholder
//        }
//
//        private void computeDerivatives() {
//            // Use central differences with the center points
//            // For corner c000, derivative in X direction is approximated by:
//            // dx000 ≈ (m000 - neighbor_x) / halfCellWidth
//
//            int cellWidth = NoiseChunkMixin.this.cellWidth;
//            int cellHeight = NoiseChunkMixin.this.cellHeight;
//
//            // X derivatives (using center points as neighbors)
//            dx000 = (m000 - c000) * 2.0 / cellWidth;
//            dx001 = (m001 - c001) * 2.0 / cellWidth;
//            dx010 = (m010 - c010) * 2.0 / cellWidth;
//            dx011 = (m011 - c011) * 2.0 / cellWidth;
//            dx100 = (c100 - m000) * 2.0 / cellWidth;
//            dx101 = (c101 - m001) * 2.0 / cellWidth;
//            dx110 = (c110 - m010) * 2.0 / cellWidth;
//            dx111 = (c111 - m011) * 2.0 / cellWidth;
//
//            // Y derivatives
//            dy000 = (m010 - c000) * 2.0 / cellHeight;
//            dy001 = (m011 - c001) * 2.0 / cellHeight;
//            dy010 = (c010 - m000) * 2.0 / cellHeight;
//            dy011 = (c011 - m001) * 2.0 / cellHeight;
//            dy100 = (m110 - c100) * 2.0 / cellHeight;
//            dy101 = (m111 - c101) * 2.0 / cellHeight;
//            dy110 = (c110 - m100) * 2.0 / cellHeight;
//            dy111 = (c111 - m101) * 2.0 / cellHeight;
//
//            // Z derivatives
//            dz000 = (m001 - c000) * 2.0 / cellWidth;
//            dz001 = (c001 - m000) * 2.0 / cellWidth;
//            dz010 = (m011 - c010) * 2.0 / cellWidth;
//            dz011 = (c011 - m010) * 2.0 / cellWidth;
//            dz100 = (m101 - c100) * 2.0 / cellWidth;
//            dz101 = (c101 - m100) * 2.0 / cellWidth;
//            dz110 = (m111 - c110) * 2.0 / cellWidth;
//            dz111 = (c111 - m110) * 2.0 / cellWidth;
//        }
//
//        public double compute(DensityFunction.FunctionContext context) {
//            if (context != NoiseChunkMixin.this) {
//                return this.wrapped().compute(context);
//            } else if (!NoiseChunkMixin.this.interpolating) {
//                throw new IllegalStateException("Trying to sample interpolator outside the interpolation loop");
//            } else {
//                // Normalize position within cell
//                double tx = (double)NoiseChunkMixin.this.inCellX / (double)NoiseChunkMixin.this.cellWidth;
//                double ty = (double)NoiseChunkMixin.this.inCellY / (double)NoiseChunkMixin.this.cellHeight;
//                double tz = (double)NoiseChunkMixin.this.inCellZ / (double)NoiseChunkMixin.this.cellWidth;
//
//                return tricubicHermite(tx, ty, tz);
//            }
//        }
//
//        /**
//         * Tricubic Hermite interpolation using corner values and their derivatives
//         * This gives us C¹ continuity!
//         */
//        private double tricubicHermite(double tx, double ty, double tz) {
//            // Hermite basis functions
//            double h00 = (1 + 2*tx) * (1-tx) * (1-tx);
//            double h10 = tx * (1-tx) * (1-tx);
//            double h01 = tx * tx * (3 - 2*tx);
//            double h11 = tx * tx * (tx - 1);
//
//            double hy00 = (1 + 2*ty) * (1-ty) * (1-ty);
//            double hy10 = ty * (1-ty) * (1-ty);
//            double hy01 = ty * ty * (3 - 2*ty);
//            double hy11 = ty * ty * (ty - 1);
//
//            double hz00 = (1 + 2*tz) * (1-tz) * (1-tz);
//            double hz10 = tz * (1-tz) * (1-tz);
//            double hz01 = tz * tz * (3 - 2*tz);
//            double hz11 = tz * tz * (tz - 1);
//
//            // Interpolate in 3D using tensor product of 1D Hermite polynomials
//            // This is a simplified version - full implementation would use all 64 terms
//
//            double cellWidth = NoiseChunkMixin.this.cellWidth;
//            double cellHeight = NoiseChunkMixin.this.cellHeight;
//
//            // Interpolate along edges first, then faces, then volume
//            // Corner contributions
//            double result = 0.0;
//
//            result += c000 * h00 * hy00 * hz00;
//            result += c100 * h01 * hy00 * hz00;
//            result += c010 * h00 * hy01 * hz00;
//            result += c110 * h01 * hy01 * hz00;
//            result += c001 * h00 * hy00 * hz01;
//            result += c101 * h01 * hy00 * hz01;
//            result += c011 * h00 * hy01 * hz01;
//            result += c111 * h01 * hy01 * hz01;
//
//            // Derivative contributions (scaled by cell dimensions)
//            result += dx000 * cellWidth * h10 * hy00 * hz00;
//            result += dx100 * cellWidth * h11 * hy00 * hz00;
//            result += dy000 * cellHeight * h00 * hy10 * hz00;
//            result += dy010 * cellHeight * h00 * hy11 * hz00;
//            result += dz000 * cellWidth * h00 * hy00 * hz10;
//            result += dz001 * cellWidth * h00 * hy00 * hz11;
//
//            // ... (would continue with all 64 terms for full accuracy)
//            // This is a simplified version showing the pattern
//
//            return result;
//        }
//
//        public void fillArray(double [] values, DensityFunction.ContextProvider contextProvider) {
//            contextProvider.fillAllDirectly(values, this);
//        }
//
//    }

}