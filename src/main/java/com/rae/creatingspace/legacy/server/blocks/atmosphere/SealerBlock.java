package com.rae.creatingspace.legacy.server.blocks.atmosphere;

import com.rae.creatingspace.legacy.client.gui.screen.SealerScreen;
import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.content.life_support.sealer.RoomPressuriserBlockEntity;
import com.rae.creatingspace.legacy.server.blockentities.atmosphere.SealerBlockEntity;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.gui.ScreenOpener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import static com.rae.creatingspace.init.graphics.ShapesInit.AIR_LIQUEFIER;

public class SealerBlock extends DirectionalAxisKineticBlock implements IBE<RoomPressuriserBlockEntity> {
    public SealerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(AXIS_ALONG_FIRST_COORDINATE,false));
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return AIR_LIQUEFIER.get(state.getValue(FACING));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS_ALONG_FIRST_COORDINATE).add(FACING);
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack held = player.getMainHandItem();
        /*if (held.isEmpty()){
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                    () -> () -> withBlockEntityDo(level, pos, be-> {
                        be.resetRemainingTries();
                        displayScreen(be,player);}));
            return InteractionResult.SUCCESS;
        }*/

        return InteractionResult.PASS;
    }
    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(SealerBlockEntity be, Player player) {
        if (!(player instanceof LocalPlayer))
            return;
        ScreenOpener.open(new SealerScreen(be));
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos pos, BlockState state, boolean isMoving) {
        BlockEntity be = this.getBlockEntity(level,pos);
        if (be instanceof SealerBlockEntity sealerBlockEntity){
            sealerBlockEntity.removeO2inRoom(level);
        }
        super.onRemove(blockState, level, pos, state, isMoving);
    }

    @Override
    public Class<RoomPressuriserBlockEntity> getBlockEntityClass() {
        return RoomPressuriserBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RoomPressuriserBlockEntity> getBlockEntityType() {
        return BlockEntityInit.ROOM_PRESSURIZER.get();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0,pos,$1,blockEntity) -> {
            if(blockEntity instanceof RoomPressuriserBlockEntity sealerBlockEntity) {
                sealerBlockEntity.tick();
            }
        };
    }

}
