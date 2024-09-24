package com.rae.creatingspace.content.fluids.cassing;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.simibubi.create.content.decoration.encasing.EncasedBlock;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

import java.util.function.Supplier;

public class IsolatedFluidPump extends PumpBlock implements EncasedBlock {
    private final Supplier<Block> casing;

    public IsolatedFluidPump(Properties properties, Supplier<Block> casing) {
        super(properties);
        this.casing = casing;
    }

    @Override
    public Block getCasing() {
        return casing.get();
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource r) {
        FluidPropagator.propagateChangedPipe(world, pos, state);
    }

    @Override
    public void handleEncasing(BlockState state, Level level, BlockPos pos, ItemStack heldItem, Player player, InteractionHand hand,
                               BlockHitResult ray) {
        FluidTransportBehaviour.cacheFlows(level, pos);
        level.setBlockAndUpdate(pos,
                transferProperties(state, defaultBlockState()));
        FluidTransportBehaviour.loadFlows(level, pos);
    } //En vraie pas besoin il transfert juste la position non regarde mieux c 6 boolean 1 par direction pour savoir si il y a un tuyau a cette endroit
    //Donc pas besoin vu qu'il va faire exactement la meme chose ici on a pas besoin de plus
    public static BlockState transferProperties(BlockState from, BlockState to) {//ca devrai suffir
        to = to.setValue(FACING, from.getValue(FACING));
        to = to.setValue(BlockStateProperties.WATERLOGGED, from.getValue(BlockStateProperties.WATERLOGGED));
        return to;
    }
    @Override
    public BlockEntityType<? extends PumpBlockEntity> getBlockEntityType() {
        return BlockEntityInit.ISOLATED_PUMP.get();
    }
}
