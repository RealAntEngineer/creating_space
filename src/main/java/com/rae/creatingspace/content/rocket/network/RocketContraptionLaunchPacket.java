package com.rae.creatingspace.content.rocket.network;

import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.init.PacketInit;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEditPacket;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;


public class RocketContraptionLaunchPacket implements ServerboundPacketPayload {

    public int entityID;
    public ResourceLocation destination;
    public static final StreamCodec<RegistryFriendlyByteBuf, ScheduleEditPacket> STREAM_CODEC = Schedule.STREAM_CODEC.map(
            ScheduleEditPacket::new, ScheduleEditPacket::schedule
    );
    @Override
    public void handle(ServerPlayer player) {
        Entity entity = player.level().getEntity(entityID);
        if (entity instanceof RocketContraptionEntity ce) {
                        /*RocketSchedule schedule = new RocketSchedule();
                        CompoundTag instructionTag = new CompoundTag();
                        instructionTag.putString("Id", CreatingSpace.resource("destination").toString());
                        CompoundTag data = new CompoundTag();
                        data.putString("Text", destination.toString());
                        instructionTag.put("Data", data);
                        ScheduleEntry entry = new ScheduleEntry();
                        entry.instruction = ScheduleInstruction.fromTag(instructionTag);
                        entry.conditions.add(List.of());
                        schedule.entries.add(entry);*/
            //ce.getEntityData().set(RUNNING_ENTITY_DATA_ACCESSOR, true);
            //ce.schedule.setSchedule(schedule, true);
            ce.destination = destination;
            //ce.setShouldHandleCalculation(true);
            RocketContraptionEntity.handelTrajectoryCalculation(ce);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return PacketInit.LAUNCH_ROCKET;
    }
}
