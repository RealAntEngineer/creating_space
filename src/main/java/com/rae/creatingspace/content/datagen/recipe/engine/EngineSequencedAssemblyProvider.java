package com.rae.creatingspace.content.datagen.recipe.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.rae.creatingspace.content.datagen.recipe.engine.model.EnginePartType;
import com.rae.creatingspace.content.datagen.recipe.engine.model.MaterialLevel;
import com.rae.creatingspace.content.datagen.recipe.engine.util.EngineSAJson;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Generates all Create-style Sequenced Assembly recipes for Creating Space.
 * Iterates over MaterialLevel × EnginePartType and emits JSONs into:
 *
 *   data/creatingspace/recipe/engine_recipe/...
 *
 * Supports both material-specific and static recipes.
 */
public class EngineSequencedAssemblyProvider implements DataProvider {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private final PackOutput output;

    public EngineSequencedAssemblyProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        // Material-dependent recipes
        for (MaterialLevel mat : MaterialLevel.values()) {
            for (EnginePartType type : EnginePartType.values()) {
                if (!type.isMaterialDependent()) continue;

                try {
                    JsonObject json = buildRecipe(type, mat);
                    Path path = recipePath(type, mat);
                    futures.add(saveJson(cache, json, path));
                } catch (Exception e) {
                    throw new RuntimeException("Failed generating recipe for " + type.name() + " at " + mat.key(), e);
                }
            }
        }

        // Non-material recipes
        for (EnginePartType type : EnginePartType.values()) {
            if (type.isMaterialDependent()) continue;

            try {
                JsonObject json = buildRecipe(type, null);
                Path path = recipePath(type, null);
                futures.add(saveJson(cache, json, path));
            } catch (Exception e) {
                throw new RuntimeException("Failed generating static recipe for " + type.name(), e);
            }
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    // ------------------------------------------------------------------------
    // Recipe Construction
    // ------------------------------------------------------------------------

    private JsonObject buildRecipe(EnginePartType type, MaterialLevel mat) {
        EngineSAJson.Builder b = EngineSAJson.builder()
                .loops(type.loops());

        // --------------------------------------------------------------------
        // Material-dependent recipes
        // --------------------------------------------------------------------
        if (type.isMaterialDependent() && mat != null) {
            // Add proper ingredient with materialLevel in custom data
            b.blueprintIngredient(mat.level());

            // Result item — same name as the recipe file
            String partName = type.recipeSubPath()
                    .substring(type.recipeSubPath().lastIndexOf('/') + 1);
            b.result(getResultItemId(type, mat));

            // Transitional item naming rules
            b.transitionalItem(getTransitionalItemId(type, mat));


            // --------------------------------------------------------------------
            // Non-material recipes (engine, duplicate_blueprint)
            // --------------------------------------------------------------------
        } else {
            b.result("creatingspace:" + type.recipeSubPath());
            b.transitionalItem("creatingspace:engine_blueprint");

            // simple ingredient reference to avoid {}
            JsonObject ing = new JsonObject();
            ing.addProperty("item", "creatingspace:engine_blueprint");
            b.rawIngredient(ing);
        }

        // --------------------------------------------------------------------
        // Extra data & sequence definition
        // --------------------------------------------------------------------
        type.appendExtraEngineData(b);
        type.buildSequence(b, mat);

        // --------------------------------------------------------------------
        // Build final JSON
        // --------------------------------------------------------------------
        return b.build();
    }


    // ------------------------------------------------------------------------
    // Path Resolution
    // ------------------------------------------------------------------------

    private Path recipePath(EnginePartType type, MaterialLevel mat) {
        StringBuilder sb = new StringBuilder("data/creatingspace/recipe/");
        sb.append(type.baseFolder()).append("/");

        if (type.isMaterialDependent() && mat != null) {
            sb.append(mat.folderName()).append("/");
        }

        sb.append(type.recipeSubPath()).append(".json");
        return output.getOutputFolder().resolve(Path.of(sb.toString()));
    }

    // ------------------------------------------------------------------------
    // Save Utility
    // ------------------------------------------------------------------------

    private static CompletableFuture<?> saveJson(CachedOutput cache, JsonObject json, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        return DataProvider.saveStable(cache, GSON.toJsonTree(json), path);
    }

    private static String getResultItemId(EnginePartType part, MaterialLevel mat) {
        // These parts always produce generic item IDs (no material prefix)
        return switch (part) {
            case COMBUSTION_CHAMBER -> "creatingspace:combustion_chamber";
            case BELL_NOZZLE -> "creatingspace:bell_nozzle";
            case AEROSPIKE_PLUG -> "creatingspace:aerospike_plug";
            case FUEL_RICH_STAGED_CYCLE, FULL_FLOW_STAGED_CYCLE, OPEN_CYCLE, OX_RICH_STAGED_CYCLE -> "creatingspace:power_pack";
            case DUPLICATE_BLUEPRINT -> "creatingspace:duplicate_blueprint";
            case ENGINE -> "creatingspace:engine";
            default -> mat.itemId(
                    part.recipeSubPath().substring(part.recipeSubPath().lastIndexOf('/') + 1)
            );
        };
    }

    private static String getTransitionalItemId(EnginePartType type, MaterialLevel mat) {
        return switch (type) {
            // Exhaust pack members all use a shared transitional
            case COMBUSTION_CHAMBER -> "creatingspace:incomplete_combustion_chamber";
            case BELL_NOZZLE -> "creatingspace:incomplete_bell_nozzle";
            case AEROSPIKE_PLUG -> "creatingspace:incomplete_aerospike_plug";
            case FUEL_RICH_STAGED_CYCLE, FULL_FLOW_STAGED_CYCLE, OPEN_CYCLE, OX_RICH_STAGED_CYCLE -> "creatingspace:incomplete_power_pack";
            case DUPLICATE_BLUEPRINT -> "creatingspace:engine_blueprint";
            case ENGINE -> "creatingspace:engine_blueprint";

            // Default material-dependent transitional logic
            default -> {
                String partName = type.recipeSubPath()
                        .substring(type.recipeSubPath().lastIndexOf('/') + 1);
                if (partName.equals("injector_grid") || partName.equals("turbine"))
                    yield EngineSAJson.Builder.incompleteMaterialItemId(mat, partName);
                yield EngineSAJson.Builder.incompleteItemId(partName);
            }
        };
    }

    @Override
    public String getName() {
        return "Creating Space Engine Sequenced Assembly Recipes";
    }
}
