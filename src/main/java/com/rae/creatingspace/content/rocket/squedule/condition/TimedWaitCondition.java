package com.rae.creatingspace.content.rocket.squedule.condition;

import com.google.common.collect.ImmutableList;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public abstract class TimedWaitCondition extends ScheduleWaitCondition {

    public enum TimeUnit {
        TICKS(1, "t", "generic.unit.ticks"),
        SECONDS(20, "s", "generic.unit.seconds"),
        MINUTES(20 * 60, "min", "generic.unit.minutes");

        public final int ticksPer;
        public final String suffix;
        public final String key;

        TimeUnit(int ticksPer, String suffix, String key) {
            this.ticksPer = ticksPer;
            this.suffix = suffix;
            this.key = key;
        }

        public static List<Component> translatedOptions() {
            return CreateLang.translatedOptions(null, TICKS.key, SECONDS.key, MINUTES.key);
        }
    }

    protected void requestDisplayIfNecessary(CompoundTag context, int time) {
        int ticksUntilDeparture = totalWaitTicks() - time;
        if (ticksUntilDeparture < 20 * 60 && ticksUntilDeparture % 100 == 0)
            requestStatusToUpdate(context);
        if (ticksUntilDeparture >= 20 * 60 && ticksUntilDeparture % (20 * 60) == 0)
            requestStatusToUpdate(context);
    }

    public int totalWaitTicks() {
        return getValue() * getUnit().ticksPer;
    }

    public TimedWaitCondition() {
        data.putInt("Value", 5);
        data.putInt("TimeUnit", TimeUnit.SECONDS.ordinal());
    }

    protected Component formatTime(boolean compact) {
        if (compact)
            return Component.literal(getValue() + getUnit().suffix);
        return Component.literal(getValue() + " ").append(CreateLang.translateDirect(getUnit().key));
    }

    @Override
    public List<Component> getTitleAs(String type) {
        return ImmutableList.of(
                Component.translatable(getId().getNamespace() + ".schedule." + type + "." + getId().getPath()),
                CreateLang.translateDirect("schedule.condition.for_x_time", formatTime(false))
                        .withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return new ItemStack(Items.REPEATER);
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of(CreateLang.translateDirect("generic.duration"));
    }

    public int getValue() {
        return intData("Value");
    }

    public TimeUnit getUnit() {
        return enumData("TimeUnit", TimeUnit.class);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        builder.addScrollInput(0, 31, (i, l) -> {
            i.titled(CreateLang.translateDirect("generic.duration"))
                    .withShiftStep(15)
                    .withRange(0, 121);
            i.lockedTooltipX = -15;
            i.lockedTooltipY = 35;
        }, "Value");

        builder.addSelectionScrollInput(36, 85, (i, l) -> i.forOptions(TimeUnit.translatedOptions())
                .titled(CreateLang.translateDirect("generic.timeUnit")), "TimeUnit");
    }

    @Override
    public MutableComponent getWaitingStatus(Level level, RocketContraptionEntity train, CompoundTag tag) {
        int time = tag.getInt("Time");
        int ticksUntilDeparture = totalWaitTicks() - time;
        boolean showInMinutes = ticksUntilDeparture >= 20 * 60;
        int num =
                (int) (showInMinutes ? Math.floor(ticksUntilDeparture / (20 * 60f)) : Math.ceil(ticksUntilDeparture / 100f) * 5);
        String key = "generic." + (showInMinutes ? num == 1 ? "daytime.minute" : "unit.minutes"
                : num == 1 ? "daytime.second" : "unit.seconds");
        return CreateLang.translateDirect("schedule.condition." + getId().getPath() + ".status",
                Component.literal(num + " ").append(CreateLang.translateDirect(key)));
    }

}