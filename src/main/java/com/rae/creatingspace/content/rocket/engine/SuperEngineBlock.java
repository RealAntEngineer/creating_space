package com.rae.creatingspace.content.rocket.engine;

import com.mojang.serialization.MapCodec;
import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.rae.creatingspace.legacy.server.blocks.multiblock.engines.RocketEngineBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.Objects;
import java.util.Optional;

@NonnullDefault
public class SuperEngineBlock extends RocketEngineBlock implements IBE<RocketEngineBlockEntity.NbtDependent> {
    static final MapCodec<SuperEngineBlock> CODEC = simpleCodec(SuperEngineBlock::new);
    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public SuperEngineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Vec3i getOffset(Direction facing) {
        return switch (facing) {
            case DOWN -> new Vec3i(0, 0, 0);
            default -> new Vec3i(0, 1, 0);
        };
    }

    @Override
    public Vec3i getSize(Direction facing) {
        return new Vec3i(1, 2, 1);
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
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, entity, stack);

        if (worldIn.isClientSide)
            return;
        withBlockEntityDo(worldIn, pos, be -> {
            be.setFromNbt(Objects.requireNonNull(stack.get(DataComponents.CUSTOM_DATA)).copyTag().getCompound("blockEntity"), worldIn.registryAccess());
        });
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        Item item = asItem();

        ItemStack stack = new ItemStack(item);
        RocketEngineBlockEntity.NbtDependent be = getBlockEntity(level, pos);

        if (be != null) {
            CustomData  data = stack.get(DataComponents.CUSTOM_DATA);
            CompoundTag tag  = new CompoundTag();
            if (data != null) tag = data.copyTag();
            CompoundTag beData = be.saveWithoutMetadata(level.registryAccess());
            tag.put("blockEntity", beData);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return stack;
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        Direction targetSide = Direction.DOWN;
        BlockPos structurePos = pPos.relative(targetSide);
        BlockState occupiedState = pLevel.getBlockState(structurePos);
        BlockState requiredStructure = BlockInit.ENGINE_STRUCTURAL.getDefaultState()
                .setValue(SuperRocketStructuralBlock.FACING, targetSide.getOpposite());
        pLevel.setBlockAndUpdate(structurePos, requiredStructure);

        //make the same for big engine block

    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState1, boolean isMoving) {
        IBE.onRemove(blockState, level, blockPos, blockState1);
        super.onRemove(blockState, level, blockPos, blockState1, isMoving);
    }
}
