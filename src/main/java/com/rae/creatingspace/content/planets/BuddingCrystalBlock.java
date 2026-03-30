package com.rae.creatingspace.content.planets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class BuddingCrystalBlock extends AmethystBlock {
    public static final int GROWTH_CHANCE = 5;
    private static final Direction[] DIRECTIONS = Direction.values();

    private final Block smallBud;
    private final Block mediumBud;
    private final Block largeBud;
    private final Block cluster;

    public BuddingCrystalBlock(Properties properties, Block smallBud, Block mediumBud, Block largeBud, Block cluster) {
        super(properties);
        this.smallBud  = smallBud;
        this.mediumBud = mediumBud;
        this.largeBud  = largeBud;
        this.cluster   = cluster;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(GROWTH_CHANCE) != 0) return;

        Direction  direction      = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        BlockPos   neighborPos    = pos.relative(direction);
        BlockState neighborState  = level.getBlockState(neighborPos);
        Block      nextStage      = null;

        if (canClusterGrowAtState(neighborState)) {
            nextStage = smallBud;
        } else if (neighborState.is(smallBud)  && neighborState.getValue(BlockStateProperties.FACING) == direction) {
            nextStage = mediumBud;
        } else if (neighborState.is(mediumBud) && neighborState.getValue(BlockStateProperties.FACING) == direction) {
            nextStage = largeBud;
        } else if (neighborState.is(largeBud)  && neighborState.getValue(BlockStateProperties.FACING) == direction) {
            nextStage = cluster;
        }

        if (nextStage != null) {
            boolean     isWaterlogged = neighborState.getFluidState().getType() == Fluids.WATER;
            BlockState  newState      = nextStage.defaultBlockState()
                    .setValue(BlockStateProperties.FACING,      direction)
                    .setValue(BlockStateProperties.WATERLOGGED, isWaterlogged);
            level.setBlockAndUpdate(neighborPos, newState);
        }
    }

    public static boolean canClusterGrowAtState(BlockState state) {
        return state.isAir()
                || state.is(Blocks.WATER) && state.getFluidState().getAmount() == 8;
    }
}