package com.rae.creatingspace.content.rocket.engine;

import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.formicapi.multiblock.MBStructureBlock;
import com.simibubi.create.api.equipment.goggles.IProxyHoveringInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import com.tterrag.registrate.util.nullness.NonnullType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
@NonnullDefault
public class SuperRocketStructuralBlock extends MBStructureBlock implements  IProxyHoveringInformation {
    public SuperRocketStructuralBlock(Properties p_52591_) {
        super(p_52591_);
    }
    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        return stillValid(pLevel,pPos,pState)?pLevel.getBlockState(getMaster(pLevel,pPos,pState)).getBlock().getCloneItemStack(pLevel, pPos, pState):ItemStack.EMPTY;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
                                           BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (stillValid(pLevel, pCurrentPos, pState)) {
            BlockPos masterPos = getMaster(pLevel, pCurrentPos, pState);
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
