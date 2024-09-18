package com.rae.creatingspace.configs;

import com.rae.creatingspace.CreatingSpace;

public class CSCfgCommon extends CSConfigBase {
    public final ConfigBool additionalLogInfo = new ConfigBool("additionalLogInfo", false, Comments.additionalLogInfo);


    @Override
    public String getName() {
        return CreatingSpace.MODID + ".common";
    }

    private static class Comments {
        static String additionalLogInfo = "making the log register additional information WARNING console spam";
    }
}