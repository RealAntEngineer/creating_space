package com.rae.creatingspace.content.rocket.engine;

import com.rae.formicapi.multiblock.MBController;
import com.rae.formicapi.multiblock.MBShape;
import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.formicapi.multiblock.MBStructureBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.*;

@NonnullDefault
public class RocketEngineBlock extends MBController implements IBE<RocketEngineBlockEntity.NbtDependent> {
    //use the modular forge blockstate thing
    public static final EnumProperty<Power> POWER_PACK = EnumProperty.create("power_pack",Power.class);
    public static final EnumProperty<Exhaust> EXHAUST_PACK = EnumProperty.create("exhaust", Exhaust.class);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public RocketEngineBlock(Properties properties, DirectionalBlock structure) {
        super(properties, structure);
        this.registerDefaultState(this.defaultBlockState().setValue(EXHAUST_PACK, Exhaust.BELL_NOZZLE).setValue(POWER_PACK, Power.STANDARD).setValue(ACTIVE, Boolean.TRUE));
    }

    @Override
    protected MBShape makeShapes(DirectionalBlock structure) {//this is a bug in the API
        return MBShape.make2x1x1((MBStructureBlock) structure);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EXHAUST_PACK).add(POWER_PACK).add(ACTIVE);
    }
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context))
                .setValue(EXHAUST_PACK, Exhaust.BELL_NOZZLE)
                .setValue(POWER_PACK, Power.STANDARD)
                .setValue(ACTIVE, Boolean.TRUE);
    }


    @Override
    public Class<RocketEngineBlockEntity.NbtDependent> getBlockEntityClass() {
        return RocketEngineBlockEntity.NbtDependent.class;
    }

    @Override
    public BlockEntityType<? extends RocketEngineBlockEntity.NbtDependent> getBlockEntityType() {
        return BlockEntityInit.NBT_DEPENDENT_ENGINE.get();
    }

    @Override
    public void setPlacedBy(@NotNull Level worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity entity, @NotNull ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, entity, stack);
        if (worldIn.isClientSide)
            return;
        withBlockEntityDo(worldIn, pos, be -> {
            be.setFromNbt(stack.getOrCreateTag().getCompound("blockEntity"));
        });
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull BlockState state) {
        Item item = asItem();

        ItemStack stack = new ItemStack(item);
        Optional<RocketEngineBlockEntity.NbtDependent> blockEntityOptional = getBlockEntityOptional(blockGetter, pos);

        CompoundTag tag = stack.getOrCreateTag();
        assert blockEntityOptional.orElse(null) != null;
        CompoundTag beData = blockEntityOptional.orElse(null).saveWithoutMetadata();
        tag.put("blockEntity", beData);
        stack.setTag(tag);
        return stack;
    }


    @Override
    public void onRemove(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState newBlockState, boolean isMoving) {
        super.onRemove(blockState, level, blockPos, newBlockState, isMoving);
        IBE.onRemove(blockState, level, blockPos, newBlockState);
    }

    public enum Power implements StringRepresentable {
        STANDARD,MAGNETIC;
        @Override
        public @NotNull String getSerializedName() {
            return CreateLang.asId(name());
        }
    }
    public enum Exhaust  implements StringRepresentable {
        BELL_NOZZLE, AEROSPIKE;
        @Override
        public @NotNull String getSerializedName() {
            return CreateLang.asId(name());
        }
    }
}
