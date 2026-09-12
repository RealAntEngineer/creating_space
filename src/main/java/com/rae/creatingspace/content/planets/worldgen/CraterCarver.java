package com.rae.creatingspace.content.planets.worldgen;

import com.mojang.serialization.Codec;
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
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

import java.util.HashMap;
import java.util.List;
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
        int y = heightCache(context, config, chunk, pos, x, z);

        BlockPos craterCenter = pos.getBlockAt(x, y, z);
        BlockPos.MutableBlockPos mutable = craterCenter.mutable();

        double radius = 8 + (random.nextDouble() * (config.maxRadius - config.minRadius));
        if (random.nextBoolean() && radius < (config.minRadius + config.idealRangeOffset)
                || radius > (config.maxRadius - config.idealRangeOffset))
            radius = 8 + (random.nextDouble() * (config.maxRadius - config.minRadius));
        double depthMultiplier = 1 - ((random.nextDouble() - 0.5) * 0.3);
        boolean fresh = random.nextInt(16) == 1;

        // Per-column surface heightmap for this chunk, used afterward to re-seat structures
        // on the post-carve terrain instead of guessing from a single center offset.
        int minBuildHeight = chunk.getMinBuildHeight();
        SimpleBitStorage rawData = new SimpleBitStorage(Mth.ceillog2(chunk.getHeight() + 1), 256);

        for (int innerChunkX = 0; innerChunkX < 16; innerChunkX++) { //iterate through positions in chunk
            for (int innerChunkZ = 0; innerChunkZ < 16; innerChunkZ++) {
                double toDig = 0;

                // Seed the pre-carve surface height for this column. This heightmap may not be
                // fully populated yet at carving time, but it's the cheapest estimate available
                // and gets overwritten below with the real post-dig height wherever we actually carve.
                int preCarveSurfaceY = chunk.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, innerChunkX, innerChunkZ);
                rawData.set(innerChunkX + innerChunkZ * 16, Math.max(0, preCarveSurfaceY - minBuildHeight + 1));

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

                    // The original block state of the true surface layer for this column, captured
                    // before we dig it away, so an "old" crater can restore the real ground material
                    // (stone, sand, whatever it actually was) instead of a generic biome top material.
                    BlockState originalTopState = null;

                    if (toDig > 0) {
                        while (!chunk.getBlockState(mutable).isAir()) {
                            mutable.move(Direction.UP);
                            toDig++;
                        }
                        originalTopState = chunk.getBlockState(copy.set(mutable).move(Direction.DOWN));
                    }
                    for (int dug = 0; dug < toDig; dug++) {
                        mutable.move(Direction.DOWN);
                        if (!chunk.getBlockState(mutable).isAir() || carvingMask.get(innerChunkX, mutable.getY(), innerChunkZ) || dug > 0) {
                            if (!carvingMask.get(innerChunkX, mutable.getY(), innerChunkZ)) {
                                chunk.setBlockState(mutable, AIR, true);
                                carvingMask.set(innerChunkX, mutable.getY(), innerChunkZ);
                                if (!fresh && dug + 1 >= toDig && !chunk.getBlockState(copy.set(mutable).move(Direction.DOWN, 2)).isAir()) {
                                    if (originalTopState != null) {
                                        chunk.setBlockState(mutable.move(Direction.DOWN), originalTopState, true);
                                    } else {
                                        context.topMaterial(posToBiome, chunk, mutable, true).ifPresent(blockStates -> chunk.setBlockState(mutable.move(Direction.DOWN), blockStates, true));
                                    }
                                }
                            }
                        } else {
                            dug--;
                            //if (toDig>1)
                            toDig--;
                        }

                    }

                    if (toDig > 0) {
                        // Record the real post-dig surface for this column so the structure
                        // re-seating pass below has an accurate floor to work from.
                        int newSurfaceY = mutable.getY() - 1;
                        rawData.set(innerChunkX + innerChunkZ * 16, Math.max(0, newSurfaceY - minBuildHeight + 1));
                    }
                }
            }
        }

        // Re-seat surface structures on the terrain as it actually ended up after carving,
        // rather than shifting their bounding box by the depth at a single sample point
        // (which didn't account for structures only partially overlapping the crater, or
        // structures whose footprint wasn't centered where we happened to carve).
        Map<Structure, StructureStart> structures = new HashMap<>();
        chunk.getAllStarts().forEach(
                (structure, start) -> {
                    // we only modify surface structures
                    if (start != null && start != StructureStart.INVALID_START && structure.step() == GenerationStep.Decoration.SURFACE_STRUCTURES) {
                        BoundingBox box = start.getBoundingBox();
                        box = structure.terrainAdaptation() != TerrainAdjustment.NONE ? box.inflatedBy(-12) : box;
                        int                  references = start.getReferences();
                        List<StructurePiece> pieces     = start.getPieces();

                        int chunkMinX = chunk.getPos().getMinBlockX();
                        int chunkMaxX = chunk.getPos().getMaxBlockX();
                        int chunkMinZ = chunk.getPos().getMinBlockZ();
                        int chunkMaxZ = chunk.getPos().getMaxBlockZ();

                        int minX = Math.max(box.minX(), chunkMinX);
                        int maxX = Math.min(box.maxX(), chunkMaxX);
                        int minZ = Math.max(box.minZ(), chunkMinZ);
                        int maxZ = Math.min(box.maxZ(), chunkMaxZ);
                        int minY = Math.max(box.minY(), minBuildHeight);
                        for (int sx = minX; sx <= maxX; sx++) {
                            for (int sz = minZ; sz <= maxZ; sz++) {
                                int localX = sx & 15;
                                int localZ = sz & 15;

                                int surfaceY = rawData.get(localX + localZ * 16) + minBuildHeight;
                                if (surfaceY < minY) {
                                    minY = surfaceY;
                                }
                            }
                        }
                        BoundingBox finalBox = box;
                        int finalMinY = minY;
                        pieces.forEach(piece -> piece.move(0, finalMinY - finalBox.minY(), 0));
                        start = new StructureStart(structure, chunk.getPos(), references, new PiecesContainer(pieces));
                        structures.put(structure, start);
                    }
                }
        );
        structures.forEach(chunk::setStartForStructure);

        return true;
    }

    private int heightCache(CarvingContext context, CraterCarverConfig config, ChunkAccess chunk, ChunkPos pos, int x, int z) {
        double density = -1;
        int y = chunk.getMaxBuildHeight();
        Couple<Integer> coor = Couple.create(pos.getBlockX(x),pos.getBlockZ(z));
        if (chunk.getPos().getChessboardDistance(pos) <= config.maxRadius/16+1) {
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