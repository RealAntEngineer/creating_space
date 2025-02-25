package com.rae.creatingspace.content.rocket.engine;

import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.init.ingameobject.PropellantTypeInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
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
    public EngineItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
        RocketEngineBlock main = (RocketEngineBlock) getBlock();
        Level lvl = pContext.getLevel();
        Direction facing = pContext.getClickedFace();
        Vec3i offset = main.getPlaceOffset(facing);//nope this isn't the correct offset to know where to verify the blocks
        BlockPos mainPos = pContext.getClickedPos().offset(offset);
        boolean flag = true;
        Vec3i size = main.getSize(facing);
        for (int x = -offset.getX(); x < size.getX() - offset.getX();x++){
            for (int y = -offset.getY(); y < size.getY() - offset.getY();y++){
                for (int z = -offset.getZ(); z < size.getZ() - offset.getZ();z++){
                    if (!lvl.getBlockState(mainPos.offset(x,y,z)).isAir()){
                        flag = false;
                        break;
                    }
                }
                if (!flag){
                    break;
                }
            }
            if (!flag){
                break;
            }
        }
        return true;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext pContext, BlockState pState) {
        RocketEngineBlock main = (RocketEngineBlock) getBlock();
        Level lvl = pContext.getLevel();
        Direction facing = pContext.getClickedFace();
        BlockPos mainPos = pContext.getClickedPos().offset(main.getPlaceOffset(facing));
        lvl.setBlockAndUpdate(mainPos, main.getStateForPlacement(pContext));

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