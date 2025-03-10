package com.rae.creatingspace.configs;

import com.rae.creatingspace.CreatingSpace;

public class CSCfgServer  extends CSConfigBase {

    public final CSKinetics kinetics = nested(0, CSKinetics::new, Comments.kinetics);

    public final CSRocketEngine rocketEngine = nested(0,CSRocketEngine::new,Comments.rocketEngine);
    public final ConfigGroup oxygenRoom = group(0, "oxygenRoom", Comments.oxygenRoom);
    public final ConfigInt leafOxygenProduction = new ConfigInt("leaf02Prod",2,1,Integer.MAX_VALUE, Comments.leafOxygenProduction);
    public final ConfigInt livingO2Consumption = new ConfigInt("living02Consumption",10,1,Integer.MAX_VALUE, Comments.livingO2Consumption);
    public final ConfigInt maxSizePerSealer = new ConfigInt("maxSizePerSealer",1000,1,Integer.MAX_VALUE, Comments.maxSizePerSealer);
    public final ConfigInt maxBlockPerTick = new ConfigInt("maxBlockPerTick",1000,1,Integer.MAX_VALUE, Comments.maxBlockPerTick);

    @Override
    public String getName() {
        return CreatingSpace.MODID + ".server.V" + 2;
    }

    private static class Comments {
        static String rocketEngine ="";
        static String kinetics = "Parameters and abilities of Creating Space's kinetic mechanisms";
        static String oxygenRoom = "config for the oxygen room";

        static String leafOxygenProduction = "leaf oxygen production";
        static String livingO2Consumption = "living entity consumption";
        static String maxSizePerSealer = "maximum number of blocks a room can have per sealer before it's declared unsealable, warning putting an high number will make the search slower";
        static String maxBlockPerTick = "maximum number of blocks a room can search per tick, warning putting an high number can cause lag";

    }

}
