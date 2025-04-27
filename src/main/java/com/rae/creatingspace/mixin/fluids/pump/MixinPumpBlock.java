package com.rae.creatingspace.mixin.fluids.pump;

import com.simibubi.create.content.decoration.encasing.EncasableBlock;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.NonnullDefault;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PumpBlock.class)
@NonnullDefault
@Implements(@Interface(iface = EncasableBlock.class, prefix = "encasable$"))
public abstract class MixinPumpBlock extends Block implements EncasableBlock {

    public MixinPumpBlock(Properties p_49795_) {
        super(p_49795_);
    }

    public boolean canBeEncased(BlockState state, UseOnContext context) {
        // Example implementation, modify as necessary
        return true;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                          BlockHitResult ray) {
        ItemStack heldItem = player.getItemInHand(hand);
        InteractionResult result = tryEncase(state, world, pos, heldItem, player, hand, ray);
        if (result.consumesAction())
            return result;

        return InteractionResult.PASS;
    }

}