package com.rae.creatingspace.content.rocket.engine.table;

import com.rae.creatingspace.init.graphics.MenuTypesInit;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.NbtOps;
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
    public RocketEngineerTableBlockEntity.SyncData getSyncData() {
        return syncData;
    }

    //TODO we remove everything apart from the outputslot -> transition to a assembly line recipe
    //  add summary to the item
    RocketEngineerTableBlockEntity.SyncData syncData;
    private Slot outputSlot;

    public EngineerTableMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public EngineerTableMenu(MenuType<?> type, int id, Inventory inv, RocketEngineerTableBlockEntity be) {
        super(type, id, inv, be);

    }

    public boolean canWrite() {
        return !outputSlot.hasItem();
    }

    public static EngineerTableMenu create(int id, Inventory inv, RocketEngineerTableBlockEntity be) {
        return new EngineerTableMenu(MenuTypesInit.ENGINEER_TABLE.get(), id, inv, be);
    }

    @Override
    protected RocketEngineerTableBlockEntity createOnClient(FriendlyByteBuf extraData) {
        //System.out.println("create on client");
        ClientLevel world = Minecraft.getInstance().level;
        assert world != null;
        BlockEntity blockEntity = world.getBlockEntity(extraData.readBlockPos());
        if (blockEntity instanceof RocketEngineerTableBlockEntity engineerTable) {
            engineerTable.readClient(Objects.requireNonNull(extraData.readNbt()));
            return engineerTable;
        }
        //System.out.println("fail");
        return null;
    }

    @Override
    protected void initAndReadInventory(RocketEngineerTableBlockEntity contentHolder) {
        syncData = RocketEngineerTableBlockEntity.SyncData.getCoded()
                .parse(NbtOps.INSTANCE, contentHolder.saveScreenData()).get().orThrow();

    }

    @Override
    protected void addSlots() {
        outputSlot = new SlotItemHandler(contentHolder.inventory, 0, 316, 100) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        };
        addSlot(outputSlot);

        /*exhaustSlots = new ArrayList<>();
        int i = 1;
        for (Couple<Integer> exhaustSlotCoord : syncData.exhaustPackType(Objects.requireNonNull(contentHolder.getLevel()).isClientSide).getSlots()) {
            exhaustSlots.add(new SlotItemHandler(contentHolder.inventory, i, 127 + exhaustSlotCoord.getFirst(), 43 + exhaustSlotCoord.getSecond()) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return true;
                }
            });
            addSlot(exhaustSlots.get(exhaustSlots.size() - 1));
            i++;
        }
        powerSlots = new ArrayList<>();
        for (Couple<Integer> powerSlotCoord : syncData.powerPackType(Objects.requireNonNull(contentHolder.getLevel()).isClientSide).getSlots()) {
            powerSlots.add(new SlotItemHandler(contentHolder.inventory, i, 5 + powerSlotCoord.getFirst(), 43 + powerSlotCoord.getSecond()) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return true;
                }
            });
            addSlot(powerSlots.get(powerSlots.size() - 1));
            i++;
        }*/


        // player Slots
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(player.getInventory(), col + row * 9 + 9, 161 + col * 18, 105 + 43 + row * 18));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            this.addSlot(new Slot(player.getInventory(), hotbarSlot, 161 + hotbarSlot * 18, 163 + 43));
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