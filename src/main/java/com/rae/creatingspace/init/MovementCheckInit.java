package com.rae.creatingspace.init;

import com.rae.creatingspace.content.rocket.engine.SuperEngineBlock;
import com.rae.creatingspace.content.rocket.engine.SuperRocketStructuralBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.BigRocketStructuralBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.SmallRocketStructuralBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.engines.BigEngineBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.engines.SmallEngineBlock;
import com.simibubi.create.api.contraption.BlockMovementChecks;

public class MovementCheckInit {
    public static void register() {

        BlockMovementChecks.registerAttachedCheck(
                (state, world, pos, direction) -> {
                    if (state.getBlock() instanceof SmallRocketStructuralBlock) {
                        if (world.getBlockState(pos.relative(direction)).getBlock() instanceof SmallEngineBlock) {
                            return BlockMovementChecks.CheckResult.SUCCESS;
                        }
                    }

                    if (state.getBlock() instanceof SmallEngineBlock) {
                        if (world.getBlockState(pos.relative(direction)).getBlock() instanceof SmallRocketStructuralBlock) {
                            return BlockMovementChecks.CheckResult.SUCCESS;
                        }
                    }

                    if (state.getBlock() instanceof BigRocketStructuralBlock) {
                        if (world.getBlockState(pos.relative(direction)).getBlock() instanceof BigEngineBlock) {
                            return BlockMovementChecks.CheckResult.SUCCESS;
                        }
                    }

                    if (state.getBlock() instanceof BigEngineBlock) {
                        if (world.getBlockState(pos.relative(direction)).getBlock() instanceof BigRocketStructuralBlock) {
                            return BlockMovementChecks.CheckResult.SUCCESS;
                        }
                    }

                    if (state.getBlock() instanceof SuperRocketStructuralBlock) {
                        if (world.getBlockState(pos.relative(direction)).getBlock() instanceof SuperEngineBlock) {
                            return BlockMovementChecks.CheckResult.SUCCESS;
                        }
                    }

                    if (state.getBlock() instanceof SuperEngineBlock) {
                        if (world.getBlockState(pos.relative(direction)).getBlock() instanceof SuperRocketStructuralBlock) {
                            return BlockMovementChecks.CheckResult.SUCCESS;
                        }
                    }

                    return BlockMovementChecks.CheckResult.PASS;
                }
        );
    }
}