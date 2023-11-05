package com.rae.creatingspace.configs;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class CSDimAccess extends CSConfigBase{

    public final ConfigList<String> planets =
            l(List.of("creatingspace:the_moon","minecraft:overworld"),"planets",Comments.planets);

    public final ConfigList<String> orbits =
            l(List.of("creatingspace:moon_orbit","creatingspace:earth_orbit"),"orbits",Comments.orbits);

    public final ConfigList<String> no_02 =
            l(List.of("creatingspace:moon_orbit","creatingspace:earth_orbit","creatingspace:the_moon"),"no_O2",Comments.no_02);

    public final ConfigList<String> accessibility_matrix =
            l(List.of("minecraft:overworld-{creatingspace:moon_orbit,creatingspace:earth_orbit}",
                    "creatingspace:earth_orbit-{creatingspace:moon_orbit,minecraft:overworld}",
                    "creatingspace:moon_orbit-{creatingspace:the_moon,creatingspace:earth_orbit}",
                    "creatingspace:the_moon-{creatingspace:moon_orbit}"),
                    "accessibility_matrix",Comments.accessibilityMatrix);


    @Override
    public void registerAll(ForgeConfigSpec.Builder builder) {
        super.registerAll(builder);
        //Stream<ResourceLocation> locations =  Minecraft.getInstance().getConnection().levels().stream().map(ResourceKey::location);
        //System.out.println(locations);
    }


    @Override
    public String getName() {
        return "dimensionAccess";
    }

    private static class Comments {
        static String planets = "which dimensions are a planet";
        static String orbits = "which dimensions are an orbit";
        static String no_02 = "which dimensions doesn't have O2";
        static String accessibilityMatrix = "which dimensions are accessible from where";
    }
}
