package com.rae.creatingspace.server.blockentities;


import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.init.TagsInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
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

    public static class NbtDependent extends RocketEngineBlockEntity {
        int isp;
        int thrust;
        float oxFuelRatio;
        TagKey<Fluid> oxidizerTag;
        TagKey<Fluid> fuelTag;

        public NbtDependent(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        @Override
        public int getIsp() {
            return isp;
        }

        @Override
        public int getThrust() {
            return thrust;
        }

        @Override
        public TagKey<Fluid> getOxidizerTag() {
            return oxidizerTag;
        }

        @Override
        public TagKey<Fluid> getFuelTag() {
            return fuelTag;
        }

        @Override
        public float getOxFuelRatio() {
            return oxFuelRatio;
        }


        public void setIsp(int isp) {
            this.isp = isp;
        }

        public void setThrust(int thrust) {
            this.thrust = thrust;
        }

        public void setOxFuelRatio(float oxFuelRatio) {
            this.oxFuelRatio = oxFuelRatio;
        }

        public void setOxidizerTag(TagKey<Fluid> oxidizerTag) {
            this.oxidizerTag = oxidizerTag;
        }

        public void setFuelTag(TagKey<Fluid> fuelTag) {
            this.fuelTag = fuelTag;
        }

        @Override
        protected void saveAdditional(CompoundTag nbt) {
            nbt.putInt("isp", isp);
            nbt.putInt("thrust", thrust);
            nbt.putFloat("oxFuelRatio", oxFuelRatio);
            nbt.putString("fuelTag", fuelTag.location().toString());
            nbt.putString("oxidizerTag", oxidizerTag.location().toString());
            super.saveAdditional(nbt);
        }

        @Override
        public void load(CompoundTag nbt) {
            super.load(nbt);
            isp = nbt.getInt("isp");
            thrust = nbt.getInt("thrust");
            oxFuelRatio = nbt.getFloat("oxFuelRatio");
            fuelTag = FluidTags.create(new ResourceLocation(nbt.getString("fuelTag")));
            oxidizerTag = FluidTags.create(new ResourceLocation(nbt.getString("oxidizerTag")));
        }
    }

    public static class BigEngine extends RocketEngineBlockEntity{
        @Override
        public int getIsp() {
            return CSConfigs.SERVER.rocketEngine.methaloxISP.get();
        }

        @Override
        public int getThrust() {
            return  CSConfigs.SERVER.rocketEngine.bigRocketEngineThrust.get();
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
            return  CSConfigs.SERVER.rocketEngine.smallRocketEngineThrust.get();
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
