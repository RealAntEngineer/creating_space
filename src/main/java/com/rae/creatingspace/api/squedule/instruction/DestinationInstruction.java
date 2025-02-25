package com.rae.creatingspace.api.squedule.instruction;

import com.google.common.collect.ImmutableList;
import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import com.rae.creatingspace.legacy.utilities.CSUtil;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

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

    private void updateDataFromId() {
        int id = intData("intId");
        if (planets != null && id < planets.size()) {
            data.putString("destination", planets.get(id).toString());
        }
    }

    @Override
    public List<Component> getSecondLineTooltip(int slot) {
        return ImmutableList.of(Lang.translateDirect("schedule.instruction.filter_edit_box"),
                Lang.translateDirect("schedule.instruction.filter_edit_box_1")
                        .withStyle(ChatFormatting.GRAY),
                Lang.translateDirect("schedule.instruction.filter_edit_box_2")
                        .withStyle(ChatFormatting.DARK_GRAY),
                Lang.translateDirect("schedule.instruction.filter_edit_box_3")
                        .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public Pair<ItemStack, Component> getSummary() {
        updateDataFromId();
        return Pair.of(AllBlocks.TRACK_STATION.asStack(), Components.translatable(textData("Text")));
    }

    @Override
    public List<Component> getTitleAs(String type) {
        updateDataFromId();
        return ImmutableList.of(Lang.translateDirect("schedule." + type + "." + getId().getPath() + ".summary")
                .withStyle(ChatFormatting.GOLD), Lang.translateDirect("generic.in_quotes", Components.translatable(textData("destination"))));
    }


    @Override
    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
        planets = getPlanets();
        builder.addSelectionScrollInput(0, 121, (s, t) -> {
                    s.forOptions(planets.stream().map(r -> Component.translatable(r.toString())).toList());
                },
                "intId");
        builder.addIntegerTextInput(125, 30,(editBox,tooltipArea) ->{},"XCoord");
        builder.addIntegerTextInput(160, 30,(editBox,tooltipArea) ->{},"ZCoord");
    }

    @NotNull
    private static List<ResourceLocation> getPlanets() {
        return CSDimensionUtil.getPlanets();
    }

    public Vec2 getXYCoord() {
        updateDataFromId();
        int X = CSUtil.isInteger(data.getString("XCoord"))?Integer.parseInt(data.getString("XCoord")):0;
        int Z =  CSUtil.isInteger(data.getString("ZCoord"))?Integer.parseInt(data.getString("ZCoord")):0;
        return new Vec2(X,Z);
    }
}