package com.rae.creatingspace.mixin.worldgen;

import com.rae.creatingspace.content.worldgen.ImprovedLavaGen;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Aquifer.NoiseBasedAquifer.class)
public abstract class NoiseBasedAquiferMixin implements ImprovedLavaGen {
    @Unique
    private boolean cS_1_19_2$improvedLavaGen = false;
    @Override
    public void setImprovedGen(boolean improvedGen) {
        this.cS_1_19_2$improvedLavaGen = improvedGen;
    }

    @Override
    public boolean isImprovedGen() {
        return this.cS_1_19_2$improvedLavaGen;
    }
    @Shadow @Final private Aquifer.FluidPicker globalFluidPicker;

    @Shadow protected boolean shouldScheduleFluidUpdate;

    @Shadow protected abstract int getIndex(int p_158028_, int p_158029_, int p_158030_);

    @Shadow @Final protected long[] aquiferLocationCache;

    @Shadow @Final private PositionalRandomFactory positionalRandomFactory;

    @Shadow protected abstract Aquifer.FluidStatus getAquiferStatus(long p_188446_);

    @Shadow
    protected static double similarity(int p_158025_, int p_158026_) {
        return 0;
    }

    @Shadow @Final private static double FLOWING_UPDATE_SIMULARITY;

    @Shadow protected abstract double calculatePressure(DensityFunction.FunctionContext p_208189_, MutableDouble p_208190_, Aquifer.FluidStatus p_208191_, Aquifer.FluidStatus p_208192_);

    @Shadow @Final protected DensityFunction lavaNoise;

    @Inject(method = "computeFluidType",at = @At("HEAD"), cancellable = true)
    private void deleteLavaInFluidCompute(int x, int y, int z, Aquifer.FluidStatus globalStatus, int depth, CallbackInfoReturnable<BlockState> cir){
        BlockState $$5 = cS_1_19_2$getFluidType(globalStatus);
        if ((depth <= -10 ||isImprovedGen())&& depth != DimensionType.WAY_BELOW_MIN_Y && (cS_1_19_2$getFluidType(globalStatus) != Blocks.LAVA.defaultBlockState())|| isImprovedGen()) {

            int $$8 = Math.floorDiv(x, 64);
            int $$9 = Math.floorDiv(y, 40);
            int $$10 = Math.floorDiv(z, 64);
            double $$11 = this.lavaNoise.compute(new DensityFunction.SinglePointContext($$8, $$9, $$10));
            if (Math.abs($$11) > 0.3) {
                $$5 = cS_1_19_2$improvedLavaGen &&($$5.is(Blocks.LAVA))?Blocks.WATER.defaultBlockState():Blocks.LAVA.defaultBlockState();
            }//should make it configurable in noise router/aquifer
        }
        cir.setReturnValue($$5);
        cir.cancel();
    }

    @Unique
    private static BlockState cS_1_19_2$getFluidType(Aquifer.FluidStatus globalStatus) {
        return ((FluidStatusAccessor) (Object)globalStatus).getFluidType();
    }

    @Inject(method = "computeSubstance",at = @At("HEAD"),cancellable = true)
    private void deleteLavaInFluidCompute(DensityFunction.FunctionContext context, double aDouble, CallbackInfoReturnable<BlockState> cir){
        int $$2 = context.blockX();
        int $$3 = context.blockY();
        int $$4 = context.blockZ();
        if (aDouble > (double)0.0F) {
            this.shouldScheduleFluidUpdate = false;
            cir.setReturnValue(null);
        } else {
            Aquifer.FluidStatus $$5 = this.globalFluidPicker.computeFluid($$2, $$3, $$4);
            if ($$5.at($$3).is(Blocks.LAVA) && !cS_1_19_2$improvedLavaGen) {
                this.shouldScheduleFluidUpdate = false;
                cir.setReturnValue(Blocks.LAVA.defaultBlockState());
            } else {
                int $$6 = Math.floorDiv($$2 - 5, 16);
                int $$7 = Math.floorDiv($$3 + 1, 12);
                int $$8 = Math.floorDiv($$4 - 5, 16);
                int $$9 = Integer.MAX_VALUE;
                int $$10 = Integer.MAX_VALUE;
                int $$11 = Integer.MAX_VALUE;
                long $$12 = 0L;
                long $$13 = 0L;
                long $$14 = 0L;

                for(int $$15 = 0; $$15 <= 1; ++$$15) {
                    for(int $$16 = -1; $$16 <= 1; ++$$16) {
                        for(int $$17 = 0; $$17 <= 1; ++$$17) {
                            int $$18 = $$6 + $$15;
                            int $$19 = $$7 + $$16;
                            int $$20 = $$8 + $$17;
                            int $$21 = this.getIndex($$18, $$19, $$20);
                            long $$22 = this.aquiferLocationCache[$$21];
                            long $$23;
                            if ($$22 != Long.MAX_VALUE) {
                                $$23 = $$22;
                            } else {
                                RandomSource $$24 = this.positionalRandomFactory.at($$18, $$19, $$20);
                                $$23 = BlockPos.asLong($$18 * 16 + $$24.nextInt(10), $$19 * 12 + $$24.nextInt(9), $$20 * 16 + $$24.nextInt(10));
                                this.aquiferLocationCache[$$21] = $$23;
                            }

                            int $$26 = BlockPos.getX($$23) - $$2;
                            int $$27 = BlockPos.getY($$23) - $$3;
                            int $$28 = BlockPos.getZ($$23) - $$4;
                            int $$29 = $$26 * $$26 + $$27 * $$27 + $$28 * $$28;
                            if ($$9 >= $$29) {
                                $$14 = $$13;
                                $$13 = $$12;
                                $$12 = $$23;
                                $$11 = $$10;
                                $$10 = $$9;
                                $$9 = $$29;
                            } else if ($$10 >= $$29) {
                                $$14 = $$13;
                                $$13 = $$23;
                                $$11 = $$10;
                                $$10 = $$29;
                            } else if ($$11 >= $$29) {
                                $$14 = $$23;
                                $$11 = $$29;
                            }
                        }
                    }
                }

                Aquifer.FluidStatus $$30 = this.getAquiferStatus($$12);
                double $$31 = similarity($$9, $$10);
                BlockState $$32 = $$30.at($$3);
                if ($$31 <= (double)0.0F) {
                    this.shouldScheduleFluidUpdate = $$31 >= FLOWING_UPDATE_SIMULARITY;
                    cir.setReturnValue($$32);
                } else if ($$32.is(Blocks.WATER) && this.globalFluidPicker.computeFluid($$2, $$3 - 1, $$4).at($$3 - 1).is(Blocks.LAVA)) {
                    this.shouldScheduleFluidUpdate = true;
                    cir.setReturnValue($$32);
                } else {
                    MutableDouble $$34 = new MutableDouble(Double.NaN);
                    Aquifer.FluidStatus $$35 = this.getAquiferStatus($$13);
                    double $$36 = $$31 * this.calculatePressure(context, $$34, $$30, $$35);
                    if (aDouble + $$36 > (double)0.0F) {
                        this.shouldScheduleFluidUpdate = false;
                        cir.setReturnValue(null);
                    } else {
                        Aquifer.FluidStatus $$37 = this.getAquiferStatus($$14);
                        double $$38 = similarity($$9, $$11);
                        if ($$38 > (double)0.0F) {
                            double $$39 = $$31 * $$38 * this.calculatePressure(context, $$34, $$30, $$37);
                            if (aDouble + $$39 > (double)0.0F) {
                                this.shouldScheduleFluidUpdate = false;
                                cir.setReturnValue(null);
                            }
                        }

                        double $$40 = similarity($$10, $$11);
                        if ($$40 > (double)0.0F) {
                            double $$41 = $$31 * $$40 * this.calculatePressure(context, $$34, $$35, $$37);
                            if (aDouble + $$41 > (double)0.0F) {
                                this.shouldScheduleFluidUpdate = false;
                                cir.setReturnValue(null);
                            }
                        }

                        this.shouldScheduleFluidUpdate = true;
                        cir.setReturnValue($$32);
                    }
                }
            }
        }
        cir.cancel();
    }

}