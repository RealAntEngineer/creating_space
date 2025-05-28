package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.contraption.RocketContraption;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


public class CSContraptionType  {
    public  static final DeferredRegister<ContraptionType> CONTRAPTION_REGISTRY = DeferredRegister.create(
            CreateRegistries.CONTRAPTION_TYPE,
            CreatingSpace.MODID);
    public static final DeferredHolder<ContraptionType, ContraptionType> ROCKET = CONTRAPTION_REGISTRY.register("rocket",  () -> new ContraptionType(RocketContraption::new));

    public static void register(IEventBus modEventBus) {
        CONTRAPTION_REGISTRY.register(modEventBus);
    }
}
