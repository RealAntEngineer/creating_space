package com.rae.creatingspace.legacy.server.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CatalystItem extends Item {
    public CatalystItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }
}
