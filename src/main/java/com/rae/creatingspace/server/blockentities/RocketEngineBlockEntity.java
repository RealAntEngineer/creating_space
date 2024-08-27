package com.rae.creatingspace.server.blockentities;


import com.rae.creatingspace.api.IMass;
import com.rae.creatingspace.api.design.PropellantType;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RocketEngineBlockEntity extends BlockEntity {

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
        Integer size = 100;
        int mass = 0;

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
            try {
                nbt.put("propellantType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE,
                        PropellantTypeInit.getSyncedPropellantRegistry().getKey(propellantType)).get().orThrow());
            } catch (Exception ignored){
                nbt.put("propellantType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE,PropellantTypeInit.METHALOX.getId() ).get().orThrow());
            }
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
                mass = nbt.getInt("mass");
            try {
                propellantType = PropellantTypeInit.getSyncedPropellantRegistry().getOptional(ResourceLocation.CODEC.parse(NbtOps.INSTANCE, nbt.get("propellantType")).get().orThrow())
                        .orElse(PropellantTypeInit.METHALOX.get());
            } catch (Exception ignored){
                propellantType = PropellantTypeInit.METHALOX.get();
            }
        }

        @Override
        public float getMass() {
            return mass;
        }

        @Override
        public void saveToItem(ItemStack itemStack) {
            super.saveToItem(itemStack);
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

    }
}
