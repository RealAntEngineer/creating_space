package com.rae.creatingspace.mixin.kinetics;

import com.rae.creatingspace.utilities.CSDimensionUtil;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BacktankBlockEntity.class, priority = 900)
public abstract class BacktankBlockEntityMixin extends KineticBlockEntity implements Nameable {
    private BacktankBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onTick(CallbackInfo ci) {
        if (!CSDimensionUtil.hasO2Atmosphere(getLevel().dimension())) {
            super.tick();
            ci.cancel();
        }
    }
}