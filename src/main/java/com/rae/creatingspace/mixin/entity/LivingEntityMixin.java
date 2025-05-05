package com.rae.creatingspace.mixin.entity;

import com.rae.creatingspace.content.life_support.INeedOxygen;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements INeedOxygen {
    @Unique
    boolean cS_1_20_1$insideOxygen = false;
    @Override
    public boolean insideOxygenRoom() {
        return cS_1_20_1$insideOxygen;
    }

    @Override
    public void setInsideOxygenRoom(boolean insideOxygenRoom) {
        cS_1_20_1$insideOxygen = insideOxygenRoom;
    }
}
