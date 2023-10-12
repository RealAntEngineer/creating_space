package com.rae.creatingspace.server.contraption;

import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.content.contraptions.ContraptionType;


public class CSContraptionType  {
    public static final ContraptionType ROCKET = ContraptionType.register(CreatingSpace.resource("rocket").toString(),RocketContraption::new);

    public CSContraptionType() {
    }

    public static void prepare() {
    }
}
