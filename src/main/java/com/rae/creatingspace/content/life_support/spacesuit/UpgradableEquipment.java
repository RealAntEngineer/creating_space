package com.rae.creatingspace.content.life_support.spacesuit;

import com.google.common.collect.Multimap;
import com.simibubi.create.content.equipment.armor.BaseArmorItem;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UpgradableEquipment extends BaseArmorItem {
    public UpgradableEquipment(ArmorMaterial armorMaterial, Type slot, Properties properties, ResourceLocation textureLoc) {
        super(armorMaterial, slot, properties, textureLoc);
    }

    //that's better, way better -> the upgrade item will be an armor item of the same slot
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundTag nbt) {
        return new UpgradeInventory(stack);
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

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();

        if (!world.isClientSide && player instanceof ServerPlayer) {
            NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider(
                    (id, inv, p) -> UpgradableEquipmentMenu.create(id, inv, context.getItemInHand()),
                    Component.translatable("container.my_item_menu")
            ), buf -> buf.writeItem(context.getItemInHand()));
        }

        return InteractionResult.SUCCESS;
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

    //for the menu
    public static class UpgradeInventory implements ICapabilityProvider {

        private final ItemStackHandler itemStackHandler = new ItemStackHandler(1);
        private final LazyOptional<ItemStackHandler> handler = LazyOptional.of(() -> itemStackHandler);

        public UpgradeInventory(ItemStack stack) {
            // Load the inventory from the item's NBT data
            CompoundTag nbt = stack.getOrCreateTag();
            itemStackHandler.deserializeNBT(nbt.getCompound("upgradeElement"));
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            if (cap == ForgeCapabilities.ITEM_HANDLER) {
                return handler.cast();
            }
            return LazyOptional.empty();
        }

        public void saveInventory(ItemStack stack) {
            CompoundTag nbt = stack.getOrCreateTag();
            nbt.put("upgradeElement", itemStackHandler.serializeNBT());
            stack.setTag(nbt);
        }
    }
}
