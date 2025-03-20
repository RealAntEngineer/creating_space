package com.rae.creatingspace.api.squedule;

import com.rae.creatingspace.api.squedule.condition.ScheduleWaitCondition;
import com.rae.creatingspace.api.squedule.instruction.ScheduleInstruction;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public class ScheduleEntry {
    public ScheduleInstruction instruction;
    public List<List<ScheduleWaitCondition>> conditions;

    public ScheduleEntry() {
        conditions = new ArrayList<>();
    }

    public ScheduleEntry clone() {
        return fromTag(write());
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        ListTag outer = new ListTag();
        tag.put("Instruction", instruction.write());
        if (!instruction.supportsConditions())
            return tag;
        for (List<ScheduleWaitCondition> column : conditions) {
            if (column.contains(null)) {
                System.out.println("encountered null value in condition list :" + column);
            }
            outer.add(NBTHelper.writeCompoundList(column, ScheduleWaitCondition::write));
        }
        tag.put("Conditions", outer);
        return tag;
    }

    public static ScheduleEntry fromTag(CompoundTag tag) {
        ScheduleEntry entry = new ScheduleEntry();
        entry.instruction = ScheduleInstruction.fromTag(tag.getCompound("Instruction"));
        entry.conditions = new ArrayList<>();
        if (entry.instruction.supportsConditions())
            for (Tag t : tag.getList("Conditions", Tag.TAG_LIST))
                if (t instanceof ListTag list)
                    entry.conditions.add(NBTHelper.readCompoundList(list, ScheduleWaitCondition::fromTag));
        return entry;
    }

}