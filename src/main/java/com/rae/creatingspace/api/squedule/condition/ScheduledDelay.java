package com.rae.creatingspace.api.squedule.condition;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ScheduledDelay extends TimedWaitCondition {

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(ItemStack.EMPTY, Lang.translateDirect("schedule.condition.delay_short", formatTime(true)));
    }

    @Override
    public boolean tickCompletion(Level level, RocketContraptionEntity train, CompoundTag context) {
        int time = context.getInt("Time");
        if (CSConfigs.COMMON.additionalLogInfo.get()) {
            CreatingSpace.LOGGER.info("condition : " + time + "/" + totalWaitTicks());
        }
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