package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.api.squedule.RocketSchedule;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Objects;

public class RocketScheduleEditPacket extends SimplePacketBase {

	private RocketSchedule schedule;
	private int rocketId;

	public RocketScheduleEditPacket(RocketSchedule schedule, int rocketId) {
		this.schedule = schedule;
		this.rocketId = rocketId;
	}

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
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			Entity entity = Objects.requireNonNull(context.getSender()).level().getEntity(rocketId);
			if (entity instanceof RocketContraptionEntity contraptionEntity) {
				contraptionEntity.schedule.setSchedule(schedule, true);
				contraptionEntity.sendPacket();
			}
		});
		return true;
	}
}
