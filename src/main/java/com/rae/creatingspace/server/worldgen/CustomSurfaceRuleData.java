package com.rae.creatingspace.server.worldgen;

import com.rae.creatingspace.init.ingameobject.BlockInit;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class CustomSurfaceRuleData {
    private static final SurfaceRules.RuleSource AIR = makeStateRule(Blocks.AIR);
    private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);
    private static final SurfaceRules.RuleSource MOON_STONE = makeStateRule(BlockInit.MOON_STONE.get());
    private static final SurfaceRules.RuleSource MOON_REGOLITH = makeStateRule(BlockInit.MOON_REGOLITH.get());
    private static final SurfaceRules.RuleSource MOON_SURFACE_REGOLITH = makeStateRule(BlockInit.MOON_SURFACE_REGOLITH.get());
    private static final SurfaceRules.RuleSource PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
    private static final SurfaceRules.RuleSource ICE = makeStateRule(Blocks.ICE);
    private static final SurfaceRules.RuleSource WATER = makeStateRule(Blocks.WATER);

    //make pack of ice on craters -> management of temperature later ?
    private static SurfaceRules.RuleSource makeStateRule(Block p_194811_) {
        return SurfaceRules.state(p_194811_.defaultBlockState());
    }

    public static SurfaceRules.RuleSource moon(){

        return SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, MOON_SURFACE_REGOLITH), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, MOON_REGOLITH), MOON_STONE));
    }

    private static SurfaceRules.ConditionSource surfaceNoiseAbove(double p_194809_) {
        return SurfaceRules.noiseCondition(Noises.SURFACE, p_194809_ / 8.25D, Double.MAX_VALUE);
    }
}
