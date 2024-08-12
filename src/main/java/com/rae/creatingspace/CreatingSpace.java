package com.rae.creatingspace;

import com.mojang.logging.LogUtils;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.init.*;
import com.rae.creatingspace.init.graphics.MenuTypesInit;
import com.rae.creatingspace.init.graphics.ParticleTypeInit;
import com.rae.creatingspace.init.ingameobject.*;
import com.rae.creatingspace.init.worldgen.CarverInit;
import com.rae.creatingspace.saved.UnlockedDesignManager;
import com.rae.creatingspace.server.contraption.CSContraptionType;
import com.rae.creatingspace.server.event.IgniteOnPlace;
import com.rae.creatingspace.utilities.data.MassOfBlockReader;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TooltipHelper;
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
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;

@Mod(CreatingSpace.MODID)
public class CreatingSpace {
    @Deprecated
    public static final Logger OLD_LOGGER = LogUtils.getLogger();
    public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();


    public static final String MODID = "creatingspace" ;

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    public static final UnlockedDesignManager DESIGN_SAVED_DATA = new UnlockedDesignManager();
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
        CarverInit.register(modEventBus);
        EntityDataSerializersInit.register(modEventBus);
        MiscInit.register(modEventBus);

        CSConfigs.registerConfigs(modLoadingContext);

        MenuTypesInit.register();
        PacketInit.registerPackets();
        IgniteOnPlace.register();


        CSContraptionType.prepare();

        modEventBus.addListener(CreatingSpace::init);
        modEventBus.addListener(EventPriority.LOWEST, CSDatagen::gatherData);
        forgeEventBus.addListener(CreatingSpace::onAddReloadListeners);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->  CreatingSpaceClient.clientRegister(modEventBus));

    }
    public static void init(final FMLCommonSetupEvent event) {


        event.enqueueWork(() -> {

            FluidInit.registerFluidInteractions();
            FluidInit.registerOpenEndedEffect();
        });
    }
    public static void onAddReloadListeners(AddReloadListenerEvent event)
    {
        //datagen, and tag provider
        event.addListener(MassOfBlockReader.MASS_HOLDER);
    }

    public static ResourceLocation resource(String path){
        return new ResourceLocation(MODID,path);
    }
}