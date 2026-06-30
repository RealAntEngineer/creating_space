package com.rae.creatingspace.content.rocket.squedule.condition;

import com.rae.creatingspace.content.rocket.squedule.RocketSchedule;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.schedule.ScheduleDataEntry;
import com.simibubi.create.foundation.codec.CreateStreamCodecs;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public abstract class ScheduleWaitCondition extends ScheduleDataEntry {
    public static final StreamCodec<RegistryFriendlyByteBuf, ScheduleWaitCondition> STREAM_CODEC = CreateStreamCodecs.ofLegacyNbtWithRegistries(
           ScheduleWaitCondition::write, ScheduleWaitCondition::fromTag
    );
    public abstract boolean tickCompletion(Level level, RocketContraptionEntity train, CompoundTag context);

    protected void requestStatusToUpdate(CompoundTag context) {
        context.putInt("StatusVersion", context.getInt("StatusVersion") + 1);
    }

    public final CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        CompoundTag dataCopy = data.copy();
        writeAdditional(registries,dataCopy);
        tag.putString("Id", getId().toString());
        tag.put("Data", dataCopy);
        return tag;
    }

    public static ScheduleWaitCondition fromTag(HolderLookup.Provider registries,CompoundTag tag) {
        ResourceLocation location = ResourceLocation.tryParse(tag.getString("Id"));
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
        condition.readAdditional(registries,tag);
        CompoundTag data = tag.getCompound("Data");
        condition.readAdditional(registries,data);
        condition.data = data;
        return condition;
    }

    public abstract MutableComponent getWaitingStatus(Level level, RocketContraptionEntity train, CompoundTag tag);

}
