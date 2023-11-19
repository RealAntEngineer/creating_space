package com.rae.creatingspace;

import com.rae.creatingspace.init.PonderInit;
import com.rae.creatingspace.init.graphics.ParticleTypeInit;
import net.minecraftforge.eventbus.api.IEventBus;

public class CreatingSpaceClient {
    public static void clientRegister(IEventBus eventBus){
        eventBus.addListener(ParticleTypeInit::registerFactories);
        PonderInit.register();
    }
}
