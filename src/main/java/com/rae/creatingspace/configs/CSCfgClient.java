package com.rae.creatingspace.configs;

import com.rae.creatingspace.CreatingSpace;

public class CSCfgClient extends CSConfigBase{

    public final CSOxygenBacktank oxygenBacktank = nested(1, CSOxygenBacktank::new, Comments.oxygenBacktank);
    public final ConfigGroup oxygenRoom = group(1, "oxygenRoom", Comments.oxygenBacktank);
    public final ConfigBool oxygenRoomDebugMode = new ConfigBool("oxygenRoomDebugMode", false, Comments.oxygenRoomDebugMode);

    public final ConfigGroup rocket = group(1, "rocket", Comments.rocket);
    public final ConfigFloat zoomOut = new ConfigFloat("zoomOut", 3f, 0.1f, 20, Comments.zoomOut);
    @Override
    public String getName() {
        return CreatingSpace.MODID + ".client";
    }

    private static class Comments {
        static String oxygenBacktank = "config for the oxygen backtank";
        static String oxygenRoomDebugMode = "turn this one to see the shape of the room";
        static String rocket = "client configs for the rockets";
        static String zoomOut = "multiplier for the rocket zoom out ( when on a seat)";
    }
}
