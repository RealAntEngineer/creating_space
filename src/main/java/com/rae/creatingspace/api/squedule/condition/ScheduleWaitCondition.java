package com.rae.creatingspace.api.squedule.condition;

import com.rae.creatingspace.api.squedule.RocketSchedule;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.schedule.ScheduleDataEntry;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public abstract class ScheduleWaitCondition extends ScheduleDataEntry {

    public abstract boolean tickCompletion(Level level, RocketContraptionEntity train, CompoundTag context);

    protected void requestStatusToUpdate(CompoundTag context) {
        context.putInt("StatusVersion", context.getInt("StatusVersion") + 1);
    }

    public final CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        CompoundTag dataCopy = data.copy();
        writeAdditional(dataCopy);
        tag.putString("Id", getId().toString());
        tag.put("Data", dataCopy);
        return tag;
    }

    public static ScheduleWaitCondition fromTag(CompoundTag tag) {
        ResourceLocation location = new ResourceLocation(tag.getString("Id"));
        Supplier<? extends ScheduleWaitCondition> supplier = null;
        for (Pair<ResourceLocation, Supplier<? extends ScheduleWaitCondition>> pair : RocketSchedule.CONDITION_TYPES)
            if (pair.getFirst()
                    .equals(location))
                supplier = pair.getSecond();

        if (supplier == null) {
            Create.LOGGER.warn("Could not parse waiting condition type: " + location);
            return null;
        }

        ScheduleWaitCondition condition = supplier.get();
        // Left around for migration purposes. Data added in writeAdditional has moved into the "Data" tag
        condition.readAdditional(tag);
        CompoundTag data = tag.getCompound("Data");
        condition.readAdditional(data);
        condition.data = data;
        return condition;
    }

    public abstract MutableComponent getWaitingStatus(Level level, RocketContraptionEntity train, CompoundTag tag);

}
