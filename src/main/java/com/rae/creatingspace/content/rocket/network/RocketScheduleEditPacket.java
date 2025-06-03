package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.api.squedule.RocketSchedule;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.init.PacketInit;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class RocketScheduleEditPacket implements ServerboundPacketPayload {

	private final RocketSchedule schedule;
	private final int rocketId;


	public static final StreamCodec<RegistryFriendlyByteBuf, RocketScheduleEditPacket> STREAM_CODEC = StreamCodec.composite(
			RocketSchedule.STREAM_CODEC, packet -> packet.schedule,
			ByteBufCodecs.INT, packet -> packet.rocketId,
			RocketScheduleEditPacket::new
	);
	public RocketScheduleEditPacket(RocketSchedule schedule, int rocketId) {
		this.schedule = schedule;
		this.rocketId = rocketId;
	}
	@Override
	public void handle(ServerPlayer player) {
		Entity entity = player.level().getEntity(rocketId);
		if (entity instanceof RocketContraptionEntity contraptionEntity) {
			contraptionEntity.schedule.setSchedule(schedule, true);
			contraptionEntity.sendPacket();
		}
	}

	@Override
	public PacketTypeProvider getTypeProvider() {
		return PacketInit.ROCKET_SCHEDULE_EDIT;
	}
}
