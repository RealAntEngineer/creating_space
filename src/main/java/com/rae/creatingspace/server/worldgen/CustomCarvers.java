package com.rae.creatingspace.server.worldgen;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.utilities.tags.CustomBlockTags;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.*;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraftforge.registries.DeferredRegister;

public class CustomCarvers {

    public static final DeferredRegister<ConfiguredWorldCarver<?>> CARVER_DEFERRED_REGISTER =
            DeferredRegister.create(Registry.CONFIGURED_CARVER_REGISTRY, CreatingSpace.MODID);
    public static final Holder<ConfiguredWorldCarver<CaveCarverConfiguration>> MOON_CAVE =

            register("moon_cave",
                    WorldCarver.CAVE.configured(
                            new CaveCarverConfiguration(
                                    0.15F,
                                    UniformHeight
                                            .of(
                                                    VerticalAnchor.aboveBottom(8),
                                                    VerticalAnchor.absolute(180)
                                            ),
                                    UniformFloat.of(0.1F, 0.9F),
                                    VerticalAnchor.aboveBottom(8),
                                    CarverDebugSettings
                                            .of(false,
                                                    Blocks.CRIMSON_BUTTON.defaultBlockState()),
                                    Registry.BLOCK.getOrCreateTag(
                                            CustomBlockTags.MOON_CARVER_REPLACEABLE),
                                    UniformFloat.of(0.7F, 1.4F),
                                    UniformFloat.of(0.8F, 1.3F),
                                    UniformFloat.of(-1.0F, -0.4F))));

    private static <WC extends CarverConfiguration> Holder<ConfiguredWorldCarver<WC>> register(String name, ConfiguredWorldCarver<WC> configuredWorldCarver) {
        return BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_CARVER, name, configuredWorldCarver);
    }
}
