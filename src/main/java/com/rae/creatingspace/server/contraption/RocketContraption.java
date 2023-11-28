package com.rae.creatingspace.server.contraption;

import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.server.blockentities.RocketEngineBlockEntity;
import com.rae.creatingspace.utilities.CSMassUtil;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ContraptionType;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.content.contraptions.render.ContraptionLighter;
import com.simibubi.create.content.contraptions.render.NonStationaryLighter;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Queue;
import java.util.Set;

public class RocketContraption extends TranslatingContraption {
    private int trust = 0;
    private int dryMass = 0;
    private float propellantConsumption = 0;
    private HashMap<Couple<TagKey<Fluid>>, ConsumptionInfo> theoreticalPerTagFluidConsumption = new HashMap<Couple<TagKey<Fluid>>, ConsumptionInfo>();


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
            float totalPropellantConsumption = (float) (engineBlockEntity.getTrust()/(
                    engineBlockEntity.getIsp()* CSConfigs.SERVER.rocketEngine.ISPModifier.get() *9.81));
            float ratio = engineBlockEntity.getOxFuelRatio();
            TagKey<Fluid> oxidizerTag = engineBlockEntity.getOxidizerTag();
            TagKey<Fluid> fuelTag = engineBlockEntity.getFuelTag();
            Couple<TagKey<Fluid>> combination = Couple.create(oxidizerTag,fuelTag);
            ConsumptionInfo previousCombInfo = new ConsumptionInfo(0,0,0);

            if (this.theoreticalPerTagFluidConsumption.containsKey(combination)){
                previousCombInfo = this.theoreticalPerTagFluidConsumption
                        .get(combination);
            }
            float oxMass = totalPropellantConsumption *(ratio/(ratio+1));
            float fuelMass = totalPropellantConsumption *(1/(ratio+1));

            this.theoreticalPerTagFluidConsumption.put(combination,previousCombInfo.add(oxMass,fuelMass,engineBlockEntity.getTrust()));


        }
        this.dryMass += CSMassUtil.mass(blockAdded.defaultBlockState());

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

    public HashMap<Couple<TagKey<Fluid>>, ConsumptionInfo> getTPTFluidConsumption() {
        return theoreticalPerTagFluidConsumption;
    }

    public record ConsumptionInfo(float oxConsumption, float fuelConsumption, int partialTrust){
        public ConsumptionInfo add(float oxConsumption,float fuelConsumption, int partialTrust){
            return new ConsumptionInfo(this.oxConsumption
                    + oxConsumption,
                    this.fuelConsumption
                            +fuelConsumption,
                    this.partialTrust+partialTrust);
        }
    }
}
