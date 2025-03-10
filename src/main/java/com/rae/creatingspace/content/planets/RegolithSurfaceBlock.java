package com.rae.creatingspace.content.planets;

import com.rae.creatingspace.init.ingameobject.BlockInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RegolithSurfaceBlock extends Block {

    public RegolithSurfaceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if (neighbor.equals(pos.relative(Direction.UP))){
            ((Level)level).setBlockAndUpdate(pos, BlockInit.MOON_REGOLITH.getDefaultState());
        }
        super.onNeighborChange(state, level, pos, neighbor);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState blockState, LevelAccessor level, BlockPos pos, BlockPos pos1) {
        if (direction.equals(Direction.UP)){
            ((Level)level).setBlockAndUpdate(pos, BlockInit.MOON_REGOLITH.getDefaultState());

        }
        return super.updateShape(state, direction, blockState, level, pos, pos1);
    }
}
