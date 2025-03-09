package com.rae.creatingspace.mixin.entity.gravity;

import com.rae.creatingspace.content.planets.CSDimensionUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = AbstractMinecart.class)
public abstract class MinecartMixin extends Entity {
    public MinecartMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @ModifyVariable(method = "tick", at = @At(value = "LOAD"), name = "d0")
    private double modifyGravity(double d0) {
        if (!level().dimension().location().getNamespace().equals("ad_astra")) {
            return d0 * CSDimensionUtil.gravity(level().dimension().location()) / 9.81;
        }
        return d0;
    }
}
