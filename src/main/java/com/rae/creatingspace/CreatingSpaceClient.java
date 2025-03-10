package com.rae.creatingspace;

import com.rae.creatingspace.content.ponders.CSPonderPlugin;
import com.rae.creatingspace.init.graphics.DimensionEffectInit;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.rae.creatingspace.init.graphics.ParticleTypeInit;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.eventbus.api.IEventBus;

public class CreatingSpaceClient {
    public static void clientRegister(IEventBus modEventBus) {
        modEventBus.addListener(ParticleTypeInit::registerFactories);
        modEventBus.register(DimensionEffectInit.class);
        PartialModelInit.init();
        PonderIndex.addPlugin(new CSPonderPlugin());
    }
}
