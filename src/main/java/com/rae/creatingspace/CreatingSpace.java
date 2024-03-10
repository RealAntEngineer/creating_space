package com.rae.creatingspace;

import com.mojang.logging.LogUtils;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.init.CreativeModeTabsInit;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.RecipeInit;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.init.graphics.DimensionEffectInit;
import com.rae.creatingspace.init.graphics.ParticleTypeInit;
import com.rae.creatingspace.init.ingameobject.*;
import com.rae.creatingspace.init.worldgen.CarverInit;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import com.rae.creatingspace.server.contraption.CSContraptionType;
import com.rae.creatingspace.utilities.data.DimensionParameterMapReader;
import com.rae.creatingspace.utilities.data.DimensionTagsReader;
import com.rae.creatingspace.utilities.data.MassOfBlockReader;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CreatingSpace.MODID)

public class CreatingSpace {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "creatingspace" ;

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    static {
        REGISTRATE.setTooltipModifierFactory(item -> {
            return new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE);
        });
    }

    public CreatingSpace() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        REGISTRATE.registerEventListeners(modEventBus);

        TagsInit.init();

        ItemInit.register();
        BlockInit.register();
        BlockEntityInit.register();
        EntityInit.register();
        FluidInit.register();

        RecipeInit.register(modEventBus);
        CreativeModeTabsInit.register(modEventBus);
        ParticleTypeInit.register(modEventBus);

        PaintingInit.register(modEventBus);

        CarverInit.register(modEventBus);

        CSContraptionType.prepare();

        CSConfigs.registerConfigs(modLoadingContext);
        modEventBus.addListener(CreatingSpace::init);
        forgeEventBus.addListener(CreatingSpace::onAddReloadListeners);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->  CreatingSpaceClient.clientRegister(modEventBus));

    }
    public static void init(final FMLCommonSetupEvent event) {
        PacketInit.registerPackets();

        event.enqueueWork(() -> {

            FluidInit.registerFluidInteractions();
            FluidInit.registerOpenEndedEffect();
        });
    }
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        event.addListener(DimensionParameterMapReader.DIMENSION_MAP_HOLDER);
        event.addListener(DimensionTagsReader.DIMENSION_TAGS_HOLDER);
        event.addListener(MassOfBlockReader.MASS_HOLDER);
    }

    public static ResourceLocation resource(String path){
        return new ResourceLocation(MODID,path);
    }
}

