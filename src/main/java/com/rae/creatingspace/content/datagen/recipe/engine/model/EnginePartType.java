package com.rae.creatingspace.content.datagen.recipe.engine.model;

import com.rae.creatingspace.content.datagen.recipe.engine.util.EngineSAJson;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.rae.creatingspace.CreatingSpace.resource;

/**
 * Enumerates all engine-related recipe archetypes.
 * Each type defines:
 *  - Output item
 *  - Transitional item
 *  - Loop count
 *  - Whether it's material-dependent
 *  - Optional extra engineRecipeData fields
 *  - Template sequence builder for deploying steps
 *
 *  Recipes fall into these groups:
 *   - Exhaust Pack (combustion_chamber, bell_nozzle, aerospike_plug)
 *   - Power Pack (injector_grid, turbine, power cycle variants)
 *   - Utility (engine, duplicate_blueprint)
 */
public enum EnginePartType {
    // ---------- Exhaust Pack ----------
    COMBUSTION_CHAMBER(
            "engine_recipe/exhaust_pack", "combustion_chamber/combustion_chamber", true, 1
    ) {
        @Override
        public void buildSequence(EngineSAJson.Builder b, MaterialLevel mat) {
            String base = EngineSAJson.Builder.incompleteItemId("combustion_chamber");
            b.deploy(base, mat.itemId("engine_wall"));
            b.deploy(base, mat.itemId("rib"));
            b.deploy(base, mat.itemId("engine_wall"));
            b.deploy(base, mat.itemId("rib"));
        }
    },

    BELL_NOZZLE(
            "engine_recipe/exhaust_pack", "exhaust/bell_nozzle", true, 1
    ) {
        @Override
        public void buildSequence(EngineSAJson.Builder b, MaterialLevel mat) {
            String base = EngineSAJson.Builder.incompleteItemId("bell_nozzle");
            b.deploy(base, mat.itemId("rib"));
            repeatDeploy(b, base, mat.itemId("engine_wall"), 3);
            b.deploy(base, mat.itemId("rib"));
        }

        @Override
        public void appendExtraEngineData(EngineSAJson.Builder b) {
            b.putInEngineRecipeData("exhaustPackType", resource("bell_nozzle").toString());
        }
    },

    AEROSPIKE_PLUG(
            "engine_recipe/exhaust_pack", "exhaust/aerospike_plug", true, 1
    ) {
        @Override
        public void buildSequence(EngineSAJson.Builder b, MaterialLevel mat) {
            String base = EngineSAJson.Builder.incompleteItemId("aerospike_plug");
            b.deploy(base, "create:shaft");
            b.deploy(base, mat.itemId("engine_wall"));
            b.deploy(base, mat.itemId("engine_wall"));
            b.deploy(base, mat.itemId("engine_wall"));
            b.deploy(base, mat.itemId("rib"));
        }

        @Override
        public void appendExtraEngineData(EngineSAJson.Builder b) {
            b.putInEngineRecipeData("exhaustPackType", resource("aerospike").toString());
        }
    },

    // ---------- Power Pack ----------
    INJECTOR_GRID(
            "engine_recipe/power_pack", "injector_grid/injector_grid", true, 2
    ) {
        @Override
        public void buildSequence(EngineSAJson.Builder b, MaterialLevel mat) {
            String base = EngineSAJson.Builder.incompleteMaterialItemId(mat, "injector_grid");
            b.deploy(base, mat.itemId("injector"));
            b.deploy(base, mat.itemId("engine_wall"));
            b.deploy(base, mat.itemId("injector"));
        }
    },

    TURBINE(
            "engine_recipe/power_pack", "turbo_pump/turbine", true, 2 // TODO: rename turbo_pump folder if needed
    ) {
        @Override
        public void buildSequence(EngineSAJson.Builder b, MaterialLevel mat) {
            String base = EngineSAJson.Builder.incompleteMaterialItemId(mat, "turbine");
            b.deploy(base, mat.itemId("turbine_shaft"));
            b.deploy(base, mat.itemId("blisk"));
        }
    },

    FUEL_RICH_STAGED_CYCLE(
            "engine_recipe/power_pack", "fuel_rich_staged_cycle", true, 1
    ) {
        @Override
        public void buildSequence(EngineSAJson.Builder b, MaterialLevel mat) {
            String base = EngineSAJson.Builder.incompleteItemId("power_pack");
            addPowerPackSteps(b, mat, base);
        }

        @Override
        public void appendExtraEngineData(EngineSAJson.Builder b) {
            b.putInEngineRecipeData("powerPackType", resource("fuel_rich_staged_cycle").toString());
        }

        @Override
        public String resultItemId(MaterialLevel mat) {
            return "creatingspace:power_pack";
        }

        @Override
        public String transitionalItemId(MaterialLevel mat) {
            return EngineSAJson.Builder.incompleteItemId("power_pack");
        }
    },

    FULL_FLOW_STAGED_CYCLE(
            "engine_recipe/power_pack", "full_flow_staged_cycle", true, 1
    ) {
        @Override
        public void buildSequence(EngineSAJson.Builder b, MaterialLevel mat) {
            String base = EngineSAJson.Builder.incompleteItemId("power_pack");
            addPowerPackSteps(b, mat, base);
        }

        @Override
        public void appendExtraEngineData(EngineSAJson.Builder b) {
            b.putInEngineRecipeData("powerPackType", resource("full_flow_staged_cycle").toString());
        }

        @Override
        public String resultItemId(MaterialLevel mat) {
            return "creatingspace:power_pack";
        }

        @Override
        public String transitionalItemId(MaterialLevel mat) {
            return EngineSAJson.Builder.incompleteItemId("power_pack");
        }
    },

    OPEN_CYCLE(
            "engine_recipe/power_pack", "open_cycle", true, 1
    ) {
        @Override
        public void buildSequence(EngineSAJson.Builder b, MaterialLevel mat) {
            String base = EngineSAJson.Builder.incompleteItemId("power_pack");
            b.deployMach(base, mat.itemId("turbine"));
            b.deployMach(base, mat.itemId("injector_grid"));
        }

        @Override
        public void appendExtraEngineData(EngineSAJson.Builder b) {
            b.putInEngineRecipeData("powerPackType", resource("open_cycle").toString());
        }

        @Override
        public String resultItemId(MaterialLevel mat) {
            return "creatingspace:power_pack";
        }

        @Override
        public String transitionalItemId(MaterialLevel mat) {
            return EngineSAJson.Builder.incompleteItemId("power_pack");
        }
    },

    OX_RICH_STAGED_CYCLE(
            "engine_recipe/power_pack", "ox_rich_staged_cycle", true, 1
    ) {
        @Override
        public void buildSequence(EngineSAJson.Builder b, MaterialLevel mat) {
            String base = EngineSAJson.Builder.incompleteItemId("power_pack");
            addPowerPackSteps(b, mat, base);
        }

        @Override
        public void appendExtraEngineData(EngineSAJson.Builder b) {
            b.putInEngineRecipeData("powerPackType", resource("ox_rich_staged_cycle").toString());
        }

        @Override
        public String resultItemId(MaterialLevel mat) {
            return "creatingspace:power_pack";
        }

        @Override
        public String transitionalItemId(MaterialLevel mat) {
            return EngineSAJson.Builder.incompleteItemId("power_pack");
        }
    },

    // ---------- Non-material recipes ----------
    DUPLICATE_BLUEPRINT(
            "engine_recipe", "duplicate_blueprint", false, 1
    ) {
        @Override
        public void buildSequence(EngineSAJson.Builder b, MaterialLevel mat) {
            b.deploy("creatingspace:engine_blueprint", "minecraft:paper");
            b.deploy("creatingspace:engine_blueprint", "minecraft:paper");
        }
    },

    ENGINE(
            "engine_recipe", "engine", false, 1
    ) {
        @Override
        public void buildSequence(EngineSAJson.Builder b, MaterialLevel mat) {
            b.deployMach("creatingspace:engine_blueprint", "creatingspace:power_pack");
            b.deployMach("creatingspace:engine_blueprint", "creatingspace:exhaust_pack");
        }
    };

    // ------------------------------------------------------------------------

    private final String baseFolder;
    private final String recipeSubPath;
    private final boolean materialDependent;
    private final int loops;
    private final String recipeLeafName;

    EnginePartType(String baseFolder, String recipeSubPath, boolean materialDependent, int loops) {
        this.baseFolder = baseFolder;
        this.recipeSubPath = recipeSubPath;
        this.materialDependent = materialDependent;
        this.loops = loops;
        int slashIndex = recipeSubPath.lastIndexOf('/');
        this.recipeLeafName = slashIndex == -1 ? recipeSubPath : recipeSubPath.substring(slashIndex + 1);
    }

    public String baseFolder() { return baseFolder; }
    public String recipeSubPath() { return recipeSubPath; }
    public boolean isMaterialDependent() { return materialDependent; }
    public int loops() { return loops; }
    public String recipeLeafName() { return recipeLeafName; }

    /** Returns the default result item ID for this part at the given material level. */
    public String resultItemId(MaterialLevel mat) {
        if (!materialDependent) {
            return "creatingspace:" + recipeLeafName;
        }

        if (mat == null) {
            throw new IllegalArgumentException("Material-dependent recipe requires material level for " + name());
        }

        return switch (this) {
            case COMBUSTION_CHAMBER -> "creatingspace:combustion_chamber";
            case BELL_NOZZLE -> "creatingspace:bell_nozzle";
            case AEROSPIKE_PLUG -> "creatingspace:aerospike_plug";
            default -> mat.itemId(recipeLeafName);
        };
    }

    /** Returns the transitional item ID for this part at the given material level. */
    public String transitionalItemId(MaterialLevel mat) {
        if (!materialDependent) {
            return "creatingspace:engine_blueprint";
        }

        if (mat == null) {
            throw new IllegalArgumentException("Material-dependent recipe requires material level for " + name());
        }

        return switch (recipeLeafName) {
            case "injector_grid", "turbine" -> EngineSAJson.Builder.incompleteMaterialItemId(mat, recipeLeafName);
            default -> EngineSAJson.Builder.incompleteItemId(recipeLeafName);
        };
    }

    /** Stream of material levels that this part should generate recipes for. */
    public Stream<MaterialLevel> applicableMaterials() {
        return materialDependent ? Arrays.stream(MaterialLevel.values()) : Stream.empty();
    }

    /** Override this to append additional data fields into engineRecipeData. */
    public void appendExtraEngineData(EngineSAJson.Builder b) {}

    /** Override this to define sequenced assembly steps. */
    public abstract void buildSequence(EngineSAJson.Builder b, MaterialLevel mat);

    /** Shared power pack pattern for multi-cycle recipes. */
    protected static void addPowerPackSteps(EngineSAJson.Builder b, MaterialLevel mat, String base) {
        b.deployMach(base, mat.itemId("turbine"));
        b.deployMach(base, mat.itemId("turbine"));
        b.deployMach(base, mat.itemId("injector_grid"));
    }

    /** Utility to add the same deploying step multiple times. */
    protected static void repeatDeploy(EngineSAJson.Builder b, String base, String addition, int times) {
        for (int i = 0; i < times; i++) {
            b.deploy(base, addition);
        }
    }
}
