package com.rae.creatingspace.mixin.entity.gravity;

import com.rae.creatingspace.content.planets.CSDimensionUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.rae.creatingspace.content.planets.CSDimensionUtil.shouldHandleGravity;

@Mixin(value = ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "getDefaultGravity", at = @At("HEAD"), cancellable = true)
    private void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        if (shouldHandleGravity(level().dimension().location())) {
            cir.setReturnValue(0.04D * CSDimensionUtil.gravity(level().dimension().location()) / 9.81);
        }
    }
}
