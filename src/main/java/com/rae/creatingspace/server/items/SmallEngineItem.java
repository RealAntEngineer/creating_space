package com.rae.creatingspace.server.items;

import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.server.blocks.multiblock.SmallRocketStructuralBlock;
import com.rae.creatingspace.server.blocks.multiblock.engines.RocketEngineBlock;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SmallEngineItem extends RocketEngineItem {
    public SmallEngineItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        RocketEngineBlock main = (RocketEngineBlock) getBlock();
        Level lvl = pContext.getLevel();
        Direction facing = pContext.getClickedFace();
        BlockPos mainPos = pContext.getClickedPos().offset(main.getOffset(facing));

        return lvl.getBlockState(mainPos).isAir() && lvl.getBlockState(mainPos.below()).isAir();
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext pContext, BlockState pState) {
        RocketEngineBlock main = (RocketEngineBlock) getBlock();
        Level lvl = pContext.getLevel();
        Direction facing = pContext.getClickedFace();
        BlockPos mainPos = pContext.getClickedPos().offset(main.getOffset(facing));
        BlockState ghostState = BlockInit.SMALL_ENGINE_STRUCTURAL.getDefaultState()
                .setValue(SmallRocketStructuralBlock.FACING, Direction.UP);
        lvl.setBlock(mainPos,pState,11);
        lvl.setBlock(mainPos.below(),ghostState,11);
        SuperGlueEntity entity = new SuperGlueEntity(lvl, SuperGlueEntity.span(mainPos, mainPos.below()));
        if (!lvl.isClientSide) {
            lvl.addFreshEntity(entity);
        }

        return true;
    }
}
