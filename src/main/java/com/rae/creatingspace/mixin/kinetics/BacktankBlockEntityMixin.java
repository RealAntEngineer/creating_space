package com.rae.creatingspace.mixin.kinetics;

import com.rae.creatingspace.init.TagsInit;
import com.simibubi.create.content.equipment.armor.BacktankBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = BacktankBlockEntity.class)
public abstract class BacktankBlockEntityMixin extends KineticBlockEntity implements Nameable {
    private BacktankBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    public void onTick(CallbackInfo ci) {
        Holder<Biome> biome = Objects.requireNonNull(getLevel()).getBiome(getBlockPos());
        if (TagsInit.CustomBiomeTags.NO_OXYGEN.matches(biome)) {
            super.tick();
            ci.cancel();
        }
    }
}