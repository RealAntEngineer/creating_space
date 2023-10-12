package com.rae.creatingspace;

import com.rae.creatingspace.datagen.DamageTypeDataProvider;
import com.rae.creatingspace.datagen.DamageTypeTagGen;
import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.PonderInit;
import com.rae.creatingspace.init.graphics.DimensionEffectInit;
import com.rae.creatingspace.init.ingameobject.*;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.TagGen;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreatingSpace.MODID)

public class CreatingSpace {
    public static final String MODID = "creatingspace" ;

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);


    public CreatingSpace() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        REGISTRATE.registerEventListeners(bus);

        ItemInit.register();
        BlockInit.register();
        BlockEntityInit.register();
        EntityInit.register();
        FluidInit.register();

        DimensionInit.register(bus);

        PaintingInit.register(bus);

        PonderInit.register();

        bus.addListener(CreatingSpace::init);
        bus.addListener(EventPriority.LOW, CreatingSpace::gatherData);


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
        PackOutput output = gen.getPackOutput();

        if (event.includeServer()) {

            gen.addProvider(true, DamageTypeDataProvider.makeFactory(event.getLookupProvider()));
            gen.addProvider(true,
                    new DamageTypeTagGen(output, event.getLookupProvider(), event.getExistingFileHelper()));
        }
    }

    public static ResourceLocation resource(String path){
        return new ResourceLocation(MODID,path);
    }
}

