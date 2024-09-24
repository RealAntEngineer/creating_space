package com.rae.creatingspace.content.recipes.chemical_synthesis;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.Objects;

public class CatalystCarrierBlock extends HorizontalKineticBlock implements IBE<CatalystCarrierBlockEntity> {

    public CatalystCarrierBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext
                && ((EntityCollisionContext) context).getEntity() instanceof Player)
            return AllShapes.CASING_14PX.get(Direction.DOWN);

        return AllShapes.MECHANICAL_PROCESSOR_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        return !AllBlocks.BASIN.has(worldIn.getBlockState(pos.below()));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction prefferedSide = getPreferredHorizontalFacing(context);
        if (prefferedSide != null)
            return defaultBlockState().setValue(HORIZONTAL_FACING, prefferedSide);
        return super.getStateForPlacement(context);
    }

    @Override
    public Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING)
                .getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING)
                .getAxis();
    }

    @Override
    public Class<CatalystCarrierBlockEntity> getBlockEntityClass() {
        return CatalystCarrierBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CatalystCarrierBlockEntity> getBlockEntityType() {
        return BlockEntityInit.CATALYST_CARRIER.get();
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand p_60507_, BlockHitResult p_60508_) {
        ItemStack held = player.getMainHandItem();
        if (!level.isClientSide) {
            if (!held.isEmpty() && (held.getItem() instanceof CatalystItem)) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> withBlockEntityDo(level, pos, be -> be.setCatalyst(held)));
                return InteractionResult.SUCCESS;
            } else if (held.isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, ((CatalystCarrierBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos))).getCatalyst());
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> withBlockEntityDo(level, pos, be -> be.setCatalyst(null)));
            }
        }
        return InteractionResult.PASS;
    }
}
