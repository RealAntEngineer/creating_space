package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.rae.creatingspace.CreatingSpace.MODID;

public class SoundInit {

    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final RegistryObject<SoundEvent> ROCKET_LAUNCH = registerSound("rocket_launch_sound");

    public static RegistryObject<SoundEvent> registerSound(String id) {
        return SOUNDS.register(id,
                () -> new SoundEvent(CreatingSpace.resource(id)));
    }
    public static void register() {
        SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
