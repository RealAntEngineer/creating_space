package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.engine.table.EngineerTableCraft;
import com.rae.creatingspace.content.rocket.network.*;
import com.rae.creatingspace.legacy.utilities.packet.*;
import com.simibubi.create.Create;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;


public enum PacketInit implements BasePacketPayload.PacketTypeProvider {
    // C2S
    CRAFT_ENGINE(EngineerTableCraft.class, EngineerTableCraft.STREAM_CODEC),
    SYNC_ROCKET_ENGINEER_BE(RocketEngineerTableSync.class, RocketEngineerTableSync.STREAM_CODEC),
    //ASSEMBLE_ROCKET(RocketAssemblePacket.class, RocketAssemblePacket.STREAM_CODEC),
    ASSEMBLE_ROCKET_2(NewRocketAssemblePacket.class, NewRocketAssemblePacket.STREAM_CODEC),
    ROCKET_CONTROLS_SETTING(RocketControlsSettingsPacket.class,RocketControlsSettingsPacket.STREAM_CODEC),
    LAUNCH_ROCKET(RocketContraptionLaunchPacket.class, RocketContraptionLaunchPacket.STREAM_CODEC),
    SYNC_POSMAP_CLIENT(RocketEntryPosMapClientPacket.class, RocketEntryPosMapClientPacket.STREAM_CODEC),
    DISASSEMBLE_ROCKET(RocketContraptionDisassemblePacket.class, RocketContraptionDisassemblePacket.STREAM_CODEC),
    ROCKET_SCHEDULE_EDIT(RocketScheduleEditPacket.class, RocketScheduleEditPacket.STREAM_CODEC),

    // S2C
    UPDATE_SAVED_DATA(UpdateSavedDataPacket.class, UpdateSavedDataPacket.STREAM_CODEC),
    UPDATE_ROCKET(RocketContraptionUpdatePacket.class, RocketContraptionUpdatePacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> PacketInit(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(Create.asResource(name)),
                clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(CreatingSpace.MODID, 1);
        for (PacketInit packet : PacketInit.values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }

}
