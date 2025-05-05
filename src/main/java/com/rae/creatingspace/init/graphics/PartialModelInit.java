package com.rae.creatingspace.init.graphics;


import com.rae.creatingspace.CreatingSpace;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public class PartialModelInit {
    public static final PartialModel MEMORY_ROLL = block("flight_recorder/memory_roll"),
            CATALYST_CARRIER_HEAD = block("catalyst_carrier/head"),
            ELECTROLYZER_HEAD = block("mechanical_electrolyzer/head");

    private static PartialModel block(String path) {
        return PartialModel.of(CreatingSpace.resource("block/" + path));
    }

    public static void init() {
        // init static fields
    }
}
