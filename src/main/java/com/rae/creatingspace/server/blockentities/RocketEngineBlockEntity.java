package com.rae.creatingspace.server.blockentities;


import com.rae.creatingspace.server.blocks.multiblock.engines.BigEngineBlock;
import com.rae.creatingspace.server.blocks.multiblock.engines.SmallEngineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

    public abstract InteractionResult onClick(BlockState state,BlockPos pos,Player player, InteractionHand hand);


    public static class BigEngine extends RocketEngineBlockEntity{
        @Override
        public int getIsp() {
            return 350;
        }

        @Override
        public int getTrust() {
            return (int) (500000*9.81);
        }

        public InteractionResult onClick(BlockState state,BlockPos pos,Player player, InteractionHand hand) {
            if (!level.isClientSide() && player.getItemInHand(hand).isEmpty()){
                level.setBlock(pos, state.cycle(BigEngineBlock.ACTIVE), 3);
            }


            return InteractionResult.PASS;
        }
        public BigEngine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }
    }

    public static class SmallEngine extends RocketEngineBlockEntity{
        @Override
        public int getIsp() {
            return 350;
        }

        @Override
        public int getTrust() {
            return (int) (10000*9.81);
        }

        public InteractionResult onClick(BlockState state,BlockPos pos,Player player, InteractionHand hand) {
            if (!level.isClientSide() && player.getItemInHand(hand).isEmpty()){
                level.setBlock(pos, state.cycle(SmallEngineBlock.ACTIVE), 3);
            }

            return InteractionResult.PASS;
        }

        public SmallEngine(BlockEntityType<?> type, BlockPos pos, BlockState state) {
            super(type, pos, state);
        }
    }
}
