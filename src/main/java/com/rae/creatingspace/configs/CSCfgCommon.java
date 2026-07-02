package com.rae.creatingspace.configs;

import com.rae.creatingspace.CreatingSpace;

public class CSCfgCommon extends CSConfigBase {
    public final ConfigBool                          additionalLogInfo    = new ConfigBool("additionalLogInfo", false, Comments.additionalLogInfo);
    public final ConfigEnum<CSCfgServer.Measurement> recorder_measurement = e(CSCfgServer.Measurement.VOLUMETRIC, "recorder_measurement", Comments.recorder_measurement);

    @Override
    public String getName() {
        return CreatingSpace.MODID + ".common";
    }

    private static class Comments {
        static String additionalLogInfo = "making the log register additional information WARNING console spam";
        static String recorder_measurement = "the type of measurement the flight recorder give for propellant quantities";
    }
}
