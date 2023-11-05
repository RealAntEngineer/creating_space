package com.rae.creatingspace;

import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.PonderInit;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.init.graphics.DimensionEffectInit;
import com.rae.creatingspace.init.graphics.ParticleTypeInit;
import com.rae.creatingspace.init.ingameobject.*;
import com.rae.creatingspace.init.worldgen.*;
import com.rae.creatingspace.server.contraption.CSContraptionType;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreatingSpace.MODID)

public class CreatingSpace {
    public static final String MODID = "creatingspace" ;

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);


    public CreatingSpace() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        REGISTRATE.registerEventListeners(bus);

        TagsInit.init();

        ItemInit.register();
        BlockInit.register();
        BlockEntityInit.register();
        EntityInit.register();
        FluidInit.register();

        ParticleTypeInit.register(bus);

        DimensionInit.register(bus);

        PaintingInit.register(bus);
        //ConfiguredFeatureInit.register(bus);
        //PlacedFeatureInit.register(bus);
        //BiomesInit.register(bus);
        //NoiseInit.register(bus);

        CSContraptionType.prepare();

        PonderInit.register();

        CSConfigs.registerConfigs(modLoadingContext);

        bus.addListener(CreatingSpace::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> client(bus));

        bus.register(DimensionEffectInit.class);
    }
    public static void init(final FMLCommonSetupEvent event) {
        PacketInit.registerPackets();

        event.enqueueWork(() -> {

            FluidInit.registerFluidInteractions();
            FluidInit.registerOpenEndedEffect();
        });
    }

    public static void gatherData(GatherDataEvent event) {
        //TagGen.datagen();
        DataGenerator gen = event.getGenerator();

        if (event.includeServer()) {

        }
    }


    public static void client(IEventBus eventBus){
        eventBus.addListener(ParticleTypeInit::registerFactories);
    }

    public static ResourceLocation resource(String path){
        return new ResourceLocation(MODID,path);
    }
}

