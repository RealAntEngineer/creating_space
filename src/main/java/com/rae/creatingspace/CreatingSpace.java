package com.rae.creatingspace;

import com.mojang.logging.LogUtils;
import com.rae.creatingspace.content.rocket.engine.design.ExhaustPackType;
import com.rae.creatingspace.content.rocket.engine.design.PowerPackType;
import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.api.planets.RocketAccessibleDimension;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.datagen.CSDatagen;
import com.rae.creatingspace.init.*;
import com.rae.creatingspace.init.graphics.MenuTypesInit;
import com.rae.creatingspace.init.graphics.ParticleTypeInit;
import com.rae.creatingspace.init.ingameobject.*;
import com.rae.creatingspace.init.worldgen.CarverInit;
import com.rae.creatingspace.init.worldgen.DensityFunctionInit;
import com.rae.creatingspace.init.worldgen.FeatureInit;
import com.rae.creatingspace.init.CSContraptionType;
import com.rae.creatingspace.content.event.IgniteOnPlace;
import com.rae.creatingspace.legacy.utilities.data.MassOfBlockReader;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.slf4j.Logger;

@Mod(CreatingSpace.MODID)
public class CreatingSpace {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "creatingspace" ;

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> {
            return new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE).
                    andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        });
    }

    public CreatingSpace() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modEventBus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
            event.dataPackRegistry(RocketAccessibleDimension.REGISTRY_KEY,RocketAccessibleDimension.CODEC, RocketAccessibleDimension.CODEC);
            event.dataPackRegistry(MiscInit.Keys.POWER_PACK_TYPE,PowerPackType.DIRECT_CODEC, PowerPackType.DIRECT_CODEC);
            event.dataPackRegistry(MiscInit.Keys.EXHAUST_PACK_TYPE,ExhaustPackType.DIRECT_CODEC, ExhaustPackType.DIRECT_CODEC);
            event.dataPackRegistry(PropellantTypeInit.Keys.PROPELLANT_TYPE,PropellantType.DIRECT_CODEC, PropellantType.DIRECT_CODEC);
            System.out.println("added reload for registries");
        });
        REGISTRATE.registerEventListeners(modEventBus);

        TagsInit.init();

        SoundInit.register();
        ItemInit.register();
        BlockInit.register();
        BlockEntityInit.register();
        EntityInit.register();
        FluidInit.register();
        PropellantTypeInit.register(modEventBus);
        PaintingInit.register(modEventBus);
        RecipeInit.register(modEventBus);
        ParticleTypeInit.register(modEventBus);

        EntityDataSerializersInit.register(modEventBus);
        MiscInit.register(modEventBus);
        CreativeModeTabsInit.register(modEventBus);

        CSConfigs.registerConfigs(modLoadingContext);

        MenuTypesInit.register();
        PacketInit.registerPackets();
        IgniteOnPlace.register();

        CarverInit.register(modEventBus);
        DensityFunctionInit.register(modEventBus);

        FeatureInit.register(modEventBus);

        CSContraptionType.register(modEventBus);

        modEventBus.addListener(CreatingSpace::init);
        modEventBus.addListener(EventPriority.LOWEST, CSDatagen::gatherData);
        forgeEventBus.addListener(CreatingSpace::onAddReloadListeners);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->  CreatingSpaceClient.clientRegister(modEventBus,forgeEventBus));



    }
    public static void init(final FMLCommonSetupEvent event) {


        event.enqueueWork(() -> {

            //FluidInit.registerFluidInteractions();
            FluidInit.registerOpenEndedEffect();
        });
    }
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        //datagen, and tag provider
        event.addListener(MassOfBlockReader.MASS_MAP);
    }

    public static ResourceLocation resource(String path){
        return new ResourceLocation(MODID,path);
    }
}

