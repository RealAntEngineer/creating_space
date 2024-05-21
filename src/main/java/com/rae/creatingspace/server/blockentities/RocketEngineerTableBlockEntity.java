package com.rae.creatingspace.server.blockentities;

import com.rae.creatingspace.client.gui.menu.EngineerTableMenu;
import com.rae.creatingspace.server.design.PropellantType;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class RocketEngineerTableBlockEntity extends SmartBlockEntity implements MenuProvider {
    public TableInventory inventory;

    @Override
    public Component getDisplayName() {
        return Component.literal("coucou");
    }


    public class TableInventory extends ItemStackHandler {
        public TableInventory() {
            super(2);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return super.getStackInSlot(slot);
        }

    }

    public RocketEngineerTableBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
        inventory = new TableInventory();
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return EngineerTableMenu.create(id, inv, this);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }


    public static CompoundTag fromIngredients(float size, PropellantType type, float totalEfficiency) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("isp", (int) (type.getMaxISP() * totalEfficiency));
        nbt.putInt("thrust", (int) size);

        Map<TagKey<Fluid>, Float> map = type.getPropellantRatio();
        nbt.putFloat("oxFuelRatio", map.values().stream().toList().get(0) / map.values().stream().toList().get(1));
        nbt.putString("fuelTag", map.keySet().stream().toList().get(1).location().toString());
        nbt.putString("oxidizerTag", map.keySet().stream().toList().get(0).location().toString());
        return nbt;
    }
}
