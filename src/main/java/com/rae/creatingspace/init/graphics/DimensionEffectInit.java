package com.rae.creatingspace.init.graphics;

import com.rae.creatingspace.client.effects.CustomDimensionEffects;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import net.minecraft.resources.ResourceLocation;
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
                new CustomDimensionEffects.MoonEffect(), true);
    }

    private static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event, ResourceLocation location, CustomDimensionEffects.GenericCelestialOrbitEffect effects, boolean renderSun) {
        effects.setRenderSun(renderSun);
        event.register(location, effects);
    }
}
