package com.rae.creatingspace.mixin.recipe;

import com.rae.creatingspace.recipes.IMoreNbtConditions;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SequencedAssemblyRecipe.class)
public class SequencedAssemblyRecipeMixin implements IMoreNbtConditions {
    @Unique
    public boolean cS_1_19_2$keepNbt = false;
    @Unique
    public boolean cS_1_19_2$matchNbt = false;

    public void setKeepNbt(boolean value) {
        cS_1_19_2$keepNbt = value;
    }

    public void setMachNbt(boolean value) {//we can't right now
        cS_1_19_2$matchNbt = value;
    }

    @Override
    public boolean isKeepNbt() {
        return cS_1_19_2$keepNbt;
    }

    @Override
    public boolean isMachNbt() {
        return false;
    }

    /*@ModifyVariable(method = "advance", at = @At(value = "LOAD", ordinal = 0),name = "itemTag")
    private CompoundTag addNbt(CompoundTag value){
        return value;
    }*/
    @Inject(method = "advance", at = @At(value = "RETURN"), cancellable = true, remap = false)
    public void addTagBack(ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        if (cS_1_19_2$keepNbt) {
            ItemStack advancedItem = cir.getReturnValue();
            CompoundTag itemTag = advancedItem.getOrCreateTag();
            itemTag.merge(input.getOrCreateTag());
            advancedItem.setTag(itemTag);
            cir.setReturnValue(advancedItem);
        }
    }
}
