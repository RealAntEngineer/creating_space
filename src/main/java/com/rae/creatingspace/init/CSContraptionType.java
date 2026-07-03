package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.contraption.entity.RocketContraption;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateRegistries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class CSContraptionType  {
    public  static final DeferredRegister<ContraptionType> CONTRAPTION_REGISTRY = DeferredRegister.create(
            CreateRegistries.CONTRAPTION_TYPE,
            CreatingSpace.MODID);
    public static final RegistryObject<ContraptionType> ROCKET = CONTRAPTION_REGISTRY.register("rocket",  () -> new ContraptionType(RocketContraption::new));

    public static void register(IEventBus modEventBus) {
        CONTRAPTION_REGISTRY.register(modEventBus);
    }
}
