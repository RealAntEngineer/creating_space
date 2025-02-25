package com.rae.creatingspace.content.rocket.engine;

import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.simibubi.create.content.equipment.goggles.IProxyHoveringInformation;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SuperRocketStructuralBlock extends DirectionalBlock implements IWrenchable, IProxyHoveringInformation {

    public SuperRocketStructuralBlock(Properties p_52591_) {
        super(p_52591_);
    }

    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(FACING));
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState pState) {
        return BlockInit.SMALL_ROCKET_ENGINE.asStack();
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();

        if (stillValid(level, clickedPos, state)) {
            BlockPos masterPos = getMaster(level, clickedPos, state);
            context = new UseOnContext(level, context.getPlayer(), context.getHand(), context.getItemInHand(),
                    new BlockHitResult(context.getClickLocation(), context.getClickedFace(), masterPos,
                            context.isInside()));
            state = level.getBlockState(masterPos);
        }

        return IWrenchable.super.onSneakWrenched(state, context);
    }


    /*@Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (stillValid(pLevel, pPos, pState)) {
            pLevel.destroyBlock(getMaster(pLevel, pPos, pState), true);
        }
    }*/

    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        if (stillValid(pLevel, pPos, pState)) {
            BlockPos masterPos = getMaster(pLevel, pPos, pState);
            pLevel.destroyBlockProgress(masterPos.hashCode(), masterPos, -1);
            if (!pLevel.isClientSide() && pPlayer.isCreative())
                pLevel.destroyBlock(masterPos, false);
        }
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
                                  BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (stillValid(pLevel, pCurrentPos, pState)) {
            BlockPos masterPos = getMaster(pLevel, pCurrentPos, pState);
            if (!pLevel.getBlockTicks()
                    .hasScheduledTick(masterPos, BlockInit.ROCKET_ENGINE.get()))
                pLevel.scheduleTick(masterPos, BlockInit.ROCKET_ENGINE.get(), 1);
            return pState;
        }
        if (!(pLevel instanceof Level level) || level.isClientSide())
            return pState;
        if (!level.getBlockTicks()
                .hasScheduledTick(pCurrentPos, this))
            level.scheduleTick(pCurrentPos, this, 1);
        return pState;
    }

    public static BlockPos getMaster(BlockGetter level, BlockPos pos, BlockState state) {
        //makeSomething to prevent stackOverFlow -> while
        ArrayList<BlockPos> posDiscovered = new ArrayList<>();
        //posDiscovered.add(pos);
        BlockState targetedState;
        BlockPos targetedPos = pos;
        //make it like the oxygen ... and make something to prevent a lo
        while (!posDiscovered.contains(pos) && posDiscovered.size() < 10) {
            targetedState = level.getBlockState(targetedPos);

            if (targetedState.is(BlockInit.SMALL_ENGINE_STRUCTURAL.get())) {
                posDiscovered.add(targetedPos);
            } else if (targetedState.is(BlockInit.SMALL_ROCKET_ENGINE.get())) {
                return targetedPos;
            }
            if (targetedState.hasProperty(FACING)) {
                Direction direction = level.getBlockState(targetedPos).getValue(FACING);

                targetedPos = pos.relative(direction);
            } else {
                return targetedPos;
            }

        }

        return targetedPos;
    }

    public boolean stillValid(BlockGetter level, BlockPos pos, BlockState state) {
        if (!state.is(this))
            return false;

        Direction direction = state.getValue(FACING);
        BlockPos targetedPos = pos.relative(direction);
        BlockState targetedState = level.getBlockState(targetedPos);
        return targetedState.getBlock() instanceof SuperRocketStructuralBlock ||
                targetedState.getBlock() instanceof RocketEngineBlock;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!stillValid(pLevel, pPos, pState)) {
            pLevel.setBlockAndUpdate(pPos, Blocks.AIR.defaultBlockState());
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new RenderProperties());
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2,
                                     LivingEntity entity, int numberOfParticles) {
        return true;
    }

    public static class RenderProperties implements IClientBlockExtensions, MultiPosDestructionHandler {

        @Override
        public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager) {
            return true;
        }

        @Override
        public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
            if (target instanceof BlockHitResult bhr) {
                BlockPos targetPos = bhr.getBlockPos();
                @NonnullType SuperRocketStructuralBlock structuralBlock = BlockInit.ENGINE_STRUCTURAL.get();
                if (structuralBlock.stillValid(level, targetPos, state))
                    manager.crack(SuperRocketStructuralBlock.getMaster(level, targetPos, state), bhr.getDirection());
                return true;
            }
            return IClientBlockExtensions.super.addHitEffects(state, level, target, manager);
        }

        @Override
        @Nullable
        public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
            @NonnullType SuperRocketStructuralBlock structuralBlock = BlockInit.ENGINE_STRUCTURAL.get();
            if (!structuralBlock.stillValid(level, pos, blockState))
                return null;
            HashSet<BlockPos> set = new HashSet<>();
            set.add(SuperRocketStructuralBlock.getMaster(level, pos, blockState));
            return set;
        }
    }

    @Override
    public BlockPos getInformationSource(Level level, BlockPos pos, BlockState state) {
        return stillValid(level, pos, state) ? getMaster(level, pos, state) : pos;
    }

}
