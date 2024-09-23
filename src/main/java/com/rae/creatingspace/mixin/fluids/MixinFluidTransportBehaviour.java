package com.rae.creatingspace.mixin.fluids;

import com.rae.creatingspace.content.fluids.effect.FreezeEffect;
import com.rae.creatingspace.init.TagsInit;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.PipeConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(FluidTransportBehaviour.class)
public class MixinFluidTransportBehaviour {

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void onTick(CallbackInfo ci) {
        FluidTransportBehaviour behaviour = (FluidTransportBehaviour) (Object) this;
        Level level = behaviour.getWorld();
        BlockPos pos = behaviour.getPos();

        if (level instanceof ServerLevel serverWorld) {
            Collection<PipeConnection> connections = behaviour.interfaces.values();
            for (PipeConnection connection : connections) {
                FluidStack fluidStack = connection.getProvidedFluid();

                // Check if the pipe is surrounded by wool
                if (isEncasedByWool(level, pos)) {
                    continue;  // Skip applying effects if the pipe is encased by wool
                }

                // Access fluid temperature correctly
                int temperature = fluidStack.getFluid().getFluidType().getTemperature(fluidStack);

                if (!fluidStack.isEmpty() && temperature < 100) {
                    FreezeEffect.freezeEntities(serverWorld, pos);
                    FreezeEffect.freezeWaterAndSpawnParticles(serverWorld, pos, serverWorld.getRandom(), true, true);
                    break;
                }
            }
        }
    }

    private boolean isEncasedByWool(Level level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        return blockState.is(TagsInit.CustomBlockTags.ISOLATE.tag);
    }
}
