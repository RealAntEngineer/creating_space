package com.rae.creatingspace.content.rocket.engine;


import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.IMass;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;

public abstract class RocketEngineBlockEntity extends SmartBlockEntity {

    public RocketEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);

    }

    public int getIsp() {
        return (int) (getPropellantType().getMaxISP() * getEfficiency());
    }

    public abstract PropellantType getPropellantType();

    public abstract float getEfficiency();

    public abstract int getThrust();//Newtons

    public static class NbtDependent extends RocketEngineBlockEntity implements IMass {
        int                    thrust         = 1000;
        Holder<PropellantType> propellantType = null;//= PropellantTypeInit.METHALOX.get();
        Float                  efficiency     = 1f;
        int                    mass           = 0;

        public NbtDependent(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        @Override
        public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

        }

        @Override
        public void initialize() {
            notifyUpdate();
            super.initialize();
        }

        @Override
        protected void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
            nbt.putInt("thrust", thrust);
            nbt.putInt("mass", mass);
            nbt.putFloat("efficiency", efficiency);
            try {
                nbt.put("propellantType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE,
                        Objects.requireNonNull(propellantType.getKey()).location()).getOrThrow());
            } catch (Throwable error) {
                CreatingSpace.LOGGER.warn("catch exception will saving engine : " + propellantType);
                CreatingSpace.LOGGER.warn("exeption : " + error.getMessage());
                nbt.put("propellantType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, CreatingSpace.resource("methalox")).getOrThrow());//PropellantTypeInit.METHALOX.getId()));
            }
            super.write(nbt, registries, clientPacket);
        }

        @Override
        public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
            super.read(nbt, registries, clientPacket);
            setFromNbt(nbt, registries);
        }

        public void setFromNbt(CompoundTag nbt, HolderLookup.Provider registries) {

            thrust = nbt.getInt("thrust");
            efficiency = nbt.getFloat("efficiency");
            mass = nbt.getInt("mass");
            try {
                propellantType = registries.holderOrThrow(ResourceKey.create(
                        PropellantTypeInit.Keys.PROPELLANT_TYPE,
                        ResourceLocation.CODEC.parse(NbtOps.INSTANCE, nbt.get("propellantType")).getOrThrow()));
                /*propellantType = PropellantTypeInit.getSyncedPropellantRegistry().getOptional().getOrThrow())
                        .orElse(PropellantTypeInit.METHALOX.get());*/
            } catch (Throwable error) {
                //propellantType = PropellantTypeInit.METHALOX.get();
                CreatingSpace.LOGGER.warn("catch exception will loading engine : " + nbt);
                CreatingSpace.LOGGER.warn("exeption : " + error.getMessage());
                propellantType = null;
            }
        }

        @Override
        public PropellantType getPropellantType() {
            return propellantType.value();
        }

        @Override
        public float getEfficiency() {
            return efficiency;
        }

        @Override
        public int getThrust() {
            return thrust;
        }

        public void setThrust(int thrust) {
            this.thrust = thrust;
        }

        @Override
        public float getMass() {
            return mass;
        }
    }

    public static class BigEngine extends RocketEngineBlockEntity {

        public BigEngine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        @Override
        public PropellantType getPropellantType() {
            return PropellantTypeInit.METHALOX_DIRECT;
        }

        @Override
        public float getEfficiency() {
            return 0.79f;
        }

        @Override
        public int getThrust() {
            return CSConfigs.SERVER.rocketEngine.bigRocketEngineThrust.get();
        }

        @Override
        public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

        }
    }

    public static class SmallEngine extends RocketEngineBlockEntity {

        public SmallEngine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        @Override
        public PropellantType getPropellantType() {
            //need testing.
            return PropellantTypeInit.METHALOX_DIRECT;
        }

        @Override
        public float getEfficiency() {
            return 0.79f;
        }

        @Override
        public int getThrust() {
            return CSConfigs.SERVER.rocketEngine.smallRocketEngineThrust.get();
        }

        @Override
        public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

        }

    }
}
