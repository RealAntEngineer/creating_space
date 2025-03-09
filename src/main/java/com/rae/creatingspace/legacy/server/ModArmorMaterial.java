package com.rae.creatingspace.legacy.server;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public record ModArmorMaterial(String name, int durability, int[] protection,int enchantability, SoundEvent equipsound, float toughness, float knockBackResistance, Supplier<Ingredient> repairMaterial)
implements ArmorMaterial {
    private static final int[] DURABILITY_PER_SLOT = new int[] {13,15,16,11};

    public int getDurabilityForSlot(EquipmentSlot slot) {
        return DURABILITY_PER_SLOT[slot.getIndex()] * this.durability;
    }

    public int getDefenseForSlot(EquipmentSlot slot) {
        return this.protection[slot.getIndex()];
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return getDurabilityForSlot(type.getSlot());
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return getDefenseForSlot(type.getSlot());
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipsound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }

    @Override
    public String getName() {
        return CreatingSpace.MODID + ":"+ this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockBackResistance;
    }

}
