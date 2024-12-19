package com.rae.creatingspace.server.blockentities;


import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.IMass;
import com.rae.creatingspace.api.design.PropellantType;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public abstract class RocketEngineBlockEntity extends SmartBlockEntity {

    public int getIsp() {
        return (int) (getPropellantType().getMaxISP() * getEfficiency());
    }

    public abstract int getThrust();//Newtons

    public abstract float getEfficiency();

    public abstract PropellantType getPropellantType();

    public RocketEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type,pos, state);

    }

    public static class NbtDependent extends RocketEngineBlockEntity implements IMass {
        int thrust = 1000;
        PropellantType propellantType = PropellantTypeInit.METHALOX.get();
        Float efficiency = 1f;
        int mass = 0;

        public NbtDependent(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

        @Override
        public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

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
        protected void write(CompoundTag nbt, boolean clientPacket) {
            nbt.putInt("thrust", thrust);
            nbt.putInt("mass", mass);
            nbt.putFloat("efficiency", efficiency);
            try {
                nbt.put("propellantType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE,
                        PropellantTypeInit.getSyncedPropellantRegistry().getKey(propellantType)).get().orThrow());
            } catch (Throwable ignored){
                nbt.put("propellantType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE,PropellantTypeInit.METHALOX.getId() ).get().orThrow());
                CreatingSpace.LOGGER.warn("catch exeption will saving engine : "+propellantType);
                CreatingSpace.LOGGER.warn("exeption : "+ignored.getMessage());
            }
            super.write(nbt, clientPacket);
        }

        @Override
        public void read(CompoundTag nbt, boolean clientPacket) {
            super.read(nbt,clientPacket);
            setFromNbt(nbt);
        }

        public void setFromNbt(CompoundTag nbt) {

                thrust = nbt.getInt("thrust");
                efficiency = nbt.getFloat("efficiency");
                mass = nbt.getInt("mass");
            try {
                propellantType = PropellantTypeInit.getSyncedPropellantRegistry().getOptional(ResourceLocation.CODEC.parse(NbtOps.INSTANCE, nbt.get("propellantType")).get().orThrow())
                        .orElse(PropellantTypeInit.METHALOX.get());
            } catch (Throwable ignored){
                propellantType = PropellantTypeInit.METHALOX.get();
                CreatingSpace.LOGGER.warn("catch exeption will loading engine : "+propellantType);
                CreatingSpace.LOGGER.warn("exeption : "+ignored.getMessage());
            }
        }

        @Override
        public float getMass() {
            return mass;
        }

        @Override
        public void initialize() {
            notifyUpdate();
            super.initialize();
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

        @Override
        public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

        }
    }

    public static class SmallEngine extends RocketEngineBlockEntity{
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

        @Override
        public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

        }

    }
}
