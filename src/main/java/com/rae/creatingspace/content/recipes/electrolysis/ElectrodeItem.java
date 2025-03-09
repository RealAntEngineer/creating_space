package com.rae.creatingspace.content.recipes.electrolysis;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ElectrodeItem extends Item {
    public ElectrodeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }


    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return super.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public void onDestroyed(ItemEntity p_150887_) {
        super.onDestroyed(p_150887_);
    }
}
