package com.rae.creatingspace.api.squedule.instruction;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.squedule.RocketSchedule;
import com.simibubi.create.content.trains.schedule.ScheduleDataEntry;
import com.simibubi.create.foundation.codec.CreateStreamCodecs;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public abstract class ScheduleInstruction extends ScheduleDataEntry {
    public static final StreamCodec<RegistryFriendlyByteBuf, ScheduleInstruction> STREAM_CODEC = CreateStreamCodecs.ofLegacyNbtWithRegistries(
            ScheduleInstruction::write, ScheduleInstruction::fromTag
    );
    public abstract boolean supportsConditions();

    public final CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        CompoundTag dataCopy = data.copy();
        writeAdditional(registries,dataCopy);
        tag.putString("Id", getId().toString());
        tag.put("Data", dataCopy);
        return tag;
    }

    public static ScheduleInstruction fromTag(HolderLookup.Provider registries, CompoundTag tag)  {
        ResourceLocation location = ResourceLocation.tryParse(tag.getString("Id"));//TODO this is not right
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
        scheduleDestination.readAdditional(registries,tag);
        CompoundTag data = tag.getCompound("Data");
        scheduleDestination.readAdditional(registries,data);
        scheduleDestination.data = data;
        return scheduleDestination;
    }

}
