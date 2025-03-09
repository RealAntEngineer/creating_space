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

@Mixin(value = ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    public ItemEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V", ordinal = 0, shift = At.Shift.AFTER))
    public void gravity(CallbackInfo ci) {
        if (!level().dimension().location().getNamespace().equals("ad_astra")) {
            this.setDeltaMovement(getDeltaMovement().add(0, 0.04D - 0.04D * CSDimensionUtil.gravity(level().dimension().location()) / 9.81, 0));
        }
    }
}
