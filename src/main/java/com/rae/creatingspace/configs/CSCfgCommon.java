package com.rae.creatingspace.configs;

import com.simibubi.create.infrastructure.config.CClient;

public class CSCfgCommon extends CSConfigBase{

    public final ConfigGroup rocketSpawnGroup = group(0, "rocketSpawn",
            Comments.spawn);
    public final ConfigInt planetSpawnHeight = i(200,0,400,"planetSpawnHeight",Comments.planet);
    public final ConfigInt spaceSpawnHeight = i(64,0,400,"spaceSpawnHeight",Comments.space);

    public final ConfigGroup dimAccessGroup = group(1, "dimensionAccess",
            Comments.dimensionAccess);
    public final CSDimAccess dimAccess = nested(0, CSDimAccess::new,Comments.dimensionAccess);

    @Override
    public String getName() {
        return "common";
    }

    private static class Comments {
        public static String spawn = "Configure the spawn";
        static String planet = "Configure the spawn height on a planet";
        static String space = "Configure the spawn height in orbit";
        static String dimensionAccess = "Configure which dimension are a planet";
    }
}
