package com.rae.creatingspace.mixin.entity.gravity;

import com.rae.creatingspace.content.planets.CSDimensionUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = Boat.class)
public abstract class BoatMixin extends Entity {
    public BoatMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @ModifyVariable(method = "floatBoat", at = @At(value = "LOAD"), name = "d1")
    private double modifyGravity(double d1) {
        if (!level.dimension().location().getNamespace().equals("ad_astra")) {
            return d1 * CSDimensionUtil.gravity(level.dimension().location()) / 9.81;
        }
        return d1;
    }
}
