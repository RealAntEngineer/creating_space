package com.rae.creatingspace.api.squedule;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.api.squedule.condition.*;
import com.rae.creatingspace.api.squedule.instruction.DestinationInstruction;
import com.rae.creatingspace.api.squedule.instruction.ScheduleInstruction;
import com.simibubi.create.content.trains.schedule.Schedule;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RocketSchedule {
    public static final StreamCodec<RegistryFriendlyByteBuf, RocketSchedule> STREAM_CODEC = StreamCodec.composite(
            CatnipStreamCodecBuilders.list(ScheduleEntry.STREAM_CODEC), schedule -> schedule.entries,
            ByteBufCodecs.BOOL, schedule -> schedule.cyclic,
            ByteBufCodecs.VAR_INT, schedule -> schedule.savedProgress,
            RocketSchedule::new
    );
    public static List<Pair<ResourceLocation, Supplier<? extends ScheduleInstruction>>> INSTRUCTION_TYPES =
            new ArrayList<>();
    public static List<Pair<ResourceLocation, Supplier<? extends ScheduleWaitCondition>>> CONDITION_TYPES =
            new ArrayList<>();

    static {
        registerInstruction("destination", DestinationInstruction::new);
        registerCondition("delay", ScheduledDelay::new);
        registerCondition("time_of_day", TimeOfDayCondition::new);
        registerCondition("fluid_threshold", FluidThresholdCondition::new);
        registerCondition("item_threshold", ItemThresholdCondition::new);
        //registerCondition("redstone_link", RedstoneLinkCondition::new);
        registerCondition("player_count", PlayerPassengerCondition::new);
        //registerCondition("idle", IdleCargoCondition::new);
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
                .map(Component::translatable)
                .toList();
    }

    public List<ScheduleEntry> entries;
    public boolean cyclic;
    public int savedProgress;

    public RocketSchedule(List<ScheduleEntry> entries, boolean cyclic, int savedProgress) {
        this.entries = entries;
        this.cyclic = cyclic;
        this.savedProgress = savedProgress;
    }

    public RocketSchedule() {
        entries = new ArrayList<>();
        cyclic = true;
        savedProgress = 0;
    }

    public CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ListTag list = NBTHelper.writeCompoundList(entries, e -> e.write(registries));
        tag.put("Entries", list);
        tag.putBoolean("Cyclic", cyclic);
        if (savedProgress > 0)
            tag.putInt("Progress", savedProgress);
        return tag;
    }

    public static RocketSchedule fromTag(HolderLookup.Provider registries,CompoundTag tag) {
        RocketSchedule schedule = new RocketSchedule();
        schedule.entries = NBTHelper.readCompoundList(tag.getList("Entries", Tag.TAG_COMPOUND), t-> ScheduleEntry.fromTag(registries, t));
        schedule.cyclic = tag.getBoolean("Cyclic");
        if (tag.contains("Progress"))
            schedule.savedProgress = tag.getInt("Progress");
        return schedule;
    }

}
