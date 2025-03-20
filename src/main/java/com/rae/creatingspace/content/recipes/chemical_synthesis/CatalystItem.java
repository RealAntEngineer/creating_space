package com.rae.creatingspace.content.recipes.chemical_synthesis;

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
