package com.rae.creatingspace.content.worldgen;

import com.mojang.serialization.Codec;
import com.rae.creatingspace.CreatingSpace;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CraterCarver extends WorldCarver<CraterCarverConfig> {

    public HashMap<Couple<Integer>, Integer> heightMap = new HashMap<>();
    //TODO fix the inconsistency bwn the minecraft heightmap and the one used here (targeting first air block vs last solid block)
    public CraterCarver(Codec codec) {
        super(codec);
    }

    public boolean carve(CarvingContext context, CraterCarverConfig config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> posToBiome, RandomSource random, Aquifer aquiferSampler, ChunkPos centerPos, CarvingMask carvingMask) {
        //centerPos = center chunk pos
        int xCenterOffset = random.nextInt(16);//pos
        int zCenterOffset = random.nextInt(16);//pos
        int initialY = getHeightWithCaching(context, centerPos.getBlockX(xCenterOffset), centerPos.getBlockZ(zCenterOffset));


        BlockPos craterCenter = centerPos.getBlockAt(xCenterOffset, initialY, zCenterOffset);
        BlockPos.MutableBlockPos mutable = craterCenter.mutable();

        double radius = 8 + (random.nextDouble() * (config.maxRadius - config.minRadius));

        //X tilt, Z tilt ?
        //we need the height at 4 "corners"
        int northY = getHeightWithCaching(context, (int) (centerPos.getBlockX(xCenterOffset) - radius), centerPos.getBlockZ(zCenterOffset));//negative X
        int southY = getHeightWithCaching(context, (int) (centerPos.getBlockX(xCenterOffset) + radius), centerPos.getBlockZ(zCenterOffset));//positive X
        int westY = getHeightWithCaching(context, centerPos.getBlockX(xCenterOffset), (int) (centerPos.getBlockZ(zCenterOffset) - radius));//negative Z
        int eastY = getHeightWithCaching(context, centerPos.getBlockX(xCenterOffset), (int) (centerPos.getBlockZ(zCenterOffset) + radius));//positive X

        double xTilt = (southY - northY)/radius/2;
        double xStart = ((southY + northY)/2d - initialY)/radius;
        double zTilt = (eastY - westY)/radius/2;
        double zStart = ((westY + eastY)/2d - initialY)/radius;

        double depthMultiplier = (1 - ((random.nextDouble() - 0.5) * 0.3))*(1+Math.min(Math.abs(xTilt)*3 + Math.abs(zTilt)*3, 6));
        boolean fresh = random.nextInt(16) == 1;

        SimpleBitStorage rawData = new SimpleBitStorage(Mth.ceillog2(chunk.getHeight() + 1),256);


        int minBuildHeight = chunk.getMinBuildHeight();
        for (int innerChunkX = 0; innerChunkX < 16; innerChunkX++) { //iterate through positions in chunk
            for (int innerChunkZ = 0; innerChunkZ < 16; innerChunkZ++) {
                double xDev = chunk.getPos().getBlockX(innerChunkX) - craterCenter.getX();
                double zDev = chunk.getPos().getBlockZ(innerChunkZ) - craterCenter.getZ();
                int surfaceY = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, innerChunkX, innerChunkZ);//this one is not initialised at all.

                rawData.set(innerChunkX + innerChunkZ*16,Math.max(0,surfaceY - minBuildHeight+1));

                if (xDev > -32 && xDev < 32 && zDev > -32 && zDev < 32) {
                    //chunk.setBlockState(new BlockPos(innerChunkX,surfaceY, innerChunkZ), config.debugSettings.getLavaState(), false);
                    if (xDev * xDev + zDev * zDev <= radius * radius) { //distance to crater and depth
                        xDev /= radius;
                        zDev /= radius;
                        final double sqrtY = xDev * xDev + zDev * zDev ;//ellipse
                        double yDev = (sqrtY * sqrtY ) * 6 ;//ellipse squared
                        double craterDepth = 5 - yDev ;
                        craterDepth *= depthMultiplier;
                        //if (craterDepth > 0.0) {
                        double toDig = craterDepth - (zDev * zTilt + xDev * xTilt + (xStart + zStart)/2)*radius;
                        //}


                        //if (toDig >= 1) {
                        //    toDig++;
                        if (fresh) toDig++; // Dig one more block, because we're not replacing the top with turf
                        //}
                        BlockPos.MutableBlockPos copy = new BlockPos.MutableBlockPos();


                        mutable.set(innerChunkX, initialY, innerChunkZ);
                        //if (toDig>0) {
                        if (surfaceY - initialY >= 0){
                            toDig += surfaceY - initialY + 1;
                            mutable.move(Direction.UP, surfaceY - initialY + 1);
                        }
                        /*while (!chunk.getBlockState(mutable).isAir()) {
                            mutable.move(Direction.UP);
                            toDig++;
                       }
                         */
                        //}
                        if (toDig > 0) {
                            for (int dug = 0; dug < toDig; dug++) {
                                mutable.move(Direction.DOWN);
                                //carvingMask.get(innerChunkX, mutable.getY() + 64, innerChunkZ);
                                int relativeY = mutable.getY() - minBuildHeight;
                                if (relativeY <=1) break;
                                try {
                                    if (!chunk.getBlockState(mutable).isAir() || carvingMask.get(innerChunkX, relativeY, innerChunkZ) || dug > 0) {
                                        if (!carvingMask.get(innerChunkX, relativeY, innerChunkZ)) {
                                            chunk.setBlockState(mutable, AIR, true);//this never manages to update the hightmap correctly. That's why we do a dirt hack
                                            carvingMask.set(innerChunkX, relativeY, innerChunkZ);
                                            if (!fresh && dug + 1 >= toDig && !chunk.getBlockState(copy.set(mutable).move(Direction.DOWN, 2)).isAir()) {
                                                context.topMaterial(posToBiome, chunk, mutable, true).ifPresent(blockStates -> chunk.setBlockState(mutable.move(Direction.DOWN), blockStates, true));
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    System.out.println("relative Y :"+relativeY + "| real y :"+ mutable.getY()+"| crater depth :"+ (craterDepth - (zDev * zTilt + xDev * xTilt - (xStart + zStart)/2)*radius));
                                    throw e;
                                }
                            }
                            // update the heightmap for the column here
                            int y = mutable.getY() - 1;
                            //
                            if (y  < minBuildHeight)
                                System.out.println("issue with terrain hight at "+ chunk.getPos() + " y = "+ y);
                            rawData.set(innerChunkX + innerChunkZ * 16, Math.max(0, y - minBuildHeight+1));

                        }
                    }
                }
            }
        }
        //chunk.setHeightmap(Heightmap.Types.WORLD_SURFACE,rawData.getRaw());
        //chunk.setHeightmap(Heightmap.Types.WORLD_SURFACE_WG, rawData.getRaw());
        //adjust the bonding box based on the rawData.
        Map<Structure, StructureStart> structures = new HashMap<>();
        chunk.getAllStarts().forEach(
                (structure, start) -> {
                    //we only modify surface structures
                    if (start != null && start != StructureStart.INVALID_START && structure.step() == GenerationStep.Decoration.SURFACE_STRUCTURES) {
                        BoundingBox box = start.getBoundingBox();
                        box = structure.terrainAdaptation() != TerrainAdjustment.NONE ? box.inflatedBy(-12) : box;
                        int references = start.getReferences();
                        List<StructurePiece> pieces =  start.getPieces();

                        int chunkMinX = chunk.getPos().getMinBlockX();
                        int chunkMaxX = chunk.getPos().getMaxBlockX();
                        int chunkMinZ = chunk.getPos().getMinBlockZ();
                        int chunkMaxZ = chunk.getPos().getMaxBlockZ();

                        int minX = Math.max(box.minX(), chunkMinX);
                        int maxX = Math.min(box.maxX(), chunkMaxX);
                        int minZ = Math.max(box.minZ(), chunkMinZ);
                        int maxZ = Math.min(box.maxZ(), chunkMaxZ);
                        int minY = Math.max(box.minY(), minBuildHeight);
                        for (int x = minX; x <= maxX; x++) {
                            for (int z = minZ; z <= maxZ; z++) {
                                int localX = x & 15;
                                int localZ = z & 15;

                                int surfaceY = rawData.get(localX + localZ*16) + minBuildHeight;
                                if (surfaceY < minY){
                                    minY = surfaceY;
                                }
                            }
                        }
                        BoundingBox finalBox = box;
                        int finalMinY = minY;
                        pieces.forEach(piece -> piece.move(0, finalMinY- finalBox.minY(),0));
                        //we move the structure down a bit.
                        start  = new StructureStart(structure, chunk.getPos(), references, new PiecesContainer(pieces));
                        structures.put(structure, start);
                    }
                }
        );
        structures.forEach(chunk::setStartForStructure);
        /*
        chunk.getAllReferences().forEach((structure, longSet) -> {
            StructureStart start = chunk.getStartForStructure(structure);
            if (start != null && start != StructureStart.INVALID_START) {
                BoundingBox box = start.getBoundingBox();
                box = structure.terrainAdaptation() != TerrainAdjustment.NONE ? box.inflatedBy(-12):box;
                // Clamp to this chunk only
                int chunkMinX = chunk.getPos().getMinBlockX();
                int chunkMaxX = chunk.getPos().getMaxBlockX();
                int chunkMinZ = chunk.getPos().getMinBlockZ();
                int chunkMaxZ = chunk.getPos().getMaxBlockZ();

                int minX = Math.max(box.minX(), chunkMinX);
                int maxX = Math.min(box.maxX(), chunkMaxX);
                int minZ = Math.max(box.minZ(), chunkMinZ);
                int maxZ = Math.min(box.maxZ(), chunkMaxZ);

                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        int localX = x & 15;
                        int localZ = z & 15;

                        int surfaceY = rawData.get(localX + localZ*16) + minBuildHeight;
                        for (int y = surfaceY+1; y < box.minY(); y++) {
                            chunk.setBlockState(new BlockPos(x, y, z), config.debugSettings.getBarrierState(), false);
                        }
                    }
                }
            }
        });*/

        return true;
    }

    private int getHeightWithCaching(CarvingContext context, final int blockX, final int blockZ) {
        double density = -1;
        int y = context.getGenDepth() - context.getMinGenY();
        Couple<Integer> coor = Couple.create(blockX, blockZ);
        if (heightMap.containsKey(coor)) {
            return heightMap.get(coor);
        }
        while (density < 0f && y > context.getMinGenY()) {
            int finalY = y;
            DensityFunction.FunctionContext functionContext =
                    new DensityFunction.FunctionContext() {
                        @Override
                        public int blockX() {
                            return blockX;
                        }

                        @Override
                        public int blockY() {
                            return finalY;
                        }

                        @Override
                        public int blockZ() {
                            return blockZ;
                        }
                    };
            density = context.randomState().router().finalDensity().compute(functionContext);
            if (density < 0f) {
                y--;
            }
        }
        heightMap.put(coor, y);
        return y;
    }


    @Override
    public boolean isStartChunk(CraterCarverConfig config, RandomSource random) {
        return  random.nextFloat() <= config.probability;
    }
}
