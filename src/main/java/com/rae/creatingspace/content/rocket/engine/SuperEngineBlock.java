package com.rae.creatingspace.content.rocket.engine;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public class SuperEngineBlock extends RocketEngineBlock implements IBE<RocketEngineBlockEntity.NbtDependent> {


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
            be.setFromNbt(stack.getOrCreateTag().getCompound("blockEntity"));
        });
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos pos, BlockState state) {
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
        super.onRemove(blockState, level, blockPos, blockState1, isMoving);
    }
}
