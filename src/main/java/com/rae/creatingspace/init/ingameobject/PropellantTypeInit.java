package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.TagsInit;
import com.rae.creatingspace.server.design.PropellantType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.function.Supplier;

public class PropellantTypeInit {
    //TODO use datapackRegitry(Codec,Codec)
    public static final DeferredRegister<PropellantType> DEFERRED_PROPELLANT_TYPE =
            DeferredRegister.create(Keys.PROPELLANT_TYPE, CreatingSpace.MODID);
    public static final Supplier<IForgeRegistry<PropellantType>> PROPELLANT_TYPE = DEFERRED_PROPELLANT_TYPE.makeRegistry(
            RegistryBuilder::new);
    public static final RegistryObject<PropellantType> METHALOX = DEFERRED_PROPELLANT_TYPE
            .register("methalox", () -> new PropellantType(
                    Map.of(
                            TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag, 2f,//should be 3.7f
                            TagsInit.CustomFluidTags.LIQUID_METHANE.tag, 1f),
                    460,
                    4500f,
                    1.1f,
                    18
            ));//real value is 459
    public static final RegistryObject<PropellantType> LH2LOX = DEFERRED_PROPELLANT_TYPE
            .register("lh2lox", () -> new PropellantType(
                    Map.of(
                            TagsInit.CustomFluidTags.LIQUID_OXYGEN.tag, 6f,
                            TagsInit.CustomFluidTags.LIQUID_HYDROGEN.tag, 1f),
                    540,
                    4000f,
                    1.2f,
                    10
            ));//real value is 532
    public static final RegistryObject<PropellantType> METALIC_HYDROGEN = DEFERRED_PROPELLANT_TYPE
            .register("metalic_hydrogen", () -> new PropellantType(
                    Map.of(
                            TagsInit.CustomFluidTags.METALIC_HYDROGEN.tag, 1f),
                    6000,
                    18280f,
                    1.3f,
                    2
            ));

    public static void register(IEventBus modEventBus) {
        DEFERRED_PROPELLANT_TYPE.register(modEventBus);
    }

    public static class Keys {
        public static final ResourceKey<Registry<PropellantType>> PROPELLANT_TYPE =
                ResourceKey.createRegistryKey(new ResourceLocation("creatingspace:propellant_type"));
    }
}