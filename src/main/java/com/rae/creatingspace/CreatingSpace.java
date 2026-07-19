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
import com.rae.creatingspace.init.CSContraptionType;
import com.rae.creatingspace.content.event.IgniteOnPlace;
import com.rae.creatingspace.legacy.utilities.data.MassOfBlockReader;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.slf4j.Logger;

@Mod(CreatingSpace.MODID)
public class CreatingSpace {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MODID = "creatingspace" ;

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item -> {
                return new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE).
                        andThen(TooltipModifier.mapNull(KineticStats.create(item)));
            });

    public CreatingSpace(IEventBus modEventBus, ModContainer modContainer) {
        IEventBus forgeEventBus = NeoForge.EVENT_BUS;
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        modEventBus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
            event.dataPackRegistry(RocketAccessibleDimension.REGISTRY_KEY,RocketAccessibleDimension.CODEC, RocketAccessibleDimension.CODEC,
                    (builder) -> builder.sync(true)
            );
            event.dataPackRegistry(MiscInit.Keys.POWER_PACK_TYPE,PowerPackType.DIRECT_CODEC, PowerPackType.DIRECT_CODEC);
            event.dataPackRegistry(MiscInit.Keys.EXHAUST_PACK_TYPE,ExhaustPackType.DIRECT_CODEC, ExhaustPackType.DIRECT_CODEC);
            event.dataPackRegistry(PropellantTypeInit.Keys.PROPELLANT_TYPE,PropellantType.DIRECT_CODEC, PropellantType.DIRECT_CODEC,
                    (builder) -> builder.sync(true));
            LOGGER.debug("added reload for CS registries");
        });
        /*modEventBus.addListener((NewRegistryEvent event) -> {
            //event.create(new RegistryBuilder<>(RocketAccessibleDimension.REGISTRY_KEY).sync(true));
            //event.create(new RegistryBuilder<>(MiscInit.Keys.POWER_PACK_TYPE));
            //event.create(new RegistryBuilder<>(MiscInit.Keys.EXHAUST_PACK_TYPE));
            event.create(new RegistryBuilder<>(PropellantTypeInit.Keys.PROPELLANT_TYPE));
                });*/
        REGISTRATE.registerEventListeners(modEventBus);

        TagsInit.init();

        DataComponentsInit.register(modEventBus);
        SoundInit.register(modEventBus);
        ItemInit.register();
        BlockInit.register();
        BlockEntityInit.register();
        EntityInit.register();
        FluidInit.register();
        MiscInit.register(modEventBus);
        PropellantTypeInit.register(modEventBus);
        PaintingInit.register(modEventBus);
        RecipeInit.register(modEventBus);
        ParticleTypeInit.register(modEventBus);

        EntityDataSerializersInit.register(modEventBus);
        CreativeModeTabsInit.register(modEventBus);

        CSConfigs.registerConfigs(modLoadingContext,modContainer);

        MenuTypesInit.register();
        PacketInit.register();
        IgniteOnPlace.register();

        CarverInit.register(modEventBus);

        CSContraptionType.register(modEventBus);

        modEventBus.addListener(CreatingSpace::init);
        modEventBus.addListener(EventPriority.HIGHEST, CSDatagen::gatherDataHighPriority);
        modEventBus.addListener(EventPriority.LOWEST, CSDatagen::gatherData);
        forgeEventBus.addListener(CreatingSpace::onAddReloadListeners);
        //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->  CreatingSpaceClient.clientRegister(modEventBus));

    }
    public static void init(final FMLCommonSetupEvent event) {


        event.enqueueWork(() -> {

            //FluidInit.registerFluidInteractions();
            //FluidInit.registerOpenEndedEffect();
        });
    }
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        //datagen, and tag provider
        event.addListener(MassOfBlockReader.MASS_HOLDER);
    }

    public static ResourceLocation resource(String path){
        return ResourceLocation.fromNamespaceAndPath(MODID,path);
    }
}

