package com.rae.creatingspace.mixin.entity.gravity;

import com.rae.creatingspace.content.planets.CSDimensionUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.rae.creatingspace.content.planets.CSDimensionUtil.shouldHandleGravity;

@Mixin(value = FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
    public FallingBlockEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "getDefaultGravity", at = @At("HEAD"), cancellable = true)
    private void getDefaultGravity(CallbackInfoReturnable<Double> cir) {
        if (shouldHandleGravity(level().dimension().location())) {
            cir.setReturnValue(0.04D * CSDimensionUtil.gravity(level().dimension().location()) / 9.81);
        }
    }
}