package com.rae.creatingspace.configs;

import com.rae.creatingspace.CreatingSpace;

public class CSCfgClient extends CSConfigBase{

    public final CSOxygenBacktank oxygenBacktankN = nested(1, CSOxygenBacktank::new, Comments.oxygenBacktank);
    public final ConfigGroup oxygenRoom = group(1, "oxygenRoom", Comments.oxygenBacktank);
    public final ConfigBool oxygenRoomDebugMode = new ConfigBool("oxygenRoomDebugMode", false, Comments.oxygenRoomDebugMode);
    @Override
    public String getName() {
        return CreatingSpace.MODID + ".client";
    }

    private static class Comments {
        static String oxygenBacktank = "config for the oxygen backtank";
        static String oxygenRoomDebugMode = "turn this one to see the shape of the room";
    }
}
