package com.rae.creatingspace.legacy.saved;


import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

import static com.rae.creatingspace.init.MiscInit.getSyncedExhaustPackRegistry;
import static com.rae.creatingspace.init.MiscInit.getSyncedPowerPackRegistry;

public class DesignCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("addAllDesigns")
                .requires(source -> source.hasPermission(2)) // Requires operator level permission
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    addAllDesigns(player);
                    return Command.SINGLE_SUCCESS;
                }));

        dispatcher.register(Commands.literal("clearAllDesigns")
                .requires(source -> source.hasPermission(2)) // Requires operator level permission
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    clearAllDesigns(player);
                    return Command.SINGLE_SUCCESS;
                }));
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
