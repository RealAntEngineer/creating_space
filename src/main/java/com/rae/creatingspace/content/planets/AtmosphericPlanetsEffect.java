package com.rae.creatingspace.content.planets;

import com.rae.creatingspace.configs.CSConfigs;

public class AtmosphericPlanetsEffect extends SmartPlanetsEffect{

    public AtmosphericPlanetsEffect(){
    }
    @Override
    public boolean isFoggyAt(int p_108874_, int p_108875_) {
        return CSConfigs.CLIENT.render_fog.get();
    }

}
