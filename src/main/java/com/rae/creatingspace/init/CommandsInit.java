package com.rae.creatingspace.init;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import com.rae.creatingspace.init.worldgen.DimensionInit;
import com.rae.creatingspace.legacy.saved.UnlockedDesignManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.rae.creatingspace.init.MiscInit.getSyncedExhaustPackRegistry;
import static com.rae.creatingspace.init.MiscInit.getSyncedPowerPackRegistry;

public class CommandsInit {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Root command: /crowns
        dispatcher.register(Commands.literal("creatingspace")
                .requires(source -> source.hasPermission(2)) // Operator permission for all subcommands
                .then(Commands.literal("addAllDesigns")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            addAllDesigns(player);
                            return Command.SINGLE_SUCCESS;
                        }))

                .then(Commands.literal("clearAllDesigns")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            clearAllDesigns(player);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("dumpGravityList")
                        .executes(
                                context -> {
                                    for (ResourceLocation planet :CSDimensionUtil.getPlanets()){
                                        context.getSource().sendSystemMessage(
                                                Component.translatable(
                                                        planet.getNamespace() + ":"+ planet.getPath()
                                                        ).append(
                                                Component.literal(
                                                        " : "+CSDimensionUtil.gravity(planet)+ " m/s²"
                                                )));

                                    }

                                    return Command.SINGLE_SUCCESS;
                                }))
        );
    }

    private static void addAllDesigns(ServerPlayer player) {
        // Assuming you have a list of all possible designs somewhere
        List<ResourceLocation> allPPDesign = getSyncedPowerPackRegistry().keySet().stream().toList();
        List<ResourceLocation> allEPDesign = getSyncedExhaustPackRegistry().keySet().stream().toList();

        for (ResourceLocation design : allPPDesign) {
            UnlockedDesignManager.addPowerPackForPlayer(player, design);
            // Add other types of designs if needed
        }
        for (ResourceLocation design : allEPDesign) {
            UnlockedDesignManager.addExhaustForPlayer(player, design);
        }
        player.displayClientMessage(Component.literal("All designs added!"), false);
    }

    private static void clearAllDesigns(ServerPlayer player) {
        UnlockedDesignManager.clearAllExhaustDesignsForPlayer(player);
        UnlockedDesignManager.clearAllPowerPackDesignsForPlayer(player);
        UnlockedDesignManager.playerLogin(player);
        // Clear other types of designs if needed
        player.displayClientMessage(Component.literal("All designs cleared!"), false);
    }
}
