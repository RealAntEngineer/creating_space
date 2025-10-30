package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.init.TagsInit;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;


import static com.rae.creatingspace.content.event.DataEventHandler.getSideAwareRegistry;

public class PropellantTypeInit {
    //TODO use datapackRegitry(Codec,Codec)
    public static final DeferredRegister<PropellantType> DEFERRED_PROPELLANT_TYPE =
            DeferredRegister.create(Keys.PROPELLANT_TYPE, CreatingSpace.MODID);

    static {
        //DEFERRED_PROPELLANT_TYPE.makeRegistry((builder) -> builder.sync(true));
    }

                    //.dataPackRegistry(PropellantType.DIRECT_CODEC, PropellantType.DIRECT_CODEC));
    public static final DeferredHolder<PropellantType,PropellantType> METHALOX = DEFERRED_PROPELLANT_TYPE
            .register("methalox", () -> new PropellantType(
                    Map.of(
                            TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag, 2f,//should be 3.7f
                            TagsInit.CustomFluidTags.LIQUID_METHANE.tag, 1f),
                    460,
                    4500f,
                    1.1f,
                    18
            ));//real value is 459
    public static final DeferredHolder<PropellantType,PropellantType> LH2LOX = DEFERRED_PROPELLANT_TYPE
            .register("lh2lox", () -> new PropellantType(
                    Map.of(
                            TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag, 6f,
                            TagsInit.CustomFluidTags.LIQUID_HYDROGEN.tag, 1f),
                    540,
                    4000f,
                    1.2f,
                    10
            ));

    public static Registry<PropellantType> getSyncedPropellantRegistry() {
        return getSideAwareRegistry(Keys.PROPELLANT_TYPE);
    }

    public static void register(IEventBus modEventBus) {
        DEFERRED_PROPELLANT_TYPE.register(modEventBus);
    }

    public static class Keys {
        public static final ResourceKey<Registry<PropellantType>> PROPELLANT_TYPE =
                ResourceKey.createRegistryKey(ResourceLocation.parse("creatingspace:propellant_type"));
    }
}