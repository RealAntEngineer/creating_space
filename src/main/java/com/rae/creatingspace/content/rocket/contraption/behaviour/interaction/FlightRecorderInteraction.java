package com.rae.creatingspace.content.rocket.contraption.behaviour.interaction;

import com.rae.creatingspace.configs.CSCfgServer;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.content.rocket.RocketContraptionEntity;
import com.rae.creatingspace.content.rocket.contraption.RocketContraption;
import com.rae.creatingspace.content.rocket.engine.design.PropellantType;
import com.rae.creatingspace.legacy.utilities.CSUtil;
import com.rae.creatingspace.legacy.utilities.data.FlightDataHelper;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;


import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static com.rae.creatingspace.content.event.DataEventHandler.getSideAwareRegistry;
import static com.rae.creatingspace.content.rocket.RocketContraptionEntity.addToConsumableFluids;
import static com.rae.creatingspace.content.rocket.RocketContraptionEntity.getMassMap;

public class FlightRecorderInteraction extends MovingInteractionBehaviour {
    private static final boolean shouldBeDisplayed = false;

    String tradKey = "creatingspace.overlay.flight_recorder.";

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {

        if (player instanceof ServerPlayer serverPlayer) {
            boolean sneaking = serverPlayer.isShiftKeyDown();
            if (contraptionEntity instanceof RocketContraptionEntity rocket) {
                FlightDataHelper.RocketAssemblyData lastAssemblyData = rocket.assemblyData;
                if (sneaking) {
                    RocketContraption contraption     = (RocketContraption) rocket.getContraption();
                    float             totalThrust     = 0;
                    float             totalFluidMass = 0;
                    IFluidHandler     fluidHandler    = contraption.getStorage().getFluids();
                    int               nbrOfTank       = fluidHandler.getTanks();
                    //both research of every consumable fluid and addition of the total consumption
                    float totalTheoreticalConsumption = 0;
                    //TODO that could be in the inventory manager of the rocket -> 1.8
                    for (PropellantType combination : contraption.getTPTFluidConsumption().keySet()) {
                        RocketContraption.ConsumptionInfo info = contraption.getTPTFluidConsumption().get(combination);
                        //mean speed of ejected gasses for the fluid -> need to be done for a couple of tag -> ox/fuel
                        for (float consumption :
                                info.propellantConsumption().values()) {
                            totalTheoreticalConsumption += consumption;
                        }
                        totalThrust += info.partialThrust();

                        //initialize if not present
                        for (TagKey<Fluid> fluid :
                                combination.getPropellantRatio().keySet()) {
                            addToConsumableFluids(rocket, fluid);
                        }

                    }

                    float meanVe = totalThrust > 0 ? totalThrust / totalTheoreticalConsumption : 0;
                    // massForEachPropellant is just to determine if there is enough fluid,
                    // need to be called after the consumedFluids map is build
                    HashMap<TagKey<Fluid>, Integer> massForEachPropellant =
                            getMassMap(rocket);


                    for (int i = 0; i < nbrOfTank; i++) {
                        FluidStack fluidInTank = fluidHandler.getFluidInTank(i);
                        FluidType  fluidType   = fluidInTank.getFluid().getFluidType();

                        totalFluidMass += (float) (fluidInTank.getAmount() * fluidType.getDensity()) / 1000;
                    }
                    float initialPropellantMass = 0;
                    for (int mass : massForEachPropellant.values()) {
                        initialPropellantMass += mass;
                    }
                    float inertFluidMass = totalFluidMass - initialPropellantMass;
                    float emptyMass = inertFluidMass + contraption.getDryMass();
                    serverPlayer.sendSystemMessage(
                            Component.literal(
                                    "current empty mass : " + emptyMass
                            )
                    );
                    serverPlayer.sendSystemMessage(
                            Component.literal(
                                    "(dry mass : " + contraption.getDryMass() + "kg | inert fluid : " + inertFluidMass + "kg)"
                            )
                    );
                    serverPlayer.sendSystemMessage(
                            Component.literal("Mean Isp : " + meanVe/9.81 +"s")
                    );
                    serverPlayer.sendSystemMessage(
                            Component.literal("Propellant mass : " + initialPropellantMass+"kg")
                    );
                    serverPlayer.sendSystemMessage(
                            Component.literal("Computed deltaV : " + meanVe * Math.log((emptyMass + inertFluidMass + initialPropellantMass)/(emptyMass + inertFluidMass)) + "m/s")
                    );
                } else if (lastAssemblyData != null && lastAssemblyData.hasFailed()) {
                    if (lastAssemblyData.propellantStatusData().status().isFailReason) {
                        serverPlayer.sendSystemMessage(Component.translatable(tradKey + "propellant_status." +
                                        String.valueOf(
                                                lastAssemblyData.propellantStatusData()
                                                        .status()).toLowerCase(Locale.ROOT)
                                , shouldBeDisplayed)

                        );
                        for (TagKey<Fluid> fluidTagKey : lastAssemblyData.propellantStatusData().consumedMassForEachPropellant().keySet()) {
                            Integer consumedMass = lastAssemblyData.propellantStatusData().consumedMassForEachPropellant().get(fluidTagKey);
                            Integer fluidMass    = lastAssemblyData.propellantStatusData().massForEachPropellant().get(fluidTagKey);

                            if (fluidMass == null) {
                                fluidMass = 0;
                            }
                            if (CSConfigs.SERVER.recorder_measurement.get().equals(CSCfgServer.Measurement.MASS)) {
                                serverPlayer.sendSystemMessage(
                                        Component.translatable("fluid." + fluidTagKey.location().toLanguageKey())
                                                .append(" ")
                                                .append(Component.literal(CSUtil.scientificNbrFormatting((float) fluidMass / 1000, 5))
                                                        .append(Component.translatable("creatingspace.science.unit.metric_ton"))
                                                        .withStyle(consumedMass >= fluidMass ?
                                                                ChatFormatting.DARK_RED :
                                                                ChatFormatting.DARK_GREEN))
                                                .append(Component.literal(" / " +
                                                                CSUtil.scientificNbrFormatting((float) consumedMass / 1000, 5))
                                                        .append(Component.translatable("creatingspace.science.unit.metric_ton"))
                                                        .withStyle(ChatFormatting.GOLD))
                                        , shouldBeDisplayed);
                            } else if (CSConfigs.SERVER.recorder_measurement.get().equals(CSCfgServer.Measurement.VOLUMETRIC)) {
                                AtomicReference<Fluid> fluidRef = new AtomicReference<>();

                                getSideAwareRegistry(Registries.FLUID).entrySet().forEach(
                                        resourceKeyFluidEntry -> {
                                            if (resourceKeyFluidEntry.getValue().is(fluidTagKey)) {
                                                fluidRef.set(resourceKeyFluidEntry.getValue());
                                            }
                                        }
                                );
                                if (fluidRef.get() == null) {
                                    serverPlayer.sendSystemMessage(Component.literal("Warning : failed to find a fluid in game data"), shouldBeDisplayed);
                                } else {
                                    float fluidVolume = (float) (fluidMass / fluidRef.get().getFluidType().getDensity()); //in minecraft's bucket
                                    serverPlayer.sendSystemMessage(Component.translatable("fluid." + fluidTagKey.location().toLanguageKey())
                                                    .append(" ")
                                                    .append(Component.literal(CSUtil.scientificNbrFormatting(fluidVolume, 5))
                                                            .append(Component.literal("B"))
                                                            .withStyle(consumedMass >= fluidMass ?
                                                                    ChatFormatting.DARK_RED :
                                                                    ChatFormatting.DARK_GREEN))
                                                    .append(Component.literal(" / " +
                                                                    CSUtil.scientificNbrFormatting((float) consumedMass / fluidRef.get().getFluidType().getDensity(), 5))
                                                            .append(Component.literal("B"))
                                                            .withStyle(ChatFormatting.GOLD))
                                            , shouldBeDisplayed);
                                }
                            }
                        }
                    }
                    if (lastAssemblyData.thrust() < lastAssemblyData.weight()) {
                        serverPlayer.sendSystemMessage(Component.translatable(tradKey + "not_enough_thrust"), shouldBeDisplayed);
                        serverPlayer.sendSystemMessage(Component.translatable("creatingspace.overlay.flight_recorder.thrust1").append(Component.literal(" : " + CSUtil.scientificNbrFormatting(lastAssemblyData.thrust(), 3)))
                                .append(Component.translatable("creatingspace.science.unit.newton")), shouldBeDisplayed);
                        serverPlayer.sendSystemMessage(Component.translatable("creatingspace.overlay.flight_recorder.thrust2").append(Component.literal(" : " + CSUtil.scientificNbrFormatting(lastAssemblyData.weight(), 3))
                                .append(Component.translatable("creatingspace.science.unit.newton"))), shouldBeDisplayed);

                    }

                } else {
                    serverPlayer.sendSystemMessage(Component.translatable(tradKey + "no_failure"), shouldBeDisplayed);
                }
            }
        }
        return super.handlePlayerInteraction(player, activeHand, localPos, contraptionEntity);
    }
}