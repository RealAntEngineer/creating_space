package com.rae.creatingspace.server.contraption.behaviour.interaction;

import com.rae.creatingspace.configs.CSCfgClient;
import com.rae.creatingspace.configs.CSConfigs;
import com.rae.creatingspace.server.entities.RocketContraptionEntity;
import com.rae.creatingspace.utilities.CSUtil;
import com.rae.creatingspace.utilities.data.FlightDataHelper;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class FlightRecorderInteraction extends MovingInteractionBehaviour {
    String tradKey = "creatingspace.overlay.flight_recorder.";

    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {

        if (player instanceof ServerPlayer) {
            player.sendSystemMessage(Component.literal("coucou"));

            if (contraptionEntity instanceof RocketContraptionEntity rocket) {
                FlightDataHelper.RocketAssemblyData lastAssemblyData = rocket.assemblyData;
                if (lastAssemblyData != null) {
                    if (lastAssemblyData.hasFailed()) {
                        if (lastAssemblyData.propellantStatusData().status().isFailReason) {
                            player.sendSystemMessage(Component.translatable(tradKey + "propellant_status." +
                                    String.valueOf(
                                            lastAssemblyData.propellantStatusData()
                                                    .status()).toLowerCase(Locale.ROOT))

                            );
                            for (TagKey<Fluid> fluidTagKey : lastAssemblyData.propellantStatusData().consumedMassForEachPropellant().keySet()) {
                                Integer consumedMass = lastAssemblyData.propellantStatusData().consumedMassForEachPropellant().get(fluidTagKey);
                                Integer fluidMass = lastAssemblyData.propellantStatusData().massForEachPropellant().get(fluidTagKey);

                                if (fluidMass == null) {
                                    fluidMass = 0;
                                }
                                if (CSConfigs.CLIENT.recorder_measurement.get().equals(CSCfgClient.Measurement.MASS)) {
                                    player.sendSystemMessage(
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
                                    );
                                } else if (CSConfigs.CLIENT.recorder_measurement.get().equals(CSCfgClient.Measurement.VOLUMETRIC)) {
                                    AtomicReference<Fluid> fluidRef = new AtomicReference<>();

                                    ForgeRegistries.FLUIDS.getEntries().forEach(
                                            resourceKeyFluidEntry -> {
                                                if (resourceKeyFluidEntry.getValue().is(fluidTagKey)) {
                                                    fluidRef.set(resourceKeyFluidEntry.getValue());
                                                }
                                            }
                                    );
                                    if (fluidRef.get() == null) {
                                        player.sendSystemMessage(Component.literal("Warning : failed to find a fluid in game data"));
                                    } else {
                                        float fluidVolume = (float) (fluidMass / fluidRef.get().getFluidType().getDensity()); //in minecraft's bucket
                                        player.sendSystemMessage(Component.translatable("fluid." + fluidTagKey.location().toLanguageKey())
                                                .append(" ")
                                                .append(Component.literal(CSUtil.scientificNbrFormatting((float) fluidVolume, 5))
                                                        .append(Component.literal("B"))
                                                        .withStyle(consumedMass >= fluidMass ?
                                                                ChatFormatting.DARK_RED :
                                                                ChatFormatting.DARK_GREEN))
                                                .append(Component.literal(" / " +
                                                                CSUtil.scientificNbrFormatting((float) consumedMass / fluidRef.get().getFluidType().getDensity(), 5))
                                                        .append(Component.literal("B"))
                                                        .withStyle(ChatFormatting.GOLD))
                                        );
                                    }
                                }
                            }
                        }
                        if (lastAssemblyData.thrust() < lastAssemblyData.weight()) {
                            player.sendSystemMessage(Component.translatable(tradKey + "not_enough_thrust"));
                            player.sendSystemMessage(Component.translatable("creatingspace.overlay.flight_recorder.thrust1"));
                            player.sendSystemMessage(Component.literal(" : " + CSUtil.scientificNbrFormatting(lastAssemblyData.thrust(), 3))
                                    .append(Component.translatable("creatingspace.science.unit.newton")));
                            player.sendSystemMessage(Component.translatable("creatingspace.overlay.flight_recorder.thrust2"));
                            player.sendSystemMessage(Component.literal(" : " + CSUtil.scientificNbrFormatting(lastAssemblyData.weight(), 3))
                                    .append(Component.translatable("creatingspace.science.unit.newton")));

                        }

                    } else {
                        player.sendSystemMessage(Component.translatable(tradKey + "no_failure"));
                    }
                } else {
                    player.sendSystemMessage(Component.translatable(tradKey + "no_flight"));
                }
            }
        }
        return super.handlePlayerInteraction(player, activeHand, localPos, contraptionEntity);
    }
}
