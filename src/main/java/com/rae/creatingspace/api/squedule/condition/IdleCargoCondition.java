package com.rae.creatingspace.api.squedule.condition;

import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IdleCargoCondition extends TimedWaitCondition {

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(ItemStack.EMPTY, Lang.translateDirect("schedule.condition.idle_short", formatTime(true)));
    }

    @Override
    public ResourceLocation getId() {
        return Create.asResource("idle");
    }

    @Override
    public boolean tickCompletion(Level level, RocketContraptionEntity train, CompoundTag context) {
    /*    int idleTime = Integer.MAX_VALUE;
            idleTime = Math.min(idleTime, train.getContraption().getSharedFuelInventory().getTicksSinceLastExchange());
        context.putInt("Time", idleTime);
        requestDisplayIfNecessary(context, idleTime);
        return idleTime > totalWaitTicks();*/
        return false;
    }

}