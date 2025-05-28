package com.rae.creatingspace.init.graphics;

import com.rae.creatingspace.content.planets.CustomDimensionEffects;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;


public class DimensionEffectInit {

    public DimensionEffectInit() {
    }
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        registerDimensionEffects(event,
                DimensionInit.EARTH_ORBIT_TYPE.location(),
                new CustomDimensionEffects.EarthOrbitEffects(), true);
        registerDimensionEffects(event,
                DimensionInit.MOON_ORBIT_TYPE.location(),
                new CustomDimensionEffects.MoonOrbitEffect(), true);
        registerDimensionEffects(event,
                DimensionInit.MOON_TYPE.location(),
                new CustomDimensionEffects.MoonEffect(), true);
        registerDimensionEffects(event,
                DimensionInit.MARS_ORBIT_TYPE.location(),
                new CustomDimensionEffects.MarsOrbitEffects(), true);
        registerDimensionEffects(event,
                DimensionInit.MARS_TYPE.location(),
                new CustomDimensionEffects.MarsEffect(), true);
    }

    private static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event, ResourceLocation location, CustomDimensionEffects.GenericCelestialOrbitEffect effects, boolean renderSun) {
        effects.setRenderSun(renderSun);
        event.register(location, effects);
    }
}
