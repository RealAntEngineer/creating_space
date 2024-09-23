package com.rae.creatingspace.legacy.utilities.packet;

import com.rae.creatingspace.legacy.saved.UnlockabledDesignSavedData;
import com.rae.creatingspace.legacy.saved.UnlockedDesignManager;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;

public class UpdateSavedDataPacket extends SimplePacketBase {


    private final UnlockabledDesignSavedData savedData;

    public UpdateSavedDataPacket(UnlockabledDesignSavedData savedData) {
        this.savedData = savedData;
    }

    public UpdateSavedDataPacket(FriendlyByteBuf buffer) {
        savedData = UnlockabledDesignSavedData.load(Objects.requireNonNull(buffer.readNbt()));
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(savedData.save(new CompoundTag()));
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(
                () -> {
                    if (context.getDirection().getReceptionSide().isClient()) {

                        UnlockedDesignManager.setSavedData(savedData);
                    }
                });
        return true;
    }
}