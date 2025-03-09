package com.rae.creatingspace.legacy.server.blocks.atmosphere;

import com.rae.creatingspace.init.ingameobject.BlockEntityInit;
import com.rae.creatingspace.legacy.server.blockentities.atmosphere.OxygenBlockEntity;
import com.rae.creatingspace.legacy.server.blockentities.atmosphere.SealerBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;

import java.util.function.Consumer;

public class OxygenBlock extends Block implements IBE<OxygenBlockEntity> {
    public static final BooleanProperty BREATHABLE = BooleanProperty.create("breathable");

    public OxygenBlock(Properties properties) {
        super(properties);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(BREATHABLE, false);

    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(BREATHABLE));
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return Shapes.empty();
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos,
                                boolean pIsMoving) {
        BlockState neighborState = level.getBlockState(neighborPos);
        if (state.getValue(OxygenBlock.BREATHABLE)) {
            notifyMaster(level, pos);

        }

    }

    private void notifyMaster(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof OxygenBlockEntity oxygenBlockEntity){
            BlockPos masterPos = oxygenBlockEntity.getMasterPos();
            if (level.getBlockEntity(masterPos) instanceof SealerBlockEntity sealerBlockEntity ){
                /*sealerBlockEntity.unSealRoom();//be aware this can cause issue -> verifying every 40 ticks the room might be enough
                sealerBlockEntity.resetRemainingTries();
                sealerBlockEntity.setTrying(true);*/
                sealerBlockEntity.oxygenBlockChanged();
            }
            else {
                level.destroyBlock(pos,true);
            }
        }
    }


    @Override
    public Class<OxygenBlockEntity> getBlockEntityClass() {
        return OxygenBlockEntity.class;
    }
    @Override
    public BlockEntityType<? extends OxygenBlockEntity> getBlockEntityType() {
        return BlockEntityInit.OXYGEN.get();
    }
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) {
        consumer.accept(new OxygenBlock.RenderProperties());
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2,
                                     LivingEntity entity, int numberOfParticles) {
        return true;
    }

    public static class RenderProperties implements IClientBlockExtensions {
        @Override
        public boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager) {
            return true;
        }

        @Override
        public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
            return true;
        }
    }
}
