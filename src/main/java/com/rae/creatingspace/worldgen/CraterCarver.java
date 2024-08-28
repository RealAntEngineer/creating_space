package com.rae.creatingspace.worldgen;

import com.google.common.collect.Multiset;
import com.mojang.serialization.Codec;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CraterCarver extends WorldCarver<CraterCarverConfig> {

    public HashMap<Couple<Integer>, Integer> heightMap = new HashMap<>();
    public CraterCarver(Codec codec) {
        super(codec);
    }

    public boolean carve(CarvingContext context, CraterCarverConfig config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> posToBiome, RandomSource random, Aquifer aquiferSampler, ChunkPos pos, CarvingMask carvingMask) {
        //pos = center chunk pos
        int x = random.nextInt(16);
        int z = random.nextInt(16);
        int y = getHeightInInitialChunkIfClose(context, config, chunk, pos, x, z);

        BlockPos craterCenter = pos.getBlockAt(x, y, z);
        BlockPos.MutableBlockPos mutable = craterCenter.mutable();

        double radius = 8 + (random.nextDouble() * (config.maxRadius - config.minRadius));
        if (random.nextBoolean() && radius < (config.minRadius + config.idealRangeOffset)
                || radius > (config.maxRadius - config.idealRangeOffset))
            radius = 8 + (random.nextDouble() * (config.maxRadius - config.minRadius));
        double depthMultiplier = 1 - ((random.nextDouble() - 0.5) * 0.3);
        boolean fresh = random.nextInt(16) == 1;

        //adjust structures position
        if (chunk.hasAnyStructureReferences()){
            for (Map.Entry<Structure, StructureStart> entry:chunk.getAllStarts().entrySet()){
                if (entry.getKey().step().equals(GenerationStep.Decoration.SURFACE_STRUCTURES)){
                    double xDev = Math.abs((chunk.getPos().getBlockX(entry.getValue().getBoundingBox().getCenter().getX())) - craterCenter.getX());
                    double zDev = Math.abs((chunk.getPos().getBlockZ(entry.getValue().getBoundingBox().getCenter().getZ())) - craterCenter.getZ());
                    if (xDev >= 0 && xDev < 32 && zDev >= 0 && zDev < 32) {
                        if (xDev * xDev + zDev * zDev < radius * radius) { //distance to crater and depth
                            xDev /= radius;
                            zDev /= radius;
                            final double sqrtY = xDev * xDev + zDev * zDev;
                            double yDev = sqrtY * sqrtY * 6;
                            double craterDepth = 5 - yDev;
                            craterDepth *= depthMultiplier;
                            BoundingBox boundingBox = entry.getValue().getBoundingBox().moved(0, (int) - craterDepth,0);
                            entry.getKey().adjustBoundingBox(boundingBox);
                        }
                    }
                }
            }
        }


        for (int innerChunkX = 0; innerChunkX < 16; innerChunkX++) { //iterate through positions in chunk
            for (int innerChunkZ = 0; innerChunkZ < 16; innerChunkZ++) {
                double toDig = 0;

                double xDev = Math.abs((chunk.getPos().getBlockX(innerChunkX)) - craterCenter.getX());
                double zDev = Math.abs((chunk.getPos().getBlockZ(innerChunkZ)) - craterCenter.getZ());
                if (xDev >= 0 && xDev < 32 && zDev >= 0 && zDev < 32) {
                    if (xDev * xDev + zDev * zDev < radius * radius) { //distance to crater and depth
                        xDev /= radius;
                        zDev /= radius;
                        final double sqrtY = xDev * xDev + zDev * zDev;
                        double yDev = sqrtY * sqrtY * 6;
                        double craterDepth = 5 - yDev;
                        craterDepth *= depthMultiplier;
                        if (craterDepth > 0.0) {
                            toDig = craterDepth;
                        }
                    }

                    if (toDig >= 1) {
                        toDig++;
                        if (fresh) toDig++; // Dig one more block, because we're not replacing the top with turf
                    }
                    BlockPos.MutableBlockPos copy = new BlockPos.MutableBlockPos();
                    mutable.set(innerChunkX, y, innerChunkZ);
                    if (toDig>0) {
                        while (!chunk.getBlockState(mutable).isAir()) {
                            mutable.move(Direction.UP);
                            toDig++;
                        }
                    }
                    for (int dug = 0; dug < toDig; dug++) {
                        mutable.move(Direction.DOWN);
                            if (!chunk.getBlockState(mutable).isAir() || carvingMask.get(innerChunkX, mutable.getY()/* + 64*/, innerChunkZ) || dug > 0) {
                                if (!carvingMask.get(innerChunkX, mutable.getY()/* + 64*/, innerChunkZ)) {
                                    chunk.setBlockState(mutable, AIR, true);
                                    carvingMask.set(innerChunkX, mutable.getY()/* + 64*/, innerChunkZ);
                                    if (!fresh && dug + 1 >= toDig && !chunk.getBlockState(copy.set(mutable).move(Direction.DOWN, 2)).isAir()) {
                                        context.topMaterial(posToBiome, chunk, mutable, true).ifPresent(blockStates -> chunk.setBlockState(mutable.move(Direction.DOWN), blockStates, true));
                                    }
                                }
                            } else {
                                dug--;
                                //if (toDig>1)
                                toDig--;
                            }

                    }
                }
            }
        }
        return true;
    }

    private int getHeightInInitialChunkIfClose(CarvingContext context, CraterCarverConfig config, ChunkAccess chunk, ChunkPos pos, int x, int z) {
        double density = -1;
        int y = chunk.getMaxBuildHeight();
        Couple<Integer> coor = Couple.create(pos.getBlockX(x),pos.getBlockZ(z));
        if (chunk.getPos().getChessboardDistance(pos)
                <= config.maxRadius/16+1
        ) {
        if (heightMap.containsKey(coor)){
            return heightMap.get(coor);
        }
            while (density < 0f && y > chunk.getMinBuildHeight()) {
                int finalY = y;
                DensityFunction.FunctionContext functionContext =
                new DensityFunction.FunctionContext() {
                    @Override
                    public int blockX() {
                        return pos.getBlockX(x);
                    }

                    @Override
                    public int blockY() {
                        return finalY;
                    }

                    @Override
                    public int blockZ() {
                        return pos.getBlockZ(z);
                    }
                };
                density = context.randomState().router().finalDensity().compute(functionContext);
                if (density < 0f) {
                    y--;
                }
            }
            heightMap.put(coor,y);
        }
        return y;
    }


    @Override
    public boolean isStartChunk(CraterCarverConfig config, RandomSource random) {
        return  random.nextFloat() <= config.probability;
    }
}
