package com.rae.creatingspace.legacy.server.blocks;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.init.ingameobject.ItemInit;
import com.rae.creatingspace.legacy.server.blockentities.RocketGeneratorBlockEntity;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;


public class RocketGeneratorBlock extends DirectionalKineticBlock  implements IBE<RocketGeneratorBlockEntity> {


    public RocketGeneratorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)//only for the Axis
                .setValue(H_FACING,Direction.NORTH)
                //.setValue(FACE,AttachFace.FLOOR)
                .setValue(CHARGED, Boolean.valueOf(false))
                .setValue(GENERATING, Boolean.valueOf(false)));
    }

    //blockstate
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");
    public static final BooleanProperty GENERATING = BooleanProperty.create("generating");

    public static final DirectionProperty H_FACING = DirectionProperty.create("hfacing", Direction.Plane.HORIZONTAL);
    //public static final EnumProperty<AttachFace> FACE = BlockStateProperties.ATTACH_FACE;


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGED).add(GENERATING).add(H_FACING);//.add(FACE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(H_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {

        return face.getAxis() == Direction.Axis.Y;

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
                level.setBlockAndUpdate(pos,state.setValue(CHARGED,false).setValue(GENERATING,true));
            }
            this.getBlockEntity(level,pos).updateGeneratedRotation();
        }
    }
    @Override
    public Class<RocketGeneratorBlockEntity> getBlockEntityClass() {
        return RocketGeneratorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends RocketGeneratorBlockEntity> getBlockEntityType() {
        return BlockEntityInit.ROCKET_GENERATOR.get();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityInit.ROCKET_GENERATOR.get().create(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : ($0,pos,$1,blockEntity) -> {
            if(blockEntity instanceof RocketGeneratorBlockEntity starterBlockEntity) {
                starterBlockEntity.tick(level,pos,state, (RocketGeneratorBlockEntity) blockEntity);
            }
        };
    }
    public static Couple<Integer> getSpeedRange() {
        return Couple.create(0, 64);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }
}
