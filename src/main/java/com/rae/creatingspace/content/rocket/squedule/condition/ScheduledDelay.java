package com.rae.creatingspace.content.rocket.squedule.condition;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ScheduledDelay extends TimedWaitCondition {

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(ItemStack.EMPTY, CreateLang.translateDirect("schedule.condition.delay_short", formatTime(true)));
    }

    @Override
    public boolean tickCompletion(Level level, RocketContraptionEntity train, CompoundTag context) {
        int time = context.getInt("Time");
        if (time >= totalWaitTicks())
            return true;

        context.putInt("Time", time + 1);
        requestDisplayIfNecessary(context, time);
        return false;
    }

    @Override
    public ResourceLocation getId() {
        return CreatingSpace.resource("delay");
    }

}