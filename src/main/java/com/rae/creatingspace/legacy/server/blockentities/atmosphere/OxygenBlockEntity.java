package com.rae.creatingspace.legacy.server.blockentities.atmosphere;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class OxygenBlockEntity extends BlockEntity {
    public OxygenBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    private BlockPos masterPos;

    public BlockPos getMasterPos() {
        return masterPos;
    }

    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putLong("masterPos",masterPos.asLong());
        super.saveAdditional(nbt);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        assert level != null;
        level.setBlockAndUpdate(getBlockPos(), Blocks.AIR.defaultBlockState());
        setRemoved();
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        masterPos = BlockPos.of(nbt.getLong("masterPos"));
    }
}
