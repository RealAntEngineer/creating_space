package com.rae.creatingspace.configs;

import com.rae.creatingspace.CreatingSpace;

public class CSCfgClient extends CSConfigBase{

    public final CSOxygenBacktank oxygenBacktank = nested(1, CSOxygenBacktank::new, Comments.oxygenBacktank);
    public final ConfigGroup oxygenRoom = group(1, "oxygenRoom", Comments.oxygenRoom);
    public final ConfigBool oxygenRoomDebugMode = new ConfigBool("oxygenRoomDebugMode", false, Comments.oxygenRoomDebugMode);


    public final ConfigGroup rocket = group(1, "rocket", Comments.rocket);
    public final ConfigFloat zoomOut = new ConfigFloat("zoomOut", 0.5f, 0.1f, 20, Comments.zoomOut);
    public final ConfigEnum<Measurement> recorder_measurement = e(Measurement.VOLUMETRIC, "recorder_measurement", Comments.recorder_measurement);

    public final ConfigGroup rendering = group(1, "rendering", Comments.rendering);
    public final ConfigBool render_fog = new ConfigBool("render_fog",true, Comments.render_fog);
    @Override
    public String getName() {
        return CreatingSpace.MODID + ".client.V"+2;
    }

    private static class Comments {
        static String oxygenBacktank = "config for the oxygen backtank";
        static String oxygenRoom = "config for the oxygen room";
        static String oxygenRoomDebugMode = "turn this on to see the shape of the room";
        static String rocket = "client configs for the rockets";
        static String zoomOut = "multiplier for the rocket zoom out ( when on a seat)";
        static String recorder_measurement = "the type of measurement the flight recorder give for propellant quantities";
        static String rendering = "rendering";
        static String render_fog = "does the planets render fog or not";
    }

    public enum Measurement {
        VOLUMETRIC,
        MASS
    }
}
