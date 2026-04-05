package com.rae.creatingspace.mixin.recipe;

import com.rae.creatingspace.content.recipes.IMoreNbtConditions;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Objects;

@Mixin(value = SequencedAssemblyRecipe.class)
public class SequencedAssemblyRecipeMixin implements IMoreNbtConditions {
    @Unique
    public ArrayList<String> nbtKeys = new ArrayList<>();
    @Unique
    public ArrayList<String> matchNbtList = new ArrayList<>();

    public void setKeepNbt(ArrayList<String> nbtKeys) {
        this.nbtKeys = nbtKeys;
    }

    @Override
    public ArrayList<String> getKeepNbt() {
        return nbtKeys;
    }

    @Override
    public void setMachNbt(ArrayList<String> machNbtList) {
        matchNbtList = machNbtList;
    }

    @Override
    public ArrayList<String> getMachNbt() {
        return matchNbtList;
    }

    @Override
    public boolean isKeepNbt() {
        return !nbtKeys.isEmpty();
    }

    @Override
    public boolean isMachNbt() {
        return !matchNbtList.isEmpty();
    }

    @Inject(method = "advance", at = @At(value = "RETURN"), cancellable = true, remap = false)
    public void addTagBack(ResourceLocation id, ItemStack input, RandomSource random, CallbackInfoReturnable<ItemStack> cir) {
        if (isKeepNbt()) {
            ItemStack advancedItem = cir.getReturnValue();
            CustomData itemData = advancedItem.get(DataComponents.CUSTOM_DATA);
            CustomData toKeepData = input.get(DataComponents.CUSTOM_DATA);
            if (itemData != null && toKeepData != null) {
                CompoundTag itemTag = itemData.copyTag();
                CompoundTag toKeepTag = toKeepData.copyTag();
                for (String key : nbtKeys) {
                    Tag tag = toKeepTag.get(key);
                    if (tag != null) {
                        itemTag.put(key, Objects.requireNonNull(tag));
                    }
                }
                advancedItem.set(DataComponents.CUSTOM_DATA, CustomData.of(itemTag));
                cir.setReturnValue(advancedItem);
            }
        }
    }
}