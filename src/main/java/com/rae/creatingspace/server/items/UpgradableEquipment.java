package com.rae.creatingspace.server.items;

import com.google.common.collect.Multimap;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UpgradableEquipment extends BaseArmorItem {
    public UpgradableEquipment(ArmorMaterial armorMaterial, EquipmentSlot slot, Properties properties, ResourceLocation textureLoc) {
        super(armorMaterial, slot, properties, textureLoc);
    }

    //that's better, way better -> the upgrade item will be an armor item of the same slot

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (interactionHand != InteractionHand.OFF_HAND) {
            ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
            ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (offHandStack.getItem().canEquip(offHandStack, this.slot, player)) {
                player.setItemInHand(InteractionHand.OFF_HAND, getUpgrade(mainHandStack));
                player.setItemInHand(InteractionHand.MAIN_HAND, setUpgrade(mainHandStack, offHandStack));
            }
        }
        return super.use(level, player, interactionHand);
    }

    @Override
    public @NotNull ItemStack getDefaultInstance() {
        return setUpgrade(super.getDefaultInstance(), ItemStack.EMPTY);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ItemStack newStack = getUpgrade(stack);
        return newStack.isEmpty() ? super.getAttributeModifiers(slot, stack) :
                newStack.getItem().getAttributeModifiers(slot, newStack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        ItemStack upgrade = getUpgrade(itemStack);
        if (!upgrade.isEmpty()) {
            components.add(
                    Component.translatable("container.upgrade")
                            .append(" : ")
                            .append(upgrade.getItem().getDescription()));
        }
        super.appendHoverText(itemStack, level, components, flag);
    }

    @NotNull
    private static ItemStack getUpgrade(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement("upgradeElement");
        return ItemStack.of(tag);
    }

    private static ItemStack setUpgrade(ItemStack stack, ItemStack upgrade) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.put("upgradeElement", upgrade.serializeNBT());
        ItemStack newStack = stack.copy();
        newStack.setTag(tag);
        return newStack;
    }
}
