package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.utilities.packet.*;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;
import static net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER;

public enum PacketInit {
    CRAFT_ENGINE(EngineerTableCraft.class, EngineerTableCraft::new, PLAY_TO_SERVER),
    SYNC_ROCKET_ENGINEER_BE(RocketEngineerTableSync.class, RocketEngineerTableSync::new, PLAY_TO_SERVER),
    ASSEMBLE_ROCKET(RocketAssemblePacket.class, RocketAssemblePacket::new, PLAY_TO_SERVER),
    ASSEMBLE_ROCKET_2(NewRocketAssemblePacket.class, NewRocketAssemblePacket::new, PLAY_TO_SERVER),
    ROCKET_CONTROLS_SETTING(RocketControlsSettingsPacket.class,RocketControlsSettingsPacket::new,PLAY_TO_SERVER),
    LAUNCH_ROCKET(RocketContraptionLaunchPacket.class, RocketContraptionLaunchPacket::new, PLAY_TO_SERVER),
    DISASSEMBLE_ROCKET(RocketContraptionDisassemblePacket.class, RocketContraptionDisassemblePacket::new, PLAY_TO_SERVER),
    SEALER_TRY_SEALING(SealerTrySealing.class,SealerTrySealing::new,PLAY_TO_SERVER),
    SEALER_SETTINGS(SealerSettings.class,SealerSettings::new,PLAY_TO_SERVER),
    UPDATE_ROCKET(RocketContraptionUpdatePacket.class, RocketContraptionUpdatePacket::new, PLAY_TO_CLIENT),
    ROCKET_SCHEDULE_EDIT(RocketScheduleEditPacket.class, RocketScheduleEditPacket::new, PLAY_TO_SERVER);
    public static final ResourceLocation CHANNEL_NAME = CreatingSpace.resource("main");
    public static final int NETWORK_VERSION = 3;
    public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
    private static SimpleChannel channel;

    private final PacketInit.PacketType<?> packetType;

    <T extends SimplePacketBase> PacketInit(Class<T> type, Function<FriendlyByteBuf, T> factory,
                                            NetworkDirection direction) {
        packetType = new PacketInit.PacketType<>(type, factory, direction);
    }
    public static void registerPackets() {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .serverAcceptedVersions(NETWORK_VERSION_STR::equals)
                .clientAcceptedVersions(NETWORK_VERSION_STR::equals)
                .networkProtocolVersion(() -> NETWORK_VERSION_STR)
                .simpleChannel();

        for (PacketInit packet : values())
            packet.packetType.register();
    }

    public static SimpleChannel getChannel() {
        return channel;
    }

    public static void sendToNear(Level world, BlockPos pos, int range, Object message) {
        getChannel().send(
                PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), range, world.dimension())),
                message);
    }

    private static class PacketType<T extends SimplePacketBase> {
        private static int index = 0;

        private final BiConsumer<T, FriendlyByteBuf> encoder;
        private final Function<FriendlyByteBuf, T> decoder;
        private final BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
        private final Class<T> type;
        private final NetworkDirection direction;

        private PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            encoder = T::write;
            decoder = factory;
            handler = (packet, contextSupplier) -> {
                NetworkEvent.Context context = contextSupplier.get();
                if (packet.handle(context)) {
                    context.setPacketHandled(true);
                }
            };
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            getChannel().messageBuilder(type, index++, direction)
                    .encoder(encoder)
                    .decoder(decoder)
                    .consumerNetworkThread(handler)
                    .add();
        }
    }

}
