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

public class IdleCargoCondition extends TimedWaitCondition {

    @Override
    public Pair<ItemStack, Component> getSummary() {
        return Pair.of(ItemStack.EMPTY, CreateLang.translateDirect("schedule.condition.idle_short", formatTime(true)));
    }

    @Override
    public ResourceLocation getId() {
        return CreatingSpace.resource("idle");
    }

    @Override
    public boolean tickCompletion(Level level, RocketContraptionEntity rocket, CompoundTag context) {
        /*
        int idleTime = Integer.MAX_VALUE;
            idleTime = Math.min(idleTime, ((RocketContraption)rocket.getContraption()).getStorage().getTicksSinceLastExchange());
        context.putInt("Time", idleTime);
        requestDisplayIfNecessary(context, idleTime);
        return idleTime > totalWaitTicks();
         */
        return false;
    }

}