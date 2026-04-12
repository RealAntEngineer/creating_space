package com.rae.creatingspace.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.rae.creatingspace.content.life_support.spacesuit.NetheriteDivingHandler;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {

    @ModifyReturnValue(method = "fireImmune()Z", at = @At("RETURN"))
    public boolean cs$onFireImmune(boolean original) {
        return ((Entity) (Object) this).getPersistentData().getBoolean(NetheriteDivingHandler.FIRE_IMMUNE_KEY) || original;
    }
}
