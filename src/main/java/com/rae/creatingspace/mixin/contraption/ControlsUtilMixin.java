package com.rae.creatingspace.mixin.contraption;

import com.rae.creatingspace.init.KeysInit;
import com.simibubi.create.foundation.utility.ControlsUtil;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Vector;

@Mixin(ControlsUtil.class)
public class ControlsUtilMixin {

    @Shadow(remap = false) private static Vector<KeyMapping> standardControls;

    @Inject(method = "getControls", at = @At("RETURN"),remap = false)
    private static void addCustomKeyBinds(CallbackInfoReturnable<Vector<KeyMapping>> cir){
            assert standardControls != null;
            standardControls.add(KeysInit.PITCH_UP.getKeybind());//6
            standardControls.add(KeysInit.PITCH_DOWN.getKeybind());//7
            standardControls.add(KeysInit.YAW_LEFT.getKeybind());//8
            standardControls.add(KeysInit.YAW_RIGHT.getKeybind());//9
            standardControls.add(KeysInit.SWITCH_MODE.getKeybind());//10


    }
}
