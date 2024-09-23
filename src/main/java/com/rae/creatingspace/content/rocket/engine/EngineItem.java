package com.rae.creatingspace.content.rocket.engine;

import com.rae.creatingspace.api.design.PropellantType;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import com.rae.creatingspace.content.rocket.engine.engines.RocketEngineBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

public class EngineItem extends RocketEngineItem {
    public EngineItem(Block p_40565_, Properties p_40566_) {
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
        BlockState ghostState = BlockInit.ENGINE_STRUCTURAL.getDefaultState()
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
        int mass = 3000;
        return getItemStackFromInfo(thrust, efficiency, mass, PropellantTypeInit.METHALOX.getId());
    }

    @NotNull
    public ItemStack getItemStackFromInfo(int thrust, float efficiency, int mass, ResourceLocation propellantType) {
        ItemStack defaultInstance = super.getDefaultInstance();
        CompoundTag nbt = defaultInstance.getOrCreateTag();
        CompoundTag beTag = new CompoundTag();

        beTag.putInt("thrust", thrust);
        beTag.putInt("mass", mass);
        beTag.putFloat("efficiency", efficiency);
        beTag.put("propellantType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, propellantType).get().orThrow());
        nbt.put("blockEntity", beTag);
        defaultInstance.setTag(nbt);
        return defaultInstance;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        CompoundTag beTag = itemStack.getOrCreateTagElement("blockEntity");
        try {
            PropellantType propellantType = PropellantTypeInit.getSyncedPropellantRegistry().getOptional(
                    ResourceLocation.CODEC.parse(NbtOps.INSTANCE, beTag.get("propellantType"))
                            .resultOrPartial(s -> {
                            }).orElse(PropellantTypeInit.METHALOX.getId())).orElseThrow();
            appendEngineDependentText(components, propellantType, (int) (propellantType.getMaxISP() * beTag.getFloat("efficiency")), beTag.getInt("thrust"));
        } catch (Exception ignored){

        }
        super.appendHoverText(itemStack, level, components, flag);
    }

    @Override
    public void fillItemCategory(CreativeModeTab modeTab, NonNullList<ItemStack> itemStacks) {
        if (this.allowedIn(modeTab)) {
            itemStacks.add(
                    getItemStackFromInfo((int) (50000f * 9.81f), 0.9f, 1000, PropellantTypeInit.LH2LOX.getId())
            );
        }
    }
}
