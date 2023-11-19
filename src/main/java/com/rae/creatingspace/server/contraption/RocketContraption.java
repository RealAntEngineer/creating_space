package com.rae.creatingspace.server.contraption;

import com.mojang.logging.LogUtils;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.server.blockentities.RocketEngineBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ContraptionType;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.content.contraptions.render.ContraptionLighter;
import com.simibubi.create.content.contraptions.render.NonStationaryLighter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Queue;
import java.util.Set;

public class RocketContraption extends TranslatingContraption {
    private static final Logger LOGGER = LogUtils.getLogger();
    private int trust = 0;
    private int dryMass = 0;
    private float propellantConsumption = 0;



    public RocketContraption() {

    }
    @Override
    public boolean assemble(Level level, BlockPos pos) throws AssemblyException {

        if (!searchMovedStructure(level, pos, null)) {
            return false;
        }
        startMoving(level);

        return true;
    }

    @Override
    protected void addBlock(BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair) {
        Block blockAdded = pair.getLeft().state.getBlock();
        BlockEntity blockEntityAdded = pair.getRight();
        BlockPos localPos = pos.subtract(anchor);
        if (blockEntityAdded instanceof RocketEngineBlockEntity engineBlockEntity){

            this.trust += engineBlockEntity.getTrust();
            this.propellantConsumption += (float) (engineBlockEntity.getTrust()/(
                    engineBlockEntity.getIsp()* CSConfigs.SERVER.rocketEngine.ISPModifier.get() *9.81));
        }
        if (blockAdded.defaultBlockState() == AllBlocks.FLUID_TANK.getDefaultState()){

            this.dryMass += 20;//look on a json file the density ?
        } else {

            this.dryMass += 1000;
        }

        super.addBlock(pos, pair);
    }

    @Override
    protected boolean moveBlock(Level world, @Nullable Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited) throws AssemblyException {
        return super.moveBlock(world, forcedDirection, frontier, visited);
    }

    @Override //to allow the intial block to be a part of the contraption
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return false;
    }

    @Override
    public ContraptionType getType() {
        return CSContraptionType.ROCKET;
    }


    /*@Override
    public void addBlocksToWorld(Level world, StructureTransform transform) {
        for (StructureTemplate.StructureBlockInfo block : blocks.values()){
            BlockPos targetPos = transform.apply(block.pos);
            BlockState worldState = world.getBlockState(targetPos);

            if (!worldState.isAir()){
                worldState.getBlock().canBeReplaced(worldState, Fluids.WATER.defaultFluidState().getType());
                world.explode()
            }
        }
        super.addBlocksToWorld(world, transform);
    }*/

    @Override
    @OnlyIn(Dist.CLIENT)
    public ContraptionLighter<?> makeLighter() {
        return new NonStationaryLighter<>(this);
    }


    public float getDryMass(){
        return this.dryMass;
    }
    public float getTrust(){
        return this.trust;
    }
    public int getPropellantConsumption(){
        return (int) this.propellantConsumption;
    }

}
