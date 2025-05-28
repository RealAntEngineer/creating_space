package com.rae.creatingspace;

import com.rae.creatingspace.content.ponders.CSPonderPlugin;
import com.rae.creatingspace.init.graphics.DimensionEffectInit;
import com.rae.creatingspace.init.graphics.PartialModelInit;
import com.rae.creatingspace.init.graphics.ParticleTypeInit;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = CreatingSpace.MODID, dist = Dist.CLIENT)
public class CreatingSpaceClient {
    public CreatingSpaceClient(IEventBus modEventBus) {
        modEventBus.addListener(ParticleTypeInit::registerFactories);
        modEventBus.register(DimensionEffectInit.class);
        PartialModelInit.init();
        PonderIndex.addPlugin(new CSPonderPlugin());
    }
}
