package com.rae.creatingspace.utilities.packet;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.squedule.RocketSchedule;
import com.rae.creatingspace.api.squedule.ScheduleEntry;
import com.rae.creatingspace.api.squedule.condition.ScheduledDelay;
import com.rae.creatingspace.api.squedule.destination.ScheduleInstruction;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;

import static com.rae.creatingspace.server.entities.RocketContraptionEntity.RUNNING_ENTITY_DATA_ACCESSOR;

public class RocketContraptionLaunchPacket extends SimplePacketBase {

    public int entityID;
    public ResourceLocation destination;

    public RocketContraptionLaunchPacket(int entityID, ResourceLocation destination) {
        this.entityID = entityID;
        this.destination = destination;
    }

    public RocketContraptionLaunchPacket(FriendlyByteBuf buffer) {
        entityID = buffer.readInt();
        destination = buffer.readResourceLocation();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityID);
        buffer.writeResourceLocation(destination);

    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(
                () -> {
                    ServerPlayer sender = context.getSender();
                    Entity entity = sender.level.getEntity(entityID);
                    if (entity instanceof RocketContraptionEntity ce) {
                        RocketSchedule schedule = new RocketSchedule();
                        CompoundTag instructionTag = new CompoundTag();
                        instructionTag.putString("Id", CreatingSpace.resource("destination").toString());
                        CompoundTag data = new CompoundTag();
                        data.putString("Text", destination.toString());
                        instructionTag.put("Data", data);
                        ScheduleEntry entry = new ScheduleEntry();
                        entry.instruction = ScheduleInstruction.fromTag(instructionTag);
                        entry.conditions.add(List.of(new ScheduledDelay()));
                        schedule.entries.add(entry);
                        ce.getEntityData().set(RUNNING_ENTITY_DATA_ACCESSOR, true);
                        ce.schedule.setSchedule(schedule, true);
                        ce.destination = destination;
                        ce.setShouldHandleCalculation(true);
                        //handelTrajectoryCalculation(ce);
                    }
                });
        return true;
    }
}
