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

        for (EnginePartType type : EnginePartType.values()) {
            type.applicableMaterials()
                    .map(mat -> scheduleRecipe(cache, type, mat))
                    .forEach(futures::add);

            if (!type.isMaterialDependent()) {
                futures.add(scheduleRecipe(cache, type, null));
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

        if (type.isMaterialDependent()) {
            if (mat == null) {
                throw new IllegalArgumentException("Material-dependent recipe requires material level for " + type.name());
            }

            // Add proper ingredient with materialLevel in custom data
            b.blueprintIngredient(mat.level());
        } else {
            b.rawIngredient(EngineSAJson.Builder.itemIngredient("creatingspace:engine_blueprint"));
        }

        b.result(type.resultItemId(mat));
        b.transitionalItem(type.transitionalItemId(mat));

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
        Path base = Path.of("data", "creatingspace", "recipe", type.baseFolder());
        if (type.isMaterialDependent()) {
            if (mat == null) {
                throw new IllegalArgumentException("Material-dependent recipe requires material level for " + type.name());
            }
            base = base.resolve(mat.folderName());
        }

        Path file = Path.of(type.recipeSubPath() + ".json");
        return output.getOutputFolder().resolve(base).resolve(file);
    }

    // ------------------------------------------------------------------------
    // Save Utility
    // ------------------------------------------------------------------------

    private static CompletableFuture<?> saveJson(CachedOutput cache, JsonObject json, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        return DataProvider.saveStable(cache, GSON.toJsonTree(json), path);
    }

    private CompletableFuture<?> scheduleRecipe(CachedOutput cache, EnginePartType type, MaterialLevel mat) {
        try {
            JsonObject json = buildRecipe(type, mat);
            Path path = recipePath(type, mat);
            return saveJson(cache, json, path);
        } catch (Exception e) {
            String context = type.isMaterialDependent()
                    ? " at " + (mat != null ? mat.key() : "<missing material>")
                    : " (static)";
            throw new RuntimeException("Failed generating recipe for " + type.name() + context, e);
        }
    }

    @Override
    public String getName() {
        return "Creating Space Engine Sequenced Assembly Recipes";
    }
}
