package com.rae.creatingspace.configs;

public class CSCfgCommon extends CSConfigBase{

    public final CSDimAccess dimAccess = nested(0, CSDimAccess::new,Comments.dimensionAccess);
    public final ConfigInt planetSpawnHeight = i(200,0,400,"planetSpawnHeigth",Comments.planet);
    public final ConfigInt spaceSpawnHeight = i(64,0,400,"spaceSpawnHeigth",Comments.space);

    @Override
    public String getName() {
        return "client";
    }

    private static class Comments {
        static String planet = "Configure the spawn height on a planet";
        static String space = "Configure the spawn height in orbit";
        static String dimensionAccess = "Configure which dimension are a planet";
    }
}
