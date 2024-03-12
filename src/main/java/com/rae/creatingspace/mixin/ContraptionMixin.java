package com.rae.creatingspace.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Queue;
import java.util.Set;

@Mixin(value = Contraption.class)
public class ContraptionMixin {
    //should be injected at line 388
    @Inject(method = "moveBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;below()Lnet/minecraft/core/BlockPos;", shift = At.Shift.BEFORE))
    protected void onMoveBlock(Level world, Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited, CallbackInfoReturnable<Boolean> cir) {

    }

    private void moveRocketEngine(BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {
        BlockPos nextPos = BeltBlock.nextSegmentPosition(state, pos, true);
        BlockPos prevPos = BeltBlock.nextSegmentPosition(state, pos, false);
        if (nextPos != null && !visited.contains(nextPos))
            frontier.add();
        if (prevPos != null && !visited.contains(prevPos))
            frontier.add(prevPos);
    }

}
