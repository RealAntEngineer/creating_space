package com.rae.creatingspace.server.blocks;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.rae.creatingspace.server.blockentities.RocketMotorBlockEntity;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;


public class RocketMotorBlock extends DirectionalKineticBlock  implements IBE<RocketMotorBlockEntity> {


    public RocketMotorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)//only for the Axis
                .setValue(H_FACING,Direction.NORTH)
                .setValue(FACE,AttachFace.FLOOR)
                .setValue(CHARGED, Boolean.valueOf(false))
                .setValue(GENERATING, Boolean.valueOf(false)));
    }

    //blockstate
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");
    public static final BooleanProperty GENERATING = BooleanProperty.create("generating");

    public static final DirectionProperty H_FACING = DirectionProperty.create("hfacing", Direction.Plane.HORIZONTAL);
    public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGED).add(GENERATING).add(H_FACING).add(FACE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for(Direction direction : context.getNearestLookingDirections()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                return super.getStateForPlacement(context)
                        .setValue(H_FACING, context.getHorizontalDirection().getOpposite())
                        .setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR);
            } else {
                return super.getStateForPlacement(context)
                        .setValue(H_FACING, context.getHorizontalDirection().getOpposite())
                        .setValue(FACE,  AttachFace.WALL);  }

        }

        return null;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {

        return switch (state.getValue(FACE)){
            case FLOOR, CEILING -> face.getAxis() == Direction.Axis.Y;
            case WALL -> face.getAxis() == state.getValue(H_FACING).getAxis();
        };

    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(!level.isClientSide() && player.getItemInHand(hand).getItem() == ItemInit.STARTER_CHARGE.get() && !state.getValue(CHARGED)) {

            level.setBlock(pos, state.setValue(CHARGED, true), 3);

            if (!player.isCreative()) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
            return InteractionResult.sidedSuccess(!level.isClientSide());

        }

        return super.use(state,level,pos,player,hand,hitResult);

    }


    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {

        if (!level.isClientSide) {
            if (level.hasNeighborSignal(pos) && state.getValue(CHARGED)) {
                level.setBlock(pos,state.setValue(CHARGED,false).setValue(GENERATING,true),3);
            }
            this.getBlockEntity(level,pos).updateGeneratedRotation();
        }
    }
    @Override
    public Class<RocketMotorBlockEntity> getBlockEntityClass() {
        return RocketMotorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RocketMotorBlockEntity> getBlockEntityType() {
        return BlockEntityInit.ROCKET_MOTOR.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityInit.ROCKET_MOTOR.get().create(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0,pos,$1,blockEntity) -> {
            if(blockEntity instanceof RocketMotorBlockEntity starterBlockEntity) {
                starterBlockEntity.tick(level,pos,state, (RocketMotorBlockEntity) blockEntity);
            }
        };
    }
    public static Couple<Integer> getSpeedRange() {
        return Couple.create(0, 256);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return switch (state.getValue(FACE)){
            case WALL -> state.getValue(H_FACING).getAxis();
            case CEILING,FLOOR -> Direction.Axis.Y;
        };
    }
}
