package com.rae.creatingspace;

import com.rae.creatingspace.init.PonderInit;
import com.rae.creatingspace.init.graphics.DimensionEffectInit;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.rae.creatingspace.init.graphics.ParticleTypeInit;
import net.minecraftforge.eventbus.api.IEventBus;

public class CreatingSpaceClient {
    public static void clientRegister(IEventBus modEventBus) {
        modEventBus.addListener(ParticleTypeInit::registerFactories);
        modEventBus.register(DimensionEffectInit.class);
        PartialModelInit.init();
        PonderInit.register();
    }
}
