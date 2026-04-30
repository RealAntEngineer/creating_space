package com.rae.creatingspace.content.rocket.engine;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.legacy.server.blocks.multiblock.SmallRocketStructuralBlock;
import com.rae.creatingspace.legacy.server.blocks.multiblock.engines.RocketEngineBlock;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.system.NonnullDefault;

import java.util.List;

@NonnullDefault
public class EngineItem extends RocketEngineItem {
    public EngineItem(Block p_40565_, Properties p_40566_) {
        super(p_40565_, p_40566_);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        RocketEngineBlock main    = (RocketEngineBlock) getBlock();
        Level             lvl     = pContext.getLevel();
        Direction         facing  = pContext.getClickedFace();
        BlockPos          mainPos = pContext.getClickedPos().offset(main.getOffset(facing));

        return lvl.getBlockState(mainPos).isAir() && lvl.getBlockState(mainPos.below()).isAir();
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext pContext, BlockState pState) {
        RocketEngineBlock main    = (RocketEngineBlock) getBlock();
        Level             lvl     = pContext.getLevel();
        Direction         facing  = pContext.getClickedFace();
        BlockPos          mainPos = pContext.getClickedPos().offset(main.getOffset(facing));
        BlockState ghostState = BlockInit.ENGINE_STRUCTURAL.getDefaultState()
                .setValue(SmallRocketStructuralBlock.FACING, Direction.UP);
        lvl.setBlock(mainPos, pState, 11);
        lvl.setBlock(mainPos.below(), ghostState, 11);
        Player     player      = pContext.getPlayer();
        ItemStack  itemstack   = pContext.getItemInHand();
        BlockState blockstate1 = lvl.getBlockState(mainPos);
        blockstate1.getBlock().setPlacedBy(lvl, mainPos, blockstate1, player, itemstack);
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, mainPos, itemstack);
        }

        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        CustomData  data = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbt  = new CompoundTag();
        if (data != null) {
            nbt = data.copyTag();
        }
        CompoundTag beTag = nbt.getCompound("blockEntity");
        appendEngineDependentText(tooltipComponents, "", beTag);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public ItemStack getDefaultInstance() {

        int   thrust     = 1000;
        float efficiency = 1f;
        int   mass       = 3000;
        return getItemStackFromInfo(thrust, efficiency, mass, CreatingSpace.resource("methalox"));
    }

    public ItemStack getItemStackFromInfo(int thrust, float efficiency, int mass, ResourceLocation propellantType) {
        ItemStack   defaultInstance = super.getDefaultInstance();
        CustomData  data            = defaultInstance.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbt             = new CompoundTag();
        if (data != null) {
            nbt = data.copyTag();
        }
        CompoundTag beTag = new CompoundTag();
        //TODO maybe do a record to use the DataComponents system
        beTag.putInt("thrust", thrust);
        beTag.putInt("mass", mass);
        beTag.putFloat("efficiency", efficiency);
        try {
            beTag.put("propellantType", ResourceLocation.CODEC.encodeStart(NbtOps.INSTANCE, propellantType).getOrThrow());
        } catch (Exception ignored) {
        }
        nbt.put("blockEntity", beTag);
        defaultInstance.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        return defaultInstance;
    }
}
