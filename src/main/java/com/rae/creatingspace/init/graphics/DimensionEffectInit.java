package com.rae.creatingspace.init.graphics;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.planets.AtmosphericPlanetsEffect;
import com.rae.creatingspace.content.planets.SmartPlanetsEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DimensionEffectInit {

    public DimensionEffectInit() {
    }
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(CreatingSpace.resource("mars"),new AtmosphericPlanetsEffect());
        event.register(CreatingSpace.resource("venus"),new AtmosphericPlanetsEffect());
        event.register(CreatingSpace.resource("smart"),new SmartPlanetsEffect());
    }
}