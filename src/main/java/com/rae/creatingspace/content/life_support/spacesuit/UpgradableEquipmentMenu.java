package com.rae.creatingspace.content.life_support.spacesuit;

import com.rae.creatingspace.init.graphics.MenuTypesInit;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class UpgradableEquipmentMenu extends MenuBase<ItemStack> {
    private Slot upgradeItem;

    public UpgradableEquipmentMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public UpgradableEquipmentMenu(MenuType<?> type, int id, Inventory inv, ItemStack be) {
        super(type, id, inv, be);
    }

    public boolean canWrite() {
        return true;
    }

    public static UpgradableEquipmentMenu create(int id, Inventory inv, ItemStack be) {
        return new UpgradableEquipmentMenu(MenuTypesInit.UPGRADABLE_EQUIPMENT.get(), id, inv, be);
    }

    @Override
    protected ItemStack createOnClient(FriendlyByteBuf extraData) {
        //System.out.println("create on client");
        return extraData.readItem();
    }

    @Override
    protected void initAndReadInventory(ItemStack contentHolder) {
    }

    @Override
    protected void addSlots() {
        //temporary false container
        upgradeItem = new SlotItemHandler(new ItemStackHandler(1),
                0, 24, 23) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.canEquip(((UpgradableEquipment) contentHolder.getItem()).getEquipmentSlot(), player);
            }
        };
        CompoundTag nbt = contentHolder.getOrCreateTag();
        ItemStack upgradeStack = ItemStack.of(nbt.getCompound("upgradeElement"));
        upgradeItem.set(upgradeStack);
        addSlot(upgradeItem);

        // player Slots
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(player.getInventory(), col + row * 9 + 9, 8 + col * 18, 43 + 43 + row * 18));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            this.addSlot(new Slot(player.getInventory(), hotbarSlot, 8 + hotbarSlot * 18, 101 + 43));
        }
    }

    @Override
    protected void saveData(ItemStack contentHolder) {
        CompoundTag nbt = contentHolder.getOrCreateTag();
        nbt.put("upgradeElement", upgradeItem.getItem().serializeNBT());
        contentHolder.setTag(nbt);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = clickedSlot.getItem();
        if (index < 1)
            moveItemStackTo(stack, 1, slots.size(), false);
        else
            moveItemStackTo(stack, 0, 1, false);

        return ItemStack.EMPTY;
    }
}