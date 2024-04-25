package com.rae.creatingspace.server.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class JetPackItem extends Item {
    public JetPackItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int nbr, boolean isMoving) {
        if (entity instanceof ServerPlayer player) {
            player.isShiftKeyDown();
        }
    }
}
