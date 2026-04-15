package com.rae.creatingspace.mixin.fluid;


import com.rae.creatingspace.init.ingameobject.FluidInit;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidInteractionRegistry;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(FluidInteractionRegistry.class)
public class FluidInteractionRegistryMixin {
    @Mutable
    @Shadow(remap = false) @Final private static Map<FluidType, List<FluidInteractionRegistry.InteractionInformation>> INTERACTIONS;

    @Inject(method = "<clinit>", at = @At("TAIL"), cancellable = true)
    private static void redirectFluidInteractionRegistry(CallbackInfo ci) {
        INTERACTIONS = new HashMap<>();
        FluidInit.registerFluidInteractions();
        FluidInteractionRegistry.addInteraction(NeoForgeMod.LAVA_TYPE.value(),
                new FluidInteractionRegistry.InteractionInformation((level, currentPos, relativePos, currentState) ->
                        level.getBlockState(currentPos.below()).is(Blocks.SOUL_SOIL) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
                        Blocks.BASALT.defaultBlockState()));

        ci.cancel();
    }
}
