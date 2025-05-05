package com.rae.creatingspace.content.event;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = CreatingSpace.MODID)
public class IgniteOnPlace {
    static Set<Block> dirtLikeBlocks = new HashSet<>(Arrays.asList(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM, Blocks.ROOTED_DIRT, Blocks.COARSE_DIRT));

    public IgniteOnPlace() {
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof LivingEntity player) {
            BlockState blockState = event.getState();
            if (!CSEventHandler.inO2(player) && player.level().dimension().location().toString().equals("creatingspace:venus")) {
                Level world = (Level) event.getLevel();
                BlockPos pos = event.getPos();
                if (blockState.isFlammable(event.getLevel(), event.getPos(), Direction.UP)) {
                    BlockPos topPos = pos.above();
                    if (world.isEmptyBlock(topPos)) {
                        world.setBlockAndUpdate(topPos, Blocks.FIRE.defaultBlockState());
                    } else {

                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            BlockPos sidePos = pos.relative(direction);
                            if (world.isEmptyBlock(sidePos)) {
                                world.setBlockAndUpdate(sidePos, Blocks.FIRE.defaultBlockState());
                                break;
                            }
                        }
                    }
                }

                if (dirtLikeBlocks.contains(blockState.getBlock())) {
                    world.setBlockAndUpdate(pos, Blocks.SOUL_SAND.defaultBlockState());
                }

                if (blockState.getBlock() instanceof SaplingBlock) {
                    world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                }
            }
        }

    }

    public static void register() {
    }
}
