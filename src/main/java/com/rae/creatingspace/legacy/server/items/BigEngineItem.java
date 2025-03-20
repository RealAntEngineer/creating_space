package com.rae.creatingspace.legacy.server.items;

import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.rocket.engine.RocketEngineItem;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.rae.creatingspace.legacy.server.blocks.multiblock.BigRocketStructuralBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.engines.RocketEngineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BigEngineItem extends RocketEngineItem {
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
                        Direction ghostFacing = getGhostDirection(x, z, y);

                        BlockState ghostState = BlockInit.BIG_ENGINE_STRUCTURAL.getDefaultState()
                                .setValue(BigRocketStructuralBlock.FACING, ghostFacing);

                        lvl.setBlock(pos, ghostState, 11);
                    }
                }
            }
        }
        return true;
    }

    @NotNull
    private static Direction getGhostDirection(int x, int z, int y) {
        Direction ghostFacing;
        if (x == 0 && z == 0) {
            if (y == -1) {
                ghostFacing = Direction.UP;
            } else {
                ghostFacing = Direction.DOWN;
            }
        } else if (x < 0) {
            ghostFacing = Direction.EAST;
        } else if (x > 0) {
            ghostFacing = Direction.WEST;
        } else if (z > 0) {
            ghostFacing = Direction.NORTH;

        } else {
            ghostFacing = Direction.SOUTH;
        }
        return ghostFacing;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        appendEngineDependentText(components,PropellantTypeInit.METHALOX.get(), (int) (PropellantTypeInit.METHALOX.get().getMaxISP() * 0.79f),
                CSConfigs.SERVER.rocketEngine.bigRocketEngineThrust.get());

        super.appendHoverText(itemStack, level, components, flag);
    }
}
