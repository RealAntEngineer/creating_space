package com.rae.creatingspace.content.fluids.storage;


import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
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
            CustomData tag = stack.get(DataComponents.CUSTOM_DATA);
            if (tag !=null)
                be.setTank(worldIn.registryAccess(),(CompoundTag) tag.copyTag()
                    .get("Fluid"));
            if (stack.has(DataComponents.CUSTOM_NAME))
                be.setCustomName(stack.getHoverName());
        });
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        Item item = asItem();

        ItemStack stack = new ItemStack(item);
        Optional<CryogenicTankBlockEntity> blockEntityOptional = getBlockEntityOptional(level, pos);
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data!=null) {
            CompoundTag tag = data.copyTag();
            FluidTank tank = blockEntityOptional.map(CryogenicTankBlockEntity::getTank).orElse(null);
            if (tank != null) {
                CompoundTag fluidTank = new CompoundTag();
                tag.put("Fluid", tank.writeToNBT(level.registryAccess(),fluidTank));
            }
            Component customName = blockEntityOptional.map(CryogenicTankBlockEntity::getCustomName)
                    .orElse(null);
            if (customName != null) {
                stack.getHoverName();
                stack.set(DataComponents.CUSTOM_NAME,customName);
            }

            stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
        }
        return stack;
    }

}
