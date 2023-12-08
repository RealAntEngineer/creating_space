package com.rae.creatingspace.configs;

public class CSCfgCommon extends CSConfigBase{

    public final ConfigGroup rocketSpawnGroup = group(0, "rocketSpawn",
            Comments.spawn);
    public final ConfigInt planetSpawnHeight = i(200,0,400,"planetSpawnHeight",Comments.planet);
    public final ConfigInt spaceSpawnHeight = i(64,0,400,"spaceSpawnHeight",Comments.space);


    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        public static String spawn = "Configure the spawn";
        static String planet = "Configure the spawn height on a planet";
        static String space = "Configure the spawn height in orbit";
    }
}
