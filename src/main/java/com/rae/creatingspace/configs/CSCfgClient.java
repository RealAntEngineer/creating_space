package com.rae.creatingspace.configs;

import com.rae.creatingspace.CreatingSpace;

public class CSCfgClient extends CSConfigBase{
    public final CSOxygenBacktank oxygenBacktank = nested(0, CSOxygenBacktank::new, Comments.oxygenBacktank);

    @Override
    public String getName() {
        return CreatingSpace.MODID + ".client";
    }

    private static class Comments {
        static String oxygenBacktank = "config for the oxygen backtank";
    }
}
