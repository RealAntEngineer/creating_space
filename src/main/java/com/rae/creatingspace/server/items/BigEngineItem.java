package com.rae.creatingspace.server.items;

import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.server.blocks.multiblock.BigRocketStructuralBlock;
import com.rae.creatingspace.server.blocks.multiblock.engines.RocketEngineBlock;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BigEngineItem extends RocketEngineItem{
    public BigEngineItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }
    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        RocketEngineBlock part = (RocketEngineBlock) getBlock();
        Level level = pContext.getLevel();
        Direction facing = pContext.getClickedFace();
        Vec3i offset = part.getOffset(facing);
        BlockPos place = pContext.getClickedPos().offset(offset).relative(facing);
        Vec3i size = part.getSize(facing);

        for(int x = -1;x<size.getX()-1;x++) {
            for(int y = -1;y<size.getY()-1;y++) {
                for(int z = -1;z<size.getZ()-1;z++) {
                    BlockPos pos = place.offset(x, y, z);
                    if(pContext.getLevel().isOutsideBuildHeight(pos))return false;
                    BlockState replacedBlock = level.getBlockState(pos);
                    if (!replacedBlock.isAir()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext pContext, BlockState pState) {
        RocketEngineBlock part = (RocketEngineBlock) getBlock();
        Level lvl = pContext.getLevel();
        Direction facing = pContext.getClickedFace();
        Vec3i offset = part.getOffset(facing);
        BlockPos place = pContext.getClickedPos().offset(offset).relative(facing);
        Vec3i size = part.getSize(facing);

        for(int x = -1; x<size.getX()- 1; x++) {
            for (int y = -1; y < size.getY() - 1; y++) {
                for (int z = -1; z < size.getZ() - 1; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        lvl.setBlock(place, pState, 11);
                    } else {
                        BlockPos pos = place.offset(x, y, z);
                        Direction ghostFacing;
                        if (x == 0 && z == 0) {
                            if (y == -1) {
                                ghostFacing = Direction.UP;
                            } else {
                                ghostFacing = Direction.DOWN;
                            }
                        }
                        else if (x<0){
                            ghostFacing = Direction.EAST;
                        }
                        else if (x>0){
                            ghostFacing = Direction.WEST;
                        }
                        else if (z>0){
                            ghostFacing = Direction.NORTH;

                        }
                        else {
                            ghostFacing = Direction.SOUTH;
                        }

                        BlockState ghostState = BlockInit.BIG_ENGINE_STRUCTURAL.getDefaultState()
                                    .setValue(BigRocketStructuralBlock.FACING, ghostFacing);

                        lvl.setBlock(pos,ghostState,11);
                    }
                }
            }
        }
        SuperGlueEntity entity = new SuperGlueEntity(lvl, SuperGlueEntity.span(place.offset(-1,-1,-1),place.offset(1,1,1)));
        if (!lvl.isClientSide) {
            lvl.addFreshEntity(entity);
        }
        return true;
    }
}
