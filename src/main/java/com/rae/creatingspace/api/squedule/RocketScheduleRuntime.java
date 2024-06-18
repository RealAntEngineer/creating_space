package com.rae.creatingspace.api.squedule;

import com.rae.creatingspace.api.squedule.condition.ScheduleWaitCondition;
import com.rae.creatingspace.api.squedule.destination.ChangeTitleInstruction;
import com.rae.creatingspace.api.squedule.destination.DestinationInstruction;
import com.rae.creatingspace.api.squedule.destination.ScheduleInstruction;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.rae.creatingspace.utilities.CSDimensionUtil;
import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class RocketScheduleRuntime {
    //TODO remove anything unnecessary : item + graph stuff + train stuff
    // tick methode should be fine
    private static final int TBD = -1;
    private static final int INVALID = -2;

    public enum State {
        PRE_TRANSIT, IN_TRANSIT, POST_TRANSIT
    }

    //TODO remove train and replace by RocketEntity
    //Train train;
    ResourceLocation currentWorld;
    RocketContraptionEntity rocket;
    RocketSchedule schedule;

    public boolean isAutoSchedule;
    public boolean paused;
    public boolean completed;
    public int currentEntry;
    public State state;

    static final int INTERVAL = 40;
    int cooldown;
    List<Integer> conditionProgress;
    List<CompoundTag> conditionContext;
    String currentTitle;

    int ticksInTransit;
    List<Integer> predictionTicks;

    public boolean displayLinkUpdateRequested;

    public RocketScheduleRuntime(RocketContraptionEntity rocket) {
        this.rocket = rocket;
        reset();
    }

    public void destinationReached() {
        if (state != State.IN_TRANSIT)
            return;
        state = State.POST_TRANSIT;
        conditionProgress.clear();
        displayLinkUpdateRequested = true;

        if (ticksInTransit > 0) {
            int current = predictionTicks.get(currentEntry);
            if (current > 0)
                ticksInTransit = (current + ticksInTransit) / 2;
            predictionTicks.set(currentEntry, ticksInTransit);
        }

        if (currentEntry >= schedule.entries.size())
            return;
        List<List<ScheduleWaitCondition>> conditions = schedule.entries.get(currentEntry).conditions;
        for (int i = 0; i < conditions.size(); i++) {
            conditionProgress.add(0);
            conditionContext.add(new CompoundTag());
        }
    }

    public void transitInterrupted() {
        if (schedule == null || state != State.IN_TRANSIT)
            return;
        state = State.PRE_TRANSIT;
        cooldown = 0;
    }

    //TODO create a Rocket like the Train class ?
    public void tick(Level level) {
        if (schedule == null)
            return;
        if (paused)
            return;
        if (rocket.destination != null) {
            ticksInTransit++;
            return;
        }
        if (currentEntry >= schedule.entries.size()) {
            currentEntry = 0;
            if (!schedule.cyclic) {
                paused = true;
                completed = true;
            }
            return;
        }

        if (cooldown-- > 0)
            return;
        if (state == State.IN_TRANSIT)
            return;
        if (state == State.POST_TRANSIT) {
            tickConditions(level);
            return;
        }

        RocketPath nextPath = startCurrentInstruction();
        if (nextPath == null)
            return;

        rocket.successfulNavigation();
        if (nextPath.destination == rocket.getLevel().dimension().location()) {
            state = State.IN_TRANSIT;
            destinationReached();
            return;
        }
        if (rocket.startNavigation(nextPath) != TBD) {
            state = State.IN_TRANSIT;
            ticksInTransit = 0;
        }
    }

    public void tickConditions(Level level) {
        List<List<ScheduleWaitCondition>> conditions = schedule.entries.get(currentEntry).conditions;
        for (int i = 0; i < conditions.size(); i++) {
            List<ScheduleWaitCondition> list = conditions.get(i);
            int progress = conditionProgress.get(i);

            if (progress >= list.size()) {
                state = State.PRE_TRANSIT;
                currentEntry++;
                return;
            }

            CompoundTag tag = conditionContext.get(i);
            ScheduleWaitCondition condition = list.get(progress);
            int prevVersion = tag.getInt("StatusVersion");

            if (condition.tickCompletion(level, rocket, tag)) {
                conditionContext.set(i, new CompoundTag());
                conditionProgress.set(i, progress + 1);
                displayLinkUpdateRequested |= i == 0;
            }

            displayLinkUpdateRequested |= i == 0 && prevVersion != tag.getInt("StatusVersion");
        }
    }

    public RocketPath startCurrentInstruction() {
        //TODO replace this by the resource location of dimensions -> may be a Record to add the coordinates ?
        ScheduleEntry entry = schedule.entries.get(currentEntry);
        ScheduleInstruction instruction = entry.instruction;

        if (instruction instanceof DestinationInstruction destination) {
            ResourceLocation destinationWorld = destination.getDestination();

            /*if (!train.hasForwardConductor() && !train.hasBackwardConductor()) {
                train.status.missingConductor();
                cooldown = INTERVAL;
                return null;
            }*/
            int cost = CSDimensionUtil.cost(currentWorld, destinationWorld);
            if (cost < 0) {
                return null;
            } else {
                return new RocketPath(currentWorld, destinationWorld, cost);
            }
        }

        if (instruction instanceof ChangeTitleInstruction title) {
            currentTitle = title.getScheduleTitle();
            state = State.PRE_TRANSIT;
            currentEntry++;
            return null;
        }
        return null;
    }

    public void setSchedule(RocketSchedule schedule, boolean auto) {
        reset();
        this.schedule = schedule;
        currentEntry = Mth.clamp(schedule.savedProgress, 0, schedule.entries.size() - 1);
        paused = false;
        isAutoSchedule = auto;
        //train.status.newSchedule();
        predictionTicks = new ArrayList<>();
        schedule.entries.forEach($ -> predictionTicks.add(TBD));
        displayLinkUpdateRequested = true;
    }

    public RocketSchedule getSchedule() {
        return schedule;
    }

    public void discardSchedule() {
        //train.navigation.cancelNavigation();
        reset();
    }

    private void reset() {
        paused = true;
        completed = false;
        isAutoSchedule = false;
        currentEntry = 0;
        currentTitle = "";
        schedule = null;
        state = State.PRE_TRANSIT;
        conditionProgress = new ArrayList<>();
        conditionContext = new ArrayList<>();
        predictionTicks = new ArrayList<>();
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("CurrentEntry", currentEntry);
        tag.putBoolean("AutoSchedule", isAutoSchedule);
        tag.putBoolean("Paused", paused);
        tag.putBoolean("Completed", completed);
        if (schedule != null)
            tag.put("Schedule", schedule.write());
        NBTHelper.writeEnum(tag, "State", state);
        tag.putIntArray("ConditionProgress", conditionProgress);
        tag.put("ConditionContext", NBTHelper.writeCompoundList(conditionContext, CompoundTag::copy));
        tag.putIntArray("TransitTimes", predictionTicks);
        return tag;
    }

    public void read(CompoundTag tag) {
        reset();
        paused = tag.getBoolean("Paused");
        completed = tag.getBoolean("Completed");
        isAutoSchedule = tag.getBoolean("AutoSchedule");
        currentEntry = tag.getInt("CurrentEntry");
        if (tag.contains("Schedule"))
            schedule = RocketSchedule.fromTag(tag.getCompound("Schedule"));
        state = NBTHelper.readEnum(tag, "State", State.class);
        for (int i : tag.getIntArray("ConditionProgress"))
            conditionProgress.add(i);
        NBTHelper.iterateCompoundList(tag.getList("ConditionContext", Tag.TAG_COMPOUND), conditionContext::add);

        int[] readTransits = tag.getIntArray("TransitTimes");
        if (schedule != null) {
            schedule.entries.forEach($ -> predictionTicks.add(TBD));
            if (readTransits.length == schedule.entries.size())
                for (int i = 0; i < readTransits.length; i++)
                    predictionTicks.set(i, readTransits[i]);
        }
    }

    public ItemStack returnSchedule() {
        if (schedule == null)
            return ItemStack.EMPTY;
        ItemStack stack = AllItems.SCHEDULE.asStack();
        CompoundTag nbt = stack.getOrCreateTag();
        schedule.savedProgress = currentEntry;
        nbt.put("Schedule", schedule.write());
        stack = isAutoSchedule ? ItemStack.EMPTY : stack;
        discardSchedule();
        return stack;
    }

    public void setSchedulePresentClientside(boolean present) {
        schedule = present ? new RocketSchedule() : null;
    }

    public MutableComponent getWaitingStatus(Level level) {
        List<List<ScheduleWaitCondition>> conditions = schedule.entries.get(currentEntry).conditions;
        if (conditions.isEmpty() || conditionProgress.isEmpty() || conditionContext.isEmpty())
            return Components.empty();

        List<ScheduleWaitCondition> list = conditions.get(0);
        int progress = conditionProgress.get(0);
        if (progress >= list.size())
            return Components.empty();

        CompoundTag tag = conditionContext.get(0);
        ScheduleWaitCondition condition = list.get(progress);
        return condition.getWaitingStatus(level, rocket, tag);
    }

}
