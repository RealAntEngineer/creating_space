package com.rae.creatingspace.server.items.engine;

import com.rae.creatingspace.api.design.PropellantType;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.rae.creatingspace.server.blocks.multiblock.SmallRocketStructuralBlock;
import com.rae.creatingspace.server.blocks.multiblock.engines.RocketEngineBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SuperEngineItem extends RocketEngineItem {
    public SuperEngineItem(Block p_40565_, Properties p_40566_) {
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
        BlockState ghostState = BlockInit.SUPER_ENGINE_STRUCTURAL.getDefaultState()
                .setValue(SmallRocketStructuralBlock.FACING, Direction.UP);
        lvl.setBlock(mainPos, pState, 11);
        lvl.setBlock(mainPos.below(), ghostState, 11);
        Player player = pContext.getPlayer();
        ItemStack itemstack = pContext.getItemInHand();
        BlockState blockstate1 = lvl.getBlockState(mainPos);
        blockstate1.getBlock().setPlacedBy(lvl, mainPos, blockstate1, player, itemstack);
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, mainPos, itemstack);
        }

        return true;
    }

    @Override
    public ItemStack getDefaultInstance() {

        int thrust = 1000;
        float efficiency = 1f;
        PropellantType propellantType = PropellantTypeInit.METHALOX.get();

        return getItemStackFromInfo(thrust, efficiency, propellantType);
    }

    @NotNull
    public ItemStack getItemStackFromInfo(int thrust, float efficiency, PropellantType propellantType) {
        ItemStack defaultInstance = super.getDefaultInstance();
        CompoundTag nbt = defaultInstance.getOrCreateTag();
        CompoundTag beTag = new CompoundTag();

        beTag.putInt("thrust", thrust);
        beTag.putFloat("efficiency", efficiency);
        beTag.put("propellantType", PropellantTypeInit.PROPELLANT_TYPE.get()
                .getCodec().encodeStart(NbtOps.INSTANCE, propellantType).get().orThrow());
        nbt.put("blockEntity", beTag);
        defaultInstance.setTag(nbt);
        return defaultInstance;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        CompoundTag beTag = itemStack.getOrCreateTagElement("blockEntity");
        PropellantType propellantType = PropellantTypeInit.PROPELLANT_TYPE.get()
                .getCodec().parse(NbtOps.INSTANCE, beTag.get("propellantType"))
                .resultOrPartial(s -> {
                }).orElse(PropellantTypeInit.METHALOX.get());
        appendEngineDependentText(components, (int) (propellantType.getMaxISP() * beTag.getFloat("efficiency")), beTag.getInt("thrust"));
        super.appendHoverText(itemStack, level, components, flag);
    }

    @Override
    public void fillItemCategory(CreativeModeTab modeTab, NonNullList<ItemStack> itemStacks) {
        if (this.allowedIn(modeTab)) {
            itemStacks.add(
                    getItemStackFromInfo((int) (50000f * 9.81f), 0.9f, PropellantTypeInit.METALIC_HYDROGEN.get())
            );
        }
    }
}
