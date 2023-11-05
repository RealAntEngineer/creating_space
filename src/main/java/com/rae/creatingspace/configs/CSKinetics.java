package com.rae.creatingspace.configs;

public class CSKinetics extends CSConfigBase{
    public CSStress stressValues  = nested(1, CSStress::new, Comments.stress);

    @Override
    public String getName() {
        return "kinetics";
    }

    private class Comments {
        static String stress = "Fine tune the kinetic stats of individual components";

    }
}
