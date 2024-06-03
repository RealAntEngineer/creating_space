package com.rae.creatingspace.server.items.engine;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MechanicalItem extends Item {
    float efficiency = 1f;

    public MechanicalItem(Properties properties) {
        super(properties);
    }

    public MechanicalItem(Properties properties, float efficiency) {
        this(properties);
        this.efficiency = efficiency;
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putFloat("efficiency", efficiency);
        return stack;
    }
}
