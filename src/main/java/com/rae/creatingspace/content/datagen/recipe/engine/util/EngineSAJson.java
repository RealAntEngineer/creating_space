package com.rae.creatingspace.content.datagen.recipe.engine.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rae.creatingspace.content.datagen.recipe.engine.model.EnginePartType;
import com.rae.creatingspace.content.datagen.recipe.engine.model.MaterialLevel;

/**
 * Fluent builder for Create-style Sequenced Assembly JSONs.
 * Produces fully structured, stable JSON matching Create 6.0.6 format.
 *
 * Example usage:
 *   EngineSAJson.Builder b = EngineSAJson.builder();
 *   b.blueprintIngredient(1)
 *    .transitionalItem("creatingspace:incomplete_example")
 *    .result("creatingspace:example")
 *    .loops(2)
 *    .deploy("creatingspace:incomplete_example", "minecraft:iron_ingot");
 */
public class EngineSAJson {

    private final JsonObject root;

    private EngineSAJson(JsonObject root) {
        this.root = root;
    }

    public JsonObject json() {
        return root;
    }

    /** Creates a new fluent builder. */
    public static Builder builder() {
        return new Builder();
    }

    // ========================================================================
    // Builder
    // ========================================================================
    public static class Builder {

        private final JsonObject root = new JsonObject();
        private final JsonArray sequence = new JsonArray();
        private final JsonArray keepNbt = new JsonArray();
        private final JsonArray results = new JsonArray();
        private final JsonObject ingredient = new JsonObject();
        private final JsonObject components = new JsonObject();
        private final JsonObject engineRecipeData = new JsonObject();

        private String transitionalItem = null;
        private int loops = 1;
        private boolean ingredientExplicitlySet = false;

        // --------------------------------------------------------------------
        // Constructor
        // --------------------------------------------------------------------
        public Builder() {
            root.addProperty("type", "create:sequenced_assembly");
            keepNbt.add("engineRecipeData");
            keepNbt.add("blockEntity");
        }

        // --------------------------------------------------------------------
        // Basic fields
        // --------------------------------------------------------------------
        public Builder loops(int loops) {
            this.loops = loops;
            return this;
        }

        public Builder transitionalItem(String itemId) {
            this.transitionalItem = itemId;
            return this;
        }

        public Builder result(String id, int count) {
            JsonObject result = new JsonObject();
            result.addProperty("id", id);
            if (count > 1) result.addProperty("count", count);
            results.add(result);
            return this;
        }

        public Builder result(String id) {
            return result(id, 1);
        }


        // --------------------------------------------------------------------
        // Ingredient setup (engine blueprint w/ custom data)
        // --------------------------------------------------------------------
        public Builder blueprintIngredient(int materialLevel) {
            resetIngredientState();
            ingredient.addProperty("items", "creatingspace:engine_blueprint");
            ingredient.addProperty("type", "neoforge:components");

            JsonObject customData = new JsonObject();
            customData.add("engineRecipeData", engineRecipeData);
            components.add("minecraft:custom_data", customData);
            ingredient.add("components", components);

            engineRecipeData.addProperty("materialLevel", materialLevel);
            return this;
        }

        public Builder putInEngineRecipeData(String key, String value) {
            engineRecipeData.addProperty(key, value);
            return this;
        }

        // --------------------------------------------------------------------
        // Simple ingredient helpers (non-material recipes)
        // --------------------------------------------------------------------
        public Builder ingredientItem(String id) {
            if (id == null || id.isBlank()) return this;
            resetIngredientState();
            ingredient.addProperty("item", id);
            ingredientExplicitlySet = true;
            return this;
        }

        /** Inserts a pre-built ingredient JSON (used for static recipes). */
        public Builder rawIngredient(JsonObject ingredientJson) {
            if (ingredientJson == null || ingredientJson.entrySet().isEmpty()) return this;
            resetIngredientState();
            ingredientJson.entrySet().forEach(e -> ingredient.add(e.getKey(), e.getValue()));
            ingredientExplicitlySet = true;
            return this;
        }

        // --------------------------------------------------------------------
        // Sequence step helpers (SAFE)
        // --------------------------------------------------------------------
        /** Adds a standard deploying step. */
        public Builder deploy(String base, String addition) {
            if (isEmpty(base) || isEmpty(addition)) {
                System.err.println("[EngineSAJson] Skipping deploy step with empty ingredient: base=" + base + ", addition=" + addition);
                return this;
            }

            JsonObject step = new JsonObject();
            step.addProperty("type", "create:deploying");

            JsonArray ingredients = new JsonArray();
            ingredients.add(itemRef(base));
            ingredients.add(itemRef(addition));

            step.add("ingredients", ingredients);
            step.add("results", singletonResult(base));
            sequence.add(step);
            return this;
        }

        /** Adds a deploying step that preserves machine NBT data. */
        public Builder deployMach(String base, String addition) {
            if (isEmpty(base) || isEmpty(addition)) {
                System.err.println("[EngineSAJson] Skipping deployMach step with empty ingredient: base=" + base + ", addition=" + addition);
                return this;
            }

            JsonObject step = new JsonObject();
            step.addProperty("type", "create:deploying");

            JsonArray machNbt = new JsonArray();
            machNbt.add("engineRecipeData");
            machNbt.add("blockEntity");
            step.add("machNbt", machNbt);

            JsonArray ingredients = new JsonArray();
            ingredients.add(itemRef(base));
            ingredients.add(itemRef(addition));

            step.add("ingredients", ingredients);
            step.add("results", singletonResult(base));
            sequence.add(step);
            return this;
        }

        // --------------------------------------------------------------------
        // Final assembly
        // --------------------------------------------------------------------
        public JsonObject build() {
            root.addProperty("loops", loops);
            root.add("keepNbt", keepNbt);

            // ingredient — only add if we have explicit data or it has contents
            if (ingredientExplicitlySet || !ingredient.entrySet().isEmpty()) {
                root.add("ingredient", ingredient);
            }

            root.add("results", results);
            root.add("sequence", sequence);

            if (transitionalItem != null) {
                JsonObject t = new JsonObject();
                t.addProperty("id", transitionalItem);
                root.add("transitional_item", t);
            }

            return root;
        }

        // --------------------------------------------------------------------
        // Utility helpers
        // --------------------------------------------------------------------
        private static JsonObject itemRef(String id) {
            JsonObject obj = new JsonObject();
            obj.addProperty("item", id);
            return obj;
        }

        private static JsonArray singletonResult(String id) {
            JsonArray arr = new JsonArray();
            JsonObject obj = new JsonObject();
            obj.addProperty("id", id);
            arr.add(obj);
            return arr;
        }

        private static boolean isEmpty(String s) {
            if (s == null) return true;
            String t = s.trim();
            if (t.isEmpty()) return true;
            if (t.endsWith(":") || t.endsWith("_")) return true;
            return !t.contains(":");
        }

        private void resetIngredientState() {
            ingredient.entrySet().clear();
            components.entrySet().clear();
            engineRecipeData.entrySet().clear();
            ingredientExplicitlySet = false;
        }

        // --------------------------------------------------------------------
        // Transitional Item ID Helpers
        // --------------------------------------------------------------------
        /** Builds a transitional item ID with no material key. */
        public static String incompleteItemId(String baseName) {
            return "creatingspace:incomplete_" + baseName;
        }

        /** Builds a transitional item ID with a material key included. */
        public static String incompleteMaterialItemId(MaterialLevel mat, String baseName) {
            return "creatingspace:incomplete_" + mat.key() + "_" + baseName;
        }

        /** Simple helper to produce a `{"item": id}` ingredient JSON. */
        public static JsonObject itemIngredient(String id) {
            JsonObject obj = new JsonObject();
            obj.addProperty("item", id);
            return obj;
        }
    }
}
