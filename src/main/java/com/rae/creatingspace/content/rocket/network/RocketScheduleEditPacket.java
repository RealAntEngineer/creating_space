package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.content.rocket.squedule.RocketSchedule;
import com.rae.creatingspace.content.rocket.contraption.entity.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Objects;

public class RocketScheduleEditPacket extends SimplePacketBase {

	private final RocketSchedule schedule;
	private final int rocketId;
	private final boolean paused;

	public RocketScheduleEditPacket(RocketSchedule schedule,boolean paused, int rocketId) {
		this.schedule = schedule;
		this.rocketId = rocketId;
		this.paused = paused;
	}

	public RocketScheduleEditPacket(FriendlyByteBuf buffer) {
		schedule = RocketSchedule.fromTag(buffer.readNbt());
		rocketId = buffer.readInt();
		paused = buffer.readBoolean();
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeNbt(schedule.write());
		buffer.writeInt(rocketId);
		buffer.writeBoolean(paused);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			Entity entity = Objects.requireNonNull(context.getSender()).level().getEntity(rocketId);
			if (entity instanceof RocketContraptionEntity contraptionEntity) {
				contraptionEntity.schedule.setSchedule(schedule, paused);
				contraptionEntity.sendPacket();
			}
		});
		return true;
	}
}
