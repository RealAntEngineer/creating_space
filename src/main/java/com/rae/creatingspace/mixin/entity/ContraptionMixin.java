package com.rae.creatingspace.mixin.entity;

import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.legacy.server.blocks.multiblock.BigRocketStructuralBlock;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Queue;
import java.util.Set;

@Mixin(value = Contraption.class)
public class ContraptionMixin {
    @Unique
    private BlockPos local$Pos;// used for capturing local variable in moveBlock methode

    //should be injected at line 388
    @Inject(method = {"moveBlock"}, at = @At(value = "HEAD"), remap = false)
    protected void gettingPos(Level world, Direction forcedDirection, @NotNull Queue<BlockPos> frontier, Set<BlockPos> visited, CallbackInfoReturnable<Boolean> cir) {
        local$Pos = frontier.peek();
    }

    @Inject(method = {"moveBlock"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;below()Lnet/minecraft/core/BlockPos;", shift = At.Shift.BEFORE))
    protected void onMoveBlock(@NotNull Level world, Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = world.getBlockState(local$Pos);
        if (state.is(BlockInit.BIG_ROCKET_ENGINE.get())) {
            moveBigRocketEngine(local$Pos, frontier, visited, state);
        } else if (state.is(BlockInit.BIG_ENGINE_STRUCTURAL.get())) {
            BlockPos masterPos = BigRocketStructuralBlock.getMaster(world, local$Pos, state);
            frontier.add(masterPos);
            moveBigRocketEngine(masterPos, frontier, visited, world.getBlockState(masterPos));
        }
        if (state.is(BlockInit.SMALL_ROCKET_ENGINE.get()) || state.is(BlockInit.ROCKET_ENGINE.get())) {
            BlockPos nextPos = local$Pos.below();
            if (!visited.contains(nextPos)) {
                frontier.add(nextPos);
            }
        } else if (state.is(BlockInit.SMALL_ENGINE_STRUCTURAL.get()) || state.is(BlockInit.ENGINE_STRUCTURAL.get())) {
            BlockPos nextPos = local$Pos.above();
            if (!visited.contains(nextPos)) {
                frontier.add(nextPos);
            }
        }
    }

    private void moveBigRocketEngine(BlockPos pos, Queue<BlockPos> frontier, Set<BlockPos> visited, BlockState state) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos nextPos = pos.offset(x, y, z);
                    if (!visited.contains(nextPos)) {
                        frontier.add(nextPos);
                    }
                }
            }
        }
    }
}
