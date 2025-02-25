package com.rae.creatingspace.api.squedule.instruction;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.squedule.RocketSchedule;
import com.simibubi.create.content.trains.schedule.ScheduleDataEntry;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public abstract class ScheduleInstruction extends ScheduleDataEntry {

    public abstract boolean supportsConditions();

    public final CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        CompoundTag dataCopy = data.copy();
        writeAdditional(dataCopy);
        tag.putString("Id", getId().toString());
        tag.put("Data", dataCopy);
        return tag;
    }

    public static ScheduleInstruction fromTag(CompoundTag tag) {
        ResourceLocation location = new ResourceLocation(tag.getString("Id"));
        Supplier<? extends ScheduleInstruction> supplier = null;
        for (Pair<ResourceLocation, Supplier<? extends ScheduleInstruction>> pair : RocketSchedule.INSTRUCTION_TYPES)
            if (pair.getFirst()
                    .equals(location))
                supplier = pair.getSecond();

        if (supplier == null) {
            CreatingSpace.LOGGER.warn("Could not parse schedule instruction type: {}", location);
            return new DestinationInstruction();
        }

        ScheduleInstruction scheduleDestination = supplier.get();
        // Left around for migration purposes. Data added in writeAdditional has moved into the "Data" tag
        scheduleDestination.readAdditional(tag);
        CompoundTag data = tag.getCompound("Data");
        scheduleDestination.readAdditional(data);
        scheduleDestination.data = data;
        return scheduleDestination;
    }

}
