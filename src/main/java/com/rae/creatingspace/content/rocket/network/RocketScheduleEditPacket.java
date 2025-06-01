package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.api.squedule.RocketSchedule;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.legacy.utilities.packet.NewRocketAssemblePacket;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Objects;

public class RocketScheduleEditPacket implements ServerboundPacketPayload {

	private RocketSchedule schedule;
	private int rocketId;

	public RocketScheduleEditPacket(RocketSchedule schedule, int rocketId) {
		this.schedule = schedule;
		this.rocketId = rocketId;
	}
	public static final StreamCodec<RegistryFriendlyByteBuf, NewRocketAssemblePacket> STREAM_CODEC = StreamCodec.composite(
			BlockPos.STREAM_CODEC, packet -> packet.pos,
			ByteBufCodecs.BOOL, packet -> packet.assembleNextTick,
			NewRocketAssemblePacket::new
	);
	public RocketScheduleEditPacket(FriendlyByteBuf buffer) {
		schedule = RocketSchedule.fromTag(buffer.readNbt());
		rocketId = buffer.readInt();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeNbt(schedule.write());
		buffer.writeInt(rocketId);
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
		return null;
	}
}
