package com.rae.creatingspace.content.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;

public class FloatingIsland extends Feature<BlockPileConfiguration> {
   public FloatingIsland(Codec<BlockPileConfiguration> configurationCodec) {
      super(configurationCodec);
   }

   public boolean place(FeaturePlaceContext<BlockPileConfiguration> context) {
      BlockPos blockpos = context.origin();
      WorldGenLevel worldgenlevel = context.level();
      RandomSource randomsource = context.random();
      BlockPileConfiguration blockpileconfiguration = context.config();
      if (blockpos.getY() < worldgenlevel.getMinBuildHeight() + 5) {
         return false;
      } else {
         int i = 2 + randomsource.nextInt(2);
         int j = 2 + randomsource.nextInt(2);

         for(BlockPos blockpos1 : BlockPos.betweenClosed(blockpos.offset(-i, 0, -j), blockpos.offset(i, 1, j))) {
            int k = blockpos.getX() - blockpos1.getX();
            int l = blockpos.getZ() - blockpos1.getZ();
            if ((float)(k * k + l * l) <= randomsource.nextFloat() * 10.0F - randomsource.nextFloat() * 6.0F) {
               this.tryPlaceBlock(worldgenlevel, blockpos1, randomsource, blockpileconfiguration);
            } else if ((double)randomsource.nextFloat() < 0.031D) {
               this.tryPlaceBlock(worldgenlevel, blockpos1, randomsource, blockpileconfiguration);
            }
         }

         return true;
      }
   }

   private boolean mayPlaceOn(LevelAccessor level, BlockPos pos, RandomSource randomSource) {
      BlockPos blockpos = pos.below();
      BlockState blockstate = level.getBlockState(blockpos);
      return true;//blockstate.is(Blocks.DIRT_PATH) ? randomSource.nextBoolean() : blockstate.isFaceSturdy(level, blockpos, Direction.UP);
   }

   private void tryPlaceBlock(LevelAccessor levelAccessor, BlockPos pos, RandomSource random, BlockPileConfiguration blockPileConfiguration) {
      if (/*levelAccessor.isEmptyBlock(pos) &&*/ this.mayPlaceOn(levelAccessor, pos, random)) {
         levelAccessor.setBlock(pos, blockPileConfiguration.stateProvider.getState(random, pos), 4);
      }

   }
}