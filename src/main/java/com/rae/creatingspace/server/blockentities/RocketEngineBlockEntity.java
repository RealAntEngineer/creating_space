package com.rae.creatingspace.server.blockentities;


import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.init.TagsInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public abstract class RocketEngineBlockEntity extends BlockEntity {

    public abstract int getIsp(); //seconds

    public abstract int getThrust();//Newtons
    public abstract TagKey<Fluid> getOxidizerTag();
    public abstract TagKey<Fluid> getFuelTag();
    public abstract float getOxFuelRatio();


    public RocketEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type,pos, state);

    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

    }
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
    }


    public static class BigEngine extends RocketEngineBlockEntity{
        @Override
        public int getIsp() {
            return CSConfigs.SERVER.rocketEngine.methaloxISP.get();
        }

        @Override
        public int getThrust() {
            return  CSConfigs.SERVER.rocketEngine.bigRocketEngineTrust.get();
        }
        @Override
        public TagKey<Fluid> getOxidizerTag() {
            return TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag;
        }

        @Override
        public TagKey<Fluid> getFuelTag() {
            return TagsInit.CustomFluidTags.LIQUID_METHANE.tag;
        }

        @Override
        public float getOxFuelRatio() {
            return 2f;
        }

        public BigEngine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }
    }

    public static class SmallEngine extends RocketEngineBlockEntity{
        public int getIsp() {
            return CSConfigs.SERVER.rocketEngine.methaloxISP.get();
        }

        @Override
        public int getThrust() {
            return  CSConfigs.SERVER.rocketEngine.smallRocketEngineTrust.get();
        }
        @Override
        public TagKey<Fluid> getOxidizerTag() {
            return TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag;
        }

        @Override
        public TagKey<Fluid> getFuelTag() {
            return TagsInit.CustomFluidTags.LIQUID_METHANE.tag;
        }

        @Override
        public float getOxFuelRatio() {
            return 2f;
        }


        public SmallEngine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

    }
}
