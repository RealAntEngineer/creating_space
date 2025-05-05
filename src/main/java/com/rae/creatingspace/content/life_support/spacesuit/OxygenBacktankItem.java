package com.rae.creatingspace.content.life_support.spacesuit;

import com.simibubi.create.foundation.item.LayeredArmorItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Supplier;

public class OxygenBacktankItem extends UpgradableEquipment {
    public static final Type TYPE = Type.CHESTPLATE;
    public static final int BAR_COLOR = 0xEFEFEF;
    private final Supplier<O2BacktankBlockItem> blockItem;

    public OxygenBacktankItem(ArmorMaterial material, Properties properties, ResourceLocation textureLoc, Supplier<O2BacktankBlockItem> placeable) {
        super(material, TYPE, properties, textureLoc);
        this.blockItem = placeable;
    }

    @Override
    public void inventoryTick(ItemStack backtank, Level level, Entity entity, int nbr, boolean isMoving) {
        CompoundTag tag = backtank.getOrCreateTag();

        float o2amount = tag.getFloat("Oxygen");
        float prevO2amount = tag.getFloat("prevOxygen");
        boolean toUpdate = tag.getBoolean("toUpdate");
        if (toUpdate) {
            tag.putFloat("prevOxygen",o2amount);
            tag.putBoolean("toUpdate", false);
        }
        // may be ? -> in the ticking entity logic make the server update the itemstack -> here don't now what is before and after
    }

    @Nullable
    public static OxygenBacktankItem getWornBy(Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return null;
        }
        if (!(livingEntity.getItemBySlot(TYPE.getSlot()).getItem() instanceof OxygenBacktankItem item)) {
            return null;
        }
        return item;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        return blockItem.get()
                .useOn(ctx);
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack p_77616_1_) {
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * Mth.clamp(getRemainingAir(stack) / ((float) OxygenBacktankUtil.maxOxygen(stack)), 0, 1));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    public Block getBlock() {
        return blockItem.get().getBlock();
    }

    public static float getRemainingAir(ItemStack stack) {
        CompoundTag orCreateTag = stack.getOrCreateTag();
        return orCreateTag.getFloat("Oxygen");
    }

    public static class O2BacktankBlockItem extends BlockItem {
        private final Supplier<Item> actualItem;

        public O2BacktankBlockItem(Block block, Supplier<Item> actualItem, Properties properties) {
            super(block, properties);
            this.actualItem = actualItem;
        }

        @Override
        public String getDescriptionId() {
            return this.getOrCreateDescriptionId();
        }

        public Item getActualItem() {
            return actualItem.get();
        }
    }

    public static class Layered extends OxygenBacktankItem implements LayeredArmorItem {
        public Layered(ArmorMaterial material, Properties properties, ResourceLocation textureLoc, Supplier<O2BacktankBlockItem> placeable) {
            super(material, properties, textureLoc, placeable);
        }

        @Override
        public String getArmorTextureLocation(LivingEntity entity, EquipmentSlot slot, ItemStack stack, int layer) {
            return String.format(Locale.ROOT, "%s:textures/models/armor/%s_layer_%d.png", textureLoc.getNamespace(), textureLoc.getPath(), layer);
        }
    }
}
