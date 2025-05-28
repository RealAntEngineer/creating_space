package com.rae.creatingspace.legacy.utilities.packet;

import com.rae.creatingspace.content.rocket.network.RocketContraptionUpdatePacket;
import com.rae.creatingspace.legacy.saved.UnlockabledDesignSavedData;
import com.rae.creatingspace.legacy.saved.UnlockedDesignManager;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;

public class UpdateSavedDataPacket implements ClientboundPacketPayload {

    public static final StreamCodec<ByteBuf, UpdateSavedDataPacket> STREAM_CODEC = StreamCodec.of(
            (byteBuf, packet) -> packet.write(new FriendlyByteBuf(byteBuf)),
            byteBuf ->  new UpdateSavedDataPacket(new FriendlyByteBuf(byteBuf))
    );
    private final UnlockabledDesignSavedData savedData;

    public UpdateSavedDataPacket(UnlockabledDesignSavedData savedData) {
        this.savedData = savedData;
    }

    public UpdateSavedDataPacket(FriendlyByteBuf buffer) {
        savedData = UnlockabledDesignSavedData.load(Objects.requireNonNull(buffer.readNbt()));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(savedData.save(new CompoundTag()));
    }

    @Override
    public void handle(LocalPlayer player) {
        UnlockedDesignManager.setSavedData(savedData);

    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return null;
    }
}