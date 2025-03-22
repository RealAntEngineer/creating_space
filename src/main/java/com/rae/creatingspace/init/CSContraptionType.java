package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.content.rocket.contraption.RocketContraption;
import com.simibubi.create.content.contraptions.ContraptionType;


public class CSContraptionType  {
    public static final ContraptionType ROCKET = ContraptionType.register(CreatingSpace.resource("rocket").toString(), RocketContraption::new);

    public CSContraptionType() {
    }

    public static void prepare() {
    }
}
