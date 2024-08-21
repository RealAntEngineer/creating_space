package com.rae.creatingspace.configs;

import com.rae.creatingspace.CreatingSpace;

public class CSCfgClient extends CSConfigBase{

    public final CSOxygenBacktank oxygenBacktank = nested(1, CSOxygenBacktank::new, Comments.oxygenBacktank);
    public final ConfigGroup oxygenRoom = group(1, "oxygenRoom", Comments.oxygenBacktank);
    public final ConfigBool oxygenRoomDebugMode = new ConfigBool("oxygenRoomDebugMode", false, Comments.oxygenRoomDebugMode);
    public final ConfigInt leafOxygenProduction = new ConfigInt("leaf02Prod",2,1,Integer.MAX_VALUE, Comments.oxygenBacktank);
    public final ConfigInt livingO2Consumption = new ConfigInt("living02Consumption",10,1,Integer.MAX_VALUE, Comments.oxygenBacktank);


    public final ConfigGroup rocket = group(1, "rocket", Comments.rocket);
    public final ConfigFloat zoomOut = new ConfigFloat("zoomOut", 3f, 0.1f, 20, Comments.zoomOut);
    public final ConfigEnum<Measurement> recorder_measurement = e(Measurement.VOLUMETRIC, "recorder_measurement", Comments.recorder_measurement);
    @Override
    public String getName() {
        return CreatingSpace.MODID + ".client";
    }

    private static class Comments {
        static String oxygenBacktank = "config for the oxygen backtank";
        static String oxygenRoom = "config for the oxygen room";

        static String leafOxygenProduction = "leaf oxygen production";
        static String livingO2Consumption = "living entity consumption";

        static String oxygenRoomDebugMode = "turn this on to see the shape of the room";
        static String rocket = "client configs for the rockets";
        static String zoomOut = "multiplier for the rocket zoom out ( when on a seat)";
        static String recorder_measurement = "the type of measurement the flight recorder give for propellant quantities";
    }

    public enum Measurement {
        VOLUMETRIC,
        MASS
    }
}
