package com.rae.creatingspace.content.rocket.squedule.instruction;

import com.google.common.collect.ImmutableList;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import com.rae.creatingspace.legacy.utilities.CSUtil;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.data.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DestinationInstruction extends ScheduleInstruction {

    private List<ResourceLocation> planets;

    @Override
    public boolean supportsConditions() {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return CreatingSpace.resource("destination");
    }

    @Override
    public ItemStack getSecondLineIcon() {
        return AllBlocks.TRACK_STATION.asStack();
    }


    public ResourceLocation getDestination() {
        updateDataFromId();
        String data = textData("destination");
        if (data.isBlank())
            return null;
        return ResourceLocation.tryParse(data);
    }

    //what is this used for exactly ?
    private void updateDataFromId() {
        int id = intData("intId");
        if (planets != null && id < planets.size()) {
            data.putString("destination", planets.get(id).toString());
        }
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of(CreateLang.translateDirect("schedule.instruction.filter_edit_box"),
                CreateLang.translateDirect("schedule.instruction.filter_edit_box_1")
                        .withStyle(ChatFormatting.GRAY),
                CreateLang.translateDirect("schedule.instruction.filter_edit_box_2")
                        .withStyle(ChatFormatting.DARK_GRAY),
                CreateLang.translateDirect("schedule.instruction.filter_edit_box_3")
                        .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        updateDataFromId();
        return Pair.of(AllBlocks.TRACK_STATION.asStack(), Component.translatable(textData("Text")));
    }

    @Override
    public List<Component> getTitleAs(String type) {
        updateDataFromId();
        return ImmutableList.of(CreateLang.translateDirect("schedule." + type + "." + getId().getPath() + ".summary")
                .withStyle(ChatFormatting.GOLD), CreateLang.translateDirect("generic.in_quotes", Component.translatable(textData("destination"))));
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        planets = getPlanets();
        builder.addSelectionScrollInput(0, 121, (s, t) -> {
                    s.forOptions(planets.stream().map(r -> Component.translatable(r.toString())).toList());
                },
                "intId");
        builder.addIntegerTextInput(125, 31,(editBox,tooltipArea) -> {
            tooltipArea.withTooltip(List.of(Component.translatable("creatingspace.gui.rocket_controls.x_entry_coord")));},
                "XCoord");
        builder.addIntegerTextInput(159, 31,(editBox,tooltipArea) -> {
            tooltipArea.withTooltip(List.of(Component.translatable("creatingspace.gui.rocket_controls.z_entry_coord")));},
                "ZCoord");
    }

    @NotNull
    private static List<ResourceLocation> getPlanets() {
        return CSDimensionUtil.getPlanets();
    }

    public @Nullable Vec2 getXYCoord() {
        updateDataFromId();
        if ( CSUtil.isInteger(data.getString("XCoord")) && CSUtil.isInteger(data.getString("ZCoord"))) {
            int X = Integer.parseInt(data.getString("XCoord"));
            int Z = Integer.parseInt(data.getString("ZCoord"));
            return new Vec2(X, Z);
        } else {
            return null;
        }
    }
}