package com.rae.creatingspace.server.blockentities;


import com.rae.creatingspace.api.IMass;
import com.rae.creatingspace.api.design.PropellantType;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public abstract class RocketEngineBlockEntity extends BlockEntity {

    public int getIsp() {
        return (int) (getPropellantType().getMaxISP() * getEfficiency());
    }

    public abstract int getThrust();//Newtons

    public abstract float getEfficiency();

    public TagKey<Fluid> getOxidizerTag() {
        return getPropellantType().getPropellantRatio().keySet().stream().toList().get(0);
    }

    public TagKey<Fluid> getFuelTag() {
        return getPropellantType().getPropellantRatio().keySet().stream().toList().get(1);
    }

    public float getOxFuelRatio() {
        return getPropellantType().getPropellantRatio().get(getOxidizerTag())
                / getPropellantType().getPropellantRatio().get(getFuelTag());
    }

    public abstract PropellantType getPropellantType();

    public RocketEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type,pos, state);

    }

    public static class NbtDependent extends RocketEngineBlockEntity implements IMass {
        int thrust = 1000;
        PropellantType propellantType = PropellantTypeInit.METHALOX.get();
        Float efficiency = 1f;
        Integer size = 100;

        public NbtDependent(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }
        @Override
        public float getEfficiency() {
            return efficiency;
        }

        @Override
        public int getThrust() {
            return thrust;
        }

        @Override
        public PropellantType getPropellantType() {
            return propellantType;
        }

        public void setThrust(int thrust) {
            this.thrust = thrust;
        }

        @Override
        protected void saveAdditional(CompoundTag nbt) {
            nbt.putInt("thrust", thrust);
            nbt.putInt("size", size);
            nbt.putFloat("efficiency", efficiency);
            nbt.put("propellantType", PropellantTypeInit.PROPELLANT_TYPE.get()
                    .getCodec().encodeStart(NbtOps.INSTANCE, propellantType).get().orThrow());
            super.saveAdditional(nbt);
        }

        @Override
        public void load(CompoundTag nbt) {
            super.load(nbt);
            setFromNbt(nbt);
        }

        public void setFromNbt(CompoundTag nbt) {
            thrust = nbt.getInt("thrust");
            efficiency = nbt.getFloat("efficiency");
            propellantType = PropellantTypeInit.PROPELLANT_TYPE.get()
                    .getCodec().parse(NbtOps.INSTANCE, nbt.get("propellantType"))
                    .resultOrPartial(s -> {
                    }).orElse(PropellantTypeInit.METHALOX.get());
            ;
        }

        @Override
        public float getMass() {
            return 0;
        }
    }

    public static class BigEngine extends RocketEngineBlockEntity{
        @Override
        public float getEfficiency() {
            return 0.79f;
        }

        @Override
        public int getThrust() {
            return  CSConfigs.SERVER.rocketEngine.bigRocketEngineThrust.get();
        }

        @Override
        public PropellantType getPropellantType() {
            return PropellantTypeInit.METHALOX.get();
        }

        public BigEngine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }
    }

    public static class SuperEngine extends RocketEngineBlockEntity {
        @Override
        public float getEfficiency() {
            return 0.9f;
        }

        @Override
        public int getThrust() {
            return CSConfigs.SERVER.rocketEngine.superRocketEngineThrust.get();
        }

        @Override
        public PropellantType getPropellantType() {
            return PropellantTypeInit.METALIC_HYDROGEN.get();
        }

        public SuperEngine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }
    }

    public static class SmallEngine extends RocketEngineBlockEntity{
        @Override
        public int getIsp() {
            return CSConfigs.SERVER.rocketEngine.methaloxISP.get();
        }

        @Override
        public int getThrust() {
            return  CSConfigs.SERVER.rocketEngine.smallRocketEngineThrust.get();
        }

        @Override
        public float getEfficiency() {
            return 0.79f;
        }

        @Override
        public PropellantType getPropellantType() {
            return PropellantTypeInit.METHALOX.get();
        }


        public SmallEngine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

    }
}
