package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.AllSoundEvents.SoundEntryBuilder;
import net.minecraft.resources.ResourceLocation;


public class SoundEventInit {



    private static SoundEntryBuilder create(String name) {
        return create(CreatingSpace.resource(name));
    }

    public static SoundEntryBuilder create(ResourceLocation id) {
        return new SoundEntryBuilder(id);
    }


}
