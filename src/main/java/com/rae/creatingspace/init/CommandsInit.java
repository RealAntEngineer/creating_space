package com.rae.creatingspace.init;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.JsonOps;
import com.rae.creatingspace.content.planets.CSDimensionUtil;
import com.rae.creatingspace.content.saved.UnlockedDesignManager;
import com.rae.creatingspace.content.worldgen.debug.Test;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

import java.io.File;
import java.util.List;

import static com.rae.creatingspace.content.worldgen.debug.DensityFunctionVisualizer.render2D;
import static com.rae.creatingspace.init.MiscInit.getSyncedExhaustPackRegistry;
import static com.rae.creatingspace.init.MiscInit.getSyncedPowerPackRegistry;

public class CommandsInit {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Root command: /creatingspace
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
                                    for (ResourceLocation planet : CSDimensionUtil.getPlanets()){
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
                .then(Commands.literal("renderDensity")
                        .then(Commands.argument("id", ResourceLocationArgument.id())
                                .executes(context -> {

                                    ResourceLocation id = ResourceLocationArgument.getId(context, "id");
                                    CommandSourceStack source = context.getSource();

                                    renderDensityFunction(source, id);

                                    try {
                                        Test.main(new String[]{});
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }

    private static void renderDensityFunction(CommandSourceStack source, ResourceLocation id) {

        try {

            var ops = RegistryOps.create(
                    com.mojang.serialization.JsonOps.INSTANCE,
                    source.registryAccess()
            );
            // Build JSON reference to the density function

            var json = new JsonPrimitive(id.toString());
            // Decode using HOLDER_HELPER_CODEC
            var result = DensityFunction.HOLDER_HELPER_CODEC
                    .parse(ops, json)
                    .resultOrPartial(error -> {
                        source.sendFailure(Component.literal("Decode error: " + error));
                    });

            if (result.isEmpty()) {
                source.sendFailure(Component.literal("Failed to load density function: " + id));
                return;
            }

            DensityFunction function = result.get();
            function = function.mapAll(f -> f);

            // Render to file

            File file = new File("density/" + id.getPath() + ".png");

            render2D(function, 512, 0, file);

            source.sendSuccess(() ->
                            Component.literal("Rendered density function to " + file.getAbsolutePath()),
                    true
            );

        } catch (Exception e) {
            source.sendFailure(Component.literal("Error: " + e.getMessage()));
        }
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
