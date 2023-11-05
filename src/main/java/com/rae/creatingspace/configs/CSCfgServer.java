package com.rae.creatingspace.configs;

public class CSCfgServer  extends CSConfigBase {

    public final CSKinetics kinetics = nested(0, CSKinetics::new, Comments.kinetics);

    public final CSRocketEngine rocketEngine = nested(0,CSRocketEngine::new,Comments.rocketEngine);
    @Override
    public String getName() {
        return "server";
    }

    private static class Comments {
        static String rocketEngine ="";
        static String kinetics = "Parameters and abilities of Creatingspace's kinetic mechanisms";
    }

}
