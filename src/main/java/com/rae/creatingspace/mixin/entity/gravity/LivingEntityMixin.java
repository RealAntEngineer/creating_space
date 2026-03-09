package com.rae.creatingspace.mixin.entity.gravity;

import com.rae.creatingspace.content.planets.CSDimensionUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.rae.creatingspace.content.planets.CSDimensionUtil.shouldHandleGravity;


@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attribute);

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    /*@ModifyVariable(method = "travel", at = @At(value = "LOAD"), name = "d0")
    private double modifyGravity(double d0) {
        if (shouldHandleGravity(level().dimension().location())) {
            return d0 * CSDimensionUtil.gravity(level().dimension().location()) / 9.81;
        }
        return d0;
    }*/


    @Inject(method = "getDefaultGravity", at = @At("HEAD"), cancellable = true)
    private void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        if (shouldHandleGravity(level().dimension().location())) {
            cir.setReturnValue(getAttributeValue(Attributes.GRAVITY) * CSDimensionUtil.gravity(level().dimension().location()) / 9.81);
        }
    }

    @ModifyVariable(method = "calculateFallDamage", at = @At(value = "HEAD"), ordinal = 0, argsOnly = true)
    public float calculateFallDamage(float distance) {
        if (shouldHandleGravity(level().dimension().location())) {
            return (float) (distance * CSDimensionUtil.gravity(level().dimension().location()) / 9.81);
        }
        return distance;
    }
}
