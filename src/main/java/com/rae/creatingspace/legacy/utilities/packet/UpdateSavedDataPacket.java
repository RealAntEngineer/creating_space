package com.rae.creatingspace.legacy.utilities.packet;

import com.rae.creatingspace.init.PacketInit;
import com.rae.creatingspace.legacy.saved.UnlockabledDesignSavedData;
import com.rae.creatingspace.legacy.saved.UnlockedDesignManager;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public class UpdateSavedDataPacket implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSavedDataPacket> STREAM_CODEC = StreamCodec.of(
            (byteBuf, packet) -> packet.write(byteBuf),
            UpdateSavedDataPacket::new);
    private final UnlockabledDesignSavedData savedData; //TODO make a better stream codec

    public UpdateSavedDataPacket(UnlockabledDesignSavedData savedData) {
        this.savedData = savedData;
    }

    public UpdateSavedDataPacket(RegistryFriendlyByteBuf buffer) {
        savedData = UnlockabledDesignSavedData.load(Objects.requireNonNull(buffer.readNbt()),buffer.registryAccess());
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeNbt(savedData.save(new CompoundTag(),buffer.registryAccess()));
    }

    @Override
    public void handle(LocalPlayer player) {
        UnlockedDesignManager.setSavedData(savedData);

    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PacketInit.UPDATE_SAVED_DATA;
    }
}