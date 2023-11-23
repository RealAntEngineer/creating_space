package com.rae.creatingspace.init.worldgen;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.utilities.data.AccessibilityMatrixReader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

import static com.rae.creatingspace.utilities.data.AccessibilityMatrixReader.translator;

public class DimensionInit {
    public  static final DeferredRegister<Level> CS_DIMENSION_REGISTRY = DeferredRegister.create(
            Registry.DIMENSION_REGISTRY,
            CreatingSpace.MODID);

    public  static final DeferredRegister<DimensionType> CS_DIMENSION_TYPE_REGISTRY = DeferredRegister.create(
            Registry.DIMENSION_TYPE_REGISTRY,
            CreatingSpace.MODID);

    public static final TagKey<DimensionType> IS_ORBIT = CS_DIMENSION_TYPE_REGISTRY.createTagKey("dimension/is_orbit");

    public static final ResourceKey<Level> EARTH_ORBIT_KEY =
            ResourceKey.create(Registry.DIMENSION_REGISTRY,
                    new ResourceLocation(CreatingSpace.MODID,"earth_orbit"));

    public static final ResourceKey<Level> MOON_ORBIT_KEY =
            ResourceKey.create(Registry.DIMENSION_REGISTRY,
                    new ResourceLocation(CreatingSpace.MODID,"moon_orbit"));

    public static final ResourceKey<Level> MOON_KEY =
            ResourceKey.create(Registry.DIMENSION_REGISTRY,
                    new ResourceLocation(CreatingSpace.MODID,"the_moon"));

    public static final ResourceKey<DimensionType> EARTH_ORBIT_TYPE =
            ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY,
                    new ResourceLocation(CreatingSpace.MODID,"earth_orbit"));
    public static final ResourceKey<DimensionType> MOON_ORBIT_TYPE =
            ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY,
                    new ResourceLocation(CreatingSpace.MODID,"moon_orbit"));

    public static final ResourceKey<DimensionType> MOON_TYPE =
            ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY,
                    new ResourceLocation(CreatingSpace.MODID,"the_moon"));
    //make the rocket came from the top rather than hard tp -> for future version
    public static void register(IEventBus bus) {
        CS_DIMENSION_REGISTRY.register(bus);
        System.out.println("Registering Dimension for : "+ CreatingSpace.MODID);
    }




}


