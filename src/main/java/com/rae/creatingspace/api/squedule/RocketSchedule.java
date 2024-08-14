package com.rae.creatingspace.api.squedule;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.squedule.condition.IdleCargoCondition;
import com.rae.creatingspace.api.squedule.condition.ScheduleWaitCondition;
import com.rae.creatingspace.api.squedule.condition.ScheduledDelay;
import com.rae.creatingspace.api.squedule.condition.TimeOfDayCondition;
import com.rae.creatingspace.api.squedule.destination.DestinationInstruction;
import com.rae.creatingspace.api.squedule.destination.ScheduleInstruction;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RocketSchedule {

    public static List<Pair<ResourceLocation, Supplier<? extends ScheduleInstruction>>> INSTRUCTION_TYPES =
            new ArrayList<>();
    public static List<Pair<ResourceLocation, Supplier<? extends ScheduleWaitCondition>>> CONDITION_TYPES =
            new ArrayList<>();

    static {
        registerInstruction("destination", DestinationInstruction::new);
        registerCondition("delay", ScheduledDelay::new);
        registerCondition("time_of_day", TimeOfDayCondition::new);
        //registerCondition("fluid_threshold", FluidThresholdCondition::new);
        //registerCondition("item_threshold", ItemThresholdCondition::new);
        //registerCondition("redstone_link", RedstoneLinkCondition::new);
        //registerCondition("player_count", PlayerPassengerCondition::new);
        registerCondition("idle", IdleCargoCondition::new);
        //registerCondition("unloaded", StationUnloadedCondition::new);
        //registerCondition("powered", StationPoweredCondition::new);
    }

    private static void registerInstruction(String name, Supplier<? extends ScheduleInstruction> factory) {
        INSTRUCTION_TYPES.add(Pair.of(CreatingSpace.resource(name), factory));
    }

    private static void registerCondition(String name, Supplier<? extends ScheduleWaitCondition> factory) {
        CONDITION_TYPES.add(Pair.of(CreatingSpace.resource(name), factory));
    }

    public static <T> List<? extends Component> getTypeOptions(List<Pair<ResourceLocation, T>> list) {
        String langSection = list.equals(INSTRUCTION_TYPES) ? "instruction." : "condition.";
        return list.stream()
                .map(Pair::getFirst)
                .map(rl -> rl.getNamespace() + ".schedule." + langSection + rl.getPath())
                .map(Components::translatable)
                .toList();
    }

    public List<ScheduleEntry> entries;
    public boolean cyclic;
    public int savedProgress;

    public RocketSchedule() {
        entries = new ArrayList<>();
        cyclic = true;
        savedProgress = 0;
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        ListTag list = NBTHelper.writeCompoundList(entries, ScheduleEntry::write);
        tag.put("Entries", list);
        tag.putBoolean("Cyclic", cyclic);
        if (savedProgress > 0)
            tag.putInt("Progress", savedProgress);
        return tag;
    }

    public static RocketSchedule fromTag(CompoundTag tag) {
        RocketSchedule schedule = new RocketSchedule();
        schedule.entries = NBTHelper.readCompoundList(tag.getList("Entries", Tag.TAG_COMPOUND), ScheduleEntry::fromTag);
        schedule.cyclic = tag.getBoolean("Cyclic");
        if (tag.contains("Progress"))
            schedule.savedProgress = tag.getInt("Progress");
        return schedule;
    }

}
