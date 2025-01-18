package com.rae.creatingspace.server.blocks;

import com.rae.creatingspace.client.gui.screen.RocketAssembleScreen;
import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.server.blockentities.RocketControlsBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public class RocketControlsBlock extends Block implements IBE<RocketControlsBlockEntity>, TooltipModifier {

    public RocketControlsBlock(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack held = player.getMainHandItem();
        if (held.isEmpty()){
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> withBlockEntityDo(level, pos, be -> this.displayScreen(be, player)));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(RocketControlsBlockEntity be, Player player) {
        if (!(player instanceof LocalPlayer))
            return;
        ScreenOpener.open(new RocketAssembleScreen(be));
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)){
            case NORTH -> Shapes.box(0, 0, 0.375, 1, 0.875, 1);
            case SOUTH -> Shapes.box(0, 0, 0, 1, 0.875, 0.625);
            case WEST -> Shapes.box(0.375, 0, 0, 1, 0.875, 1);
            case EAST -> Shapes.box(0, 0, 0, 0.625, 0.875, 1);
            default -> Shapes.box(0, 0, 0.375, 1, 0.875, 1);
        };
    }

    //blockstate

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ASSEMBLE_NEXT_TICK = BooleanProperty.create("assemble_next_tick");


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(ASSEMBLE_NEXT_TICK,false);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(ASSEMBLE_NEXT_TICK);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, entity, stack);

        if (worldIn.isClientSide)
            return;
        withBlockEntityDo(worldIn, pos, be -> {
            be.setInitialPosMap(RocketControlsBlockEntity.getPosMap( stack.getOrCreateTag().getCompound("initialPosMap")));
            if (stack.hasCustomHoverName())
                be.setCustomName(stack.getHoverName());
        });
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos pos, BlockState state) {
        Item item = asItem();

        ItemStack stack = new ItemStack(item);
        Optional<RocketControlsBlockEntity> blockEntityOptional = getBlockEntityOptional(blockGetter, pos);

        CompoundTag tag = stack.getOrCreateTag();
        HashMap<ResourceLocation, BlockPos> blockPosHashMap = blockEntityOptional.map(RocketControlsBlockEntity::getInitialPosMap).orElse(null);
        if(blockPosHashMap!= null){

            tag.put("initialPosMap", RocketControlsBlockEntity.putPosMap(blockPosHashMap));
        }
        Component customName = blockEntityOptional.map(RocketControlsBlockEntity::getCustomName).orElse(null);
        if (customName != null)
            stack.setHoverName(customName);

        stack.setTag(tag);
        return stack;
    }

    //blockEntity

    @Override
    public Class<RocketControlsBlockEntity> getBlockEntityClass() {
        return RocketControlsBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RocketControlsBlockEntity> getBlockEntityType() {
        return BlockEntityInit.CONTROLS.get();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0,pos,$1,blockEntity) -> {
            if(blockEntity instanceof RocketControlsBlockEntity controlsBlock) {
                controlsBlock.tick();
            }
        };
    }

    @Override
    public void modify(ItemTooltipEvent context) {

    }
}

