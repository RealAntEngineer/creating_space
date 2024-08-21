package com.rae.creatingspace.mixin.recipe;

import com.rae.creatingspace.recipes.IMoreNbtConditions;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemApplicationRecipe.class)
public class ItemApplicationMixin {

    @Inject(method = "matches(Lnet/minecraftforge/items/wrapper/RecipeWrapper;Lnet/minecraft/world/level/Level;)Z", at = @At(value = "RETURN"), cancellable = true, remap = false)
    public void addAdditionalLogic(RecipeWrapper inv, Level level, CallbackInfoReturnable<Boolean> cir) {
        if ((this instanceof IMoreNbtConditions conditions) && conditions.isMachNbt()) {
            CompoundTag inputTag = inv.getItem(0).getOrCreateTag();
            CompoundTag outputTag = inv.getItem(1).getOrCreateTag();
            cir.setReturnValue(inputTag.equals(outputTag));
        }
    }
}
