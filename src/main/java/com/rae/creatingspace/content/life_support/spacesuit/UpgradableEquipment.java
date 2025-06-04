package com.rae.creatingspace.content.life_support.spacesuit;

import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

public class UpgradableEquipment extends BaseArmorItem implements MenuProvider {
    public UpgradableEquipment(Holder<ArmorMaterial> armorMaterial, Type slot, Properties properties, ResourceLocation textureLoc) {
        super(armorMaterial, slot, properties, textureLoc);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.ItemHandler.ITEM,
                (itemStack, unused) -> new ItemStackHandler(1)
        );
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (interactionHand != InteractionHand.OFF_HAND) {
            ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
            ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (offHandStack.getItem().canEquip(offHandStack, this.type.getSlot(), player)) {
                player.setItemInHand(InteractionHand.OFF_HAND, getUpgrade(mainHandStack));
                player.setItemInHand(InteractionHand.MAIN_HAND, setUpgrade(mainHandStack, offHandStack));
            }
        }
        return super.use(level, player, interactionHand);
    }

    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null)
            return InteractionResult.PASS;
        return use(context.getLevel(), context.getPlayer(), context.getHand()).getResult();
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        return setUpgrade(super.getDefaultInstance(), ItemStack.EMPTY);
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ItemStack newStack = getUpgrade(stack);
        return newStack.isEmpty() ? super.getDefaultAttributeModifiers(stack) :
                newStack.getItem().getDefaultAttributeModifiers(newStack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        ItemStack upgrade = getUpgrade(stack);
        if (!upgrade.isEmpty()) {
            tooltipComponents.add(
                    Component.translatable("container.upgrade")
                            .append(" : ")
                            .append(upgrade.getItem().getDescription()));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @NotNull
    private static ItemStack getUpgrade(ItemStack stack) {
        ItemStackHandler handler = (ItemStackHandler) stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler!=null)
            return  handler.getStackInSlot(0);
        return ItemStack.EMPTY;
    }

    private static ItemStack setUpgrade(ItemStack stack, ItemStack upgrade) {
        ItemStack newStack = stack.copy();
        ItemStackHandler handler = (ItemStackHandler) stack.getCapability(Capabilities.ItemHandler.ITEM);
        if (handler!=null) {
            handler.setStackInSlot(0,upgrade);
        }
        return newStack;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return  Component.translatable("container.my_item_menu");

    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        ItemStack heldItem = player.getMainHandItem();
        return UpgradableEquipmentMenu.create(i,inventory,heldItem);
    }
}
