package com.rae.creatingspace.client.gui.menu;

import com.rae.creatingspace.init.graphics.MenuTypesInit;
import com.rae.creatingspace.server.blockentities.RocketEngineerTableBlockEntity;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Objects;

public class EngineerTableMenu extends MenuBase<RocketEngineerTableBlockEntity> {

    private Slot inputSlot;
    private Slot outputSlot;

    public EngineerTableMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public EngineerTableMenu(MenuType<?> type, int id, Inventory inv, RocketEngineerTableBlockEntity be) {
        super(type, id, inv, be);
    }

    public boolean canWrite() {
        return inputSlot.hasItem() && !outputSlot.hasItem();
    }

    public static EngineerTableMenu create(int id, Inventory inv, RocketEngineerTableBlockEntity be) {
        return new EngineerTableMenu(MenuTypesInit.ENGINEER_TABLE.get(), id, inv, be);
    }

    @Override
    protected RocketEngineerTableBlockEntity createOnClient(FriendlyByteBuf extraData) {
        ClientLevel world = Minecraft.getInstance().level;
        assert world != null;
        BlockEntity blockEntity = world.getBlockEntity(extraData.readBlockPos());
        if (blockEntity instanceof RocketEngineerTableBlockEntity engineerTable) {
            engineerTable.readClient(Objects.requireNonNull(extraData.readNbt()));
            return engineerTable;
        }
        return null;
    }

    @Override
    protected void initAndReadInventory(RocketEngineerTableBlockEntity contentHolder) {

    }

    @Override
    protected void addSlots() {
        inputSlot = new SlotItemHandler(contentHolder.inventory, 0, 21, 57) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return AllItems.EMPTY_SCHEMATIC.isIn(stack) || AllItems.SCHEMATIC_AND_QUILL.isIn(stack)
                        || AllItems.SCHEMATIC.isIn(stack);
            }
        };

        outputSlot = new SlotItemHandler(contentHolder.inventory, 1, 166, 57) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        };

        addSlot(inputSlot);
        addSlot(outputSlot);

        // player Slots
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(player.getInventory(), col + row * 9 + 9, 100 + col * 18, 105 + 41 + row * 18));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            this.addSlot(new Slot(player.getInventory(), hotbarSlot, 100 + hotbarSlot * 18, 163 + 41));
        }
    }

    @Override
    protected void saveData(RocketEngineerTableBlockEntity contentHolder) {

    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot clickedSlot = getSlot(index);
        if (!clickedSlot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = clickedSlot.getItem();
        if (index < 2)
            moveItemStackTo(stack, 2, slots.size(), false);
        else
            moveItemStackTo(stack, 0, 1, false);

        return ItemStack.EMPTY;
    }
}
