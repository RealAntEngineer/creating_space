package com.rae.creatingspace.content.life_support.spacesuit;

import com.rae.creatingspace.init.graphics.ShapesInit;
import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.simibubi.create.AllEnchantments;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Optional;

public class OxygenBacktankBlock extends HorizontalDirectionalBlock
	implements IBE<OxygenBacktankBlockEntity>, SimpleWaterloggedBlock {

	public OxygenBacktankBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false)
			: Fluids.EMPTY.defaultFluidState();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED).add(FACING);
		super.createBlockStateDefinition(builder);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		return getBlockEntityOptional(world, pos).map(OxygenBacktankBlockEntity::getComparatorOutput)
			.orElse(0);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState,
		LevelAccessor world, BlockPos pos, BlockPos neighbourPos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) 
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		return state;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidState = context.getLevel()
			.getFluidState(context.getClickedPos());
		return super.getStateForPlacement(context)
				.setValue(FACING, context.getHorizontalDirection()
				.getOpposite()).
				setValue(BlockStateProperties.WATERLOGGED,
			fluidState.getType() == Fluids.WATER);
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState p_60576_) {
		return true;
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		if (worldIn.isClientSide)
			return;
		if (stack == null)
			return;
		withBlockEntityDo(worldIn, pos, be -> {
			be.setCapacityEnchantLevel(stack.getEnchantmentLevel(AllEnchantments.CAPACITY.get()));
			be.setOxygenLevel((int) stack.getOrCreateTag()
				.getFloat("Oxygen"));
			if (stack.isEnchanted())
				be.setEnchantmentTag(stack.getEnchantmentTags());
			if (stack.hasCustomHoverName())
				be.setCustomName(stack.getHoverName());
		});
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
		BlockHitResult hit) {
		if (player == null)
			return InteractionResult.PASS;
		if (player instanceof FakePlayer)
			return InteractionResult.PASS;
		if (player.isShiftKeyDown())
			return InteractionResult.PASS;
		if (player.getMainHandItem()
			.getItem() instanceof BlockItem)
			return InteractionResult.PASS;
		if (!player.getItemBySlot(EquipmentSlot.CHEST)
			.isEmpty())
			return InteractionResult.PASS;
		if (!world.isClientSide) {
			world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, .75f, 1);
			player.setItemSlot(EquipmentSlot.CHEST, getCloneItemStack(world, pos, state));
			world.destroyBlock(pos, false);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos pos, BlockState state) {
		Item item = asItem();
		if (item instanceof OxygenBacktankItem.O2BacktankBlockItem placeable) {
			item = placeable.getActualItem();
		}

		ItemStack stack = new ItemStack(item);
		Optional<OxygenBacktankBlockEntity> blockEntityOptional = getBlockEntityOptional(blockGetter, pos);

		int air = blockEntityOptional.map(OxygenBacktankBlockEntity::getOxygenLevel)
			.orElse(0);
		CompoundTag tag = stack.getOrCreateTag();
		tag.putFloat("Oxygen", air);
		tag.putFloat("prevOxygen",air);

		ListTag enchants = blockEntityOptional.map(OxygenBacktankBlockEntity::getEnchantmentTag)
			.orElse(new ListTag());
		if (!enchants.isEmpty()) {
			ListTag enchantmentTagList = stack.getEnchantmentTags();
			enchantmentTagList.addAll(enchants);
			tag.put("Enchantments", enchantmentTagList);
		}

		Component customName = blockEntityOptional.map(OxygenBacktankBlockEntity::getCustomName)
			.orElse(null);
		if (customName != null)
			stack.setHoverName(customName);
		return stack;
	}

	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
		CollisionContext collisionContext) {
		return ShapesInit.OXYGEN_BACKTANK.get(blockState.getValue(FACING));
	}

	@Override
	public Class<OxygenBacktankBlockEntity> getBlockEntityClass() {
		return OxygenBacktankBlockEntity.class;
	}
	
	@Override
	public BlockEntityType<? extends OxygenBacktankBlockEntity> getBlockEntityType() {
		return BlockEntityInit.OXYGEN_BACKTANK.get();
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

}
