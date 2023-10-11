package com.rae.creatingspace;

import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.init.PonderInit;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.init.graphics.DimensionEffectInit;
import com.rae.creatingspace.init.ingameobject.*;
import com.rae.creatingspace.init.worldgen.*;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
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

        TagsInit.init();

        ItemInit.register();
        BlockInit.register();
        BlockEntityInit.register();
        EntityInit.register();
        FluidInit.register();

        DimensionInit.register(bus);

        PaintingInit.register(bus);
        ConfiguredFeatureInit.register(bus);
        PlacedFeatureInit.register(bus);
        BiomesInit.register(bus);
        NoiseInit.register(bus);

        PonderInit.register();

        bus.addListener(CreatingSpace::init);

        bus.register(DimensionEffectInit.class);
    }
    public static void init(final FMLCommonSetupEvent event) {
        PacketInit.registerPackets();

        event.enqueueWork(() -> {

            FluidInit.registerFluidInteractions();
            FluidInit.registerOpenEndedEffect();
        });
    }

    public static ResourceLocation resource(String path){
        return new ResourceLocation(MODID,path);
    }
}

