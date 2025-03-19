package com.rae.creatingspace.content.rocket;

import com.rae.creatingspace.init.graphics.MenuTypesInit;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class RocketMenu extends MenuBase<RocketContraptionEntity> {

    public RocketMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
        super(type, id, inv, extraData);
    }

    public RocketMenu(MenuType<?> type, int id, Inventory inv, RocketContraptionEntity be) {
        super(type, id, inv, be);
    }

    public static RocketMenu create(int id, Inventory inv, RocketContraptionEntity be) {
        return new RocketMenu(MenuTypesInit.ROCKET_MENU.get(), id, inv, be);
    }

    @Override
    protected RocketContraptionEntity createOnClient(FriendlyByteBuf extraData) {
        //System.out.println("create on client");
        int entityID = extraData.readVarInt();
        Entity entityByID = Minecraft.getInstance().level.getEntity(entityID);
        if (!(entityByID instanceof RocketContraptionEntity))
            return null;
        RocketContraptionEntity rocketEntity = (RocketContraptionEntity) entityByID;
        return rocketEntity;
    }

    @Override
    protected void initAndReadInventory(RocketContraptionEntity contentHolder) {

    }

    @Override
    protected void addSlots() {
    }

    @Override
    protected void saveData(RocketContraptionEntity contentHolder) {
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}