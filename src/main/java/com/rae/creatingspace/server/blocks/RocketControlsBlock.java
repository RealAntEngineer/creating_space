package com.rae.creatingspace.server.blocks;

import com.rae.creatingspace.client.screen.DestinationScreen;
import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.server.blockentities.RocketControlsBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
        ScreenOpener.open(new DestinationScreen(be));
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
    public static final BooleanProperty ASSEMBLE_NEXT_TICK = BooleanProperty.create("assemble_next_tick");;


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

    //item
    /*public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof RocketControlsBlockEntity rocketControlsBlockEntity) {
            if (!level.isClientSide && player.isCreative() && !rocketControlsBlockEntity.noLocalisation()) {
                ItemStack itemstack = BlockItem.byBlock(BlockInit.ROCKET_CONTROLS.get()).getDefaultInstance();
                blockentity.saveToItem(itemstack);


                ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
                itementity.setDefaultPickUpDelay();
                level.addFreshEntity(itementity);
            }
        }

        super.playerWillDestroy(level, pos, state, player);
    }*/

    public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos pos, BlockState blockState) {
        ItemStack itemstack = super.getCloneItemStack(blockGetter, pos, blockState);
        blockGetter.getBlockEntity(pos, BlockEntityInit.CONTROLS.get()).ifPresent((p_187446_) -> {
            p_187446_.saveToItem(itemstack);
        });
        return itemstack;
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

