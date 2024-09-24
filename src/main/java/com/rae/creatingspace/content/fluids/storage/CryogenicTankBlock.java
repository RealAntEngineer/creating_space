package com.rae.creatingspace.content.fluids.storage;


import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CryogenicTankBlock extends Block implements IBE<CryogenicTankBlockEntity> {
    public CryogenicTankBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public Class<CryogenicTankBlockEntity> getBlockEntityClass() {
        return CryogenicTankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CryogenicTankBlockEntity> getBlockEntityType() {
        return BlockEntityInit.CRYOGENIC_TANK.get();
    }
    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, entity, stack);

        if (worldIn.isClientSide)
            return;
        if (stack == null)
            return;
        withBlockEntityDo(worldIn, pos, be -> {
            be.setTank(stack.getOrCreateTag()
                    .getCompound("Fluid"));
            if (stack.hasCustomHoverName())
                be.setCustomName(stack.getHoverName());
        });
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos pos, BlockState state) {
        Item item = asItem();

        ItemStack stack = new ItemStack(item);
        Optional<CryogenicTankBlockEntity> blockEntityOptional = getBlockEntityOptional(blockGetter, pos);

        CompoundTag tag = stack.getOrCreateTag();
        FluidTank tank = blockEntityOptional.map(CryogenicTankBlockEntity::getTank).orElse(null);
        if(tank!= null){
            CompoundTag fluidTank = new CompoundTag();
            tag.put("Fluid", tank.writeToNBT(fluidTank));
        }
        Component customName = blockEntityOptional.map(CryogenicTankBlockEntity::getCustomName)
                .orElse(null);
        if (customName != null)
            stack.setHoverName(customName);

        stack.setTag(tag);
        return stack;
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0,pos,$1,blockEntity) -> {
            if(blockEntity instanceof CryogenicTankBlockEntity cryogenicTankBlockEntity) {
                cryogenicTankBlockEntity.tick(level,pos,state,cryogenicTankBlockEntity);
            }

        };
    }
}
