package com.rae.creatingspace.mixin.entity;

import com.rae.creatingspace.content.planets.CSDimensionUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class)
abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "aiStep", at = @At(value = "HEAD"))
    private void flying(CallbackInfo ci) {
        if (!this.onGround() && CSDimensionUtil.gravity(level().dimension().location()) == 0) {
            float d0 = 0;
            if (jumping) {
                d0 = 0.01f;
            } else if (this.isShiftKeyDown()) {
                d0 = -0.01f;
            }
            Vec3 vec3 = this.getDeltaMovement();
            this.setDeltaMovement(vec3.x, vec3.y + d0, vec3.z);
        }
    }
}
