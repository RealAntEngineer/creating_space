package com.rae.creatingspace.server.blockentities;


import com.rae.creatingspace.configs.CSConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RocketEngineBlockEntity extends BlockEntity {

    public abstract int getIsp(); //seconds

    public abstract int getTrust();//Newtons


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
        public int getTrust() {
            return  CSConfigs.SERVER.rocketEngine.bigRocketEngineTrust.get();
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
        public int getTrust() {
            return  CSConfigs.SERVER.rocketEngine.smallRocketEngineTrust.get();
        }


        public SmallEngine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }

    }
}
