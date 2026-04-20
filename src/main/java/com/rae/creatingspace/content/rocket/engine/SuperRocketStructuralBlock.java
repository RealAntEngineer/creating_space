package com.rae.creatingspace.content.rocket.engine;

import com.rae.formicapi.multiblock.MBStructureBlock;
import com.simibubi.create.api.equipment.goggles.IProxyHoveringInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class SuperRocketStructuralBlock extends MBStructureBlock implements  IProxyHoveringInformation {
    public SuperRocketStructuralBlock(Properties p_52591_) {
        super(p_52591_);
    }
    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        return stillValid(pLevel,pPos,pState)?pLevel.getBlockState(getMaster(pLevel,pPos)).getBlock().getCloneItemStack(pLevel, pPos, pState):ItemStack.EMPTY;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
                                           BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (stillValid(pLevel, pCurrentPos, pState)) {
            BlockPos masterPos = getMaster(pLevel, pCurrentPos);
            Block masterBlock = pLevel.getBlockState(masterPos).getBlock();
            if (!pLevel.getBlockTicks()
                    .hasScheduledTick(masterPos, masterBlock))
                pLevel.scheduleTick(masterPos, masterBlock, 1);
            return pState;
        }
        if (!(pLevel instanceof Level level) || level.isClientSide())
            return pState;
        if (!level.getBlockTicks()
                .hasScheduledTick(pCurrentPos, this))
            level.scheduleTick(pCurrentPos, this, 1);
        return pState;
    }
}
