package com.rae.creatingspace.server.blocks;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.server.blockentities.RocketEngineerTableBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class RocketEngineerTableBlock extends Block implements IBE<RocketEngineerTableBlockEntity> {
    public RocketEngineerTableBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit) {
        if (worldIn.isClientSide)
            return InteractionResult.SUCCESS;
        withBlockEntityDo(worldIn, pos,
                be -> NetworkHooks.openScreen((ServerPlayer) player, be, be::sendToMenu));
        return InteractionResult.SUCCESS;
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
