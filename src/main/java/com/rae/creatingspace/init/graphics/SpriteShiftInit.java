package com.rae.creatingspace.init.graphics;

import com.rae.creatingspace.CreatingSpace;
import com.simibubi.create.foundation.block.connected.AllCTTypes;
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import com.simibubi.create.foundation.block.connected.CTType;

public class SpriteShiftInit {

    public static final CTSpriteShiftEntry ROCKET_CASING = omni("rocket_casing");
    public static final CTSpriteShiftEntry ISOLATE_CASING = omni("isolate_casing");
    public static final CTSpriteShiftEntry ISOLATE_ENCASED_COGWHEEL_SIDE = vertical("isolate_encased_cogwheel_side");
    public static final CTSpriteShiftEntry ISOLATE_ENCASED_COGWHEEL_OTHERSIDE = horizontal("isolate_encased_cogwheel_side");

    private static CTSpriteShiftEntry omni(String name) {
        return getCT(AllCTTypes.OMNIDIRECTIONAL, name);
    }
    private static CTSpriteShiftEntry horizontal(String name) {
        return getCT(AllCTTypes.HORIZONTAL, name);
    }

    private static CTSpriteShiftEntry vertical(String name) {
        return getCT(AllCTTypes.VERTICAL, name);
    }
    //

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName, String connectedTextureName) {
        return CTSpriteShifter.getCT(type, CreatingSpace.resource("block/" + blockTextureName),
                CreatingSpace.resource("block/" + connectedTextureName + "_connected"));
    }

    private static CTSpriteShiftEntry getCT(CTType type, String blockTextureName) {
        return getCT(type, blockTextureName, blockTextureName);
    }
}
