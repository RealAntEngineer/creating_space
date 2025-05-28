package com.rae.creatingspace.init.ingameobject;

import com.rae.creatingspace.CreatingSpace;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;


import static com.rae.creatingspace.CreatingSpace.MODID;

public class SoundInit {

    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent>  ROCKET_LAUNCH = registerSound("rocket_launch_sound");

    public static DeferredHolder<SoundEvent, SoundEvent> registerSound(String id) {
        return SOUNDS.register(id,
                () -> SoundEvent.createVariableRangeEvent(CreatingSpace.resource(id)));
    }
    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }

}
