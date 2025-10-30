package com.rae.creatingspace.content.rocket.engine.table;

import com.mojang.serialization.MapCodec;
import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;



public class RocketEngineerTableBlock extends HorizontalDirectionalBlock implements IBE<RocketEngineerTableBlockEntity> {
    public RocketEngineerTableBlock(Properties p_49795_) {
        super(p_49795_);
    }
    public static final MapCodec<HorizontalDirectionalBlock> CODEC = simpleCodec(RocketEngineerTableBlock::new);

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        super.createBlockStateDefinition(builder);
    }
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(FACING,context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult){
        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof RocketEngineerTableBlockEntity be) {
            player.openMenu(be, be::sendToMenu);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public Class<RocketEngineerTableBlockEntity> getBlockEntityClass() {
        return RocketEngineerTableBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RocketEngineerTableBlockEntity> getBlockEntityType() {
        return BlockEntityInit.ENGINEER_TABLE.get();
    }

}
