package com.rae.creatingspace.legacy.server.items;

import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.rocket.engine.RocketEngineItem;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.rae.creatingspace.legacy.server.blocks.multiblock.SmallRocketStructuralBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.engines.RocketEngineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

        return true;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

        appendEngineDependentText(components,PropellantTypeInit.METHALOX.get(), (int) (PropellantTypeInit.METHALOX.get().getMaxISP() * 0.79f), CSConfigs.SERVER.rocketEngine.smallRocketEngineThrust.get());
        super.appendHoverText(itemStack, level, components, flag);
    }
}
