package com.rae.creatingspace.content.ponders;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.init.PonderInit;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.crank.ValveHandleBlock;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlock;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlock;
import com.simibubi.create.foundation.ponder.PonderWorldBlockEntityFix;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class CSPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return CreatingSpace.MODID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderInit.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        AllCreatePonderTags.register(helper);
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
        helper.registerSharedText("rpm8", "8 RPM");
        helper.registerSharedText("rpm16", "16 RPM");
        helper.registerSharedText("rpm16_source", "Source: 16 RPM");
        helper.registerSharedText("rpm32", "32 RPM");

        helper.registerSharedText("movement_anchors", "With the help of Super Glue, larger structures can be moved.");
        helper.registerSharedText("behaviour_modify_value_panel", "This behaviour can be modified using the value panel");
        helper.registerSharedText("storage_on_contraption", "Inventories attached to the Contraption will pick up their drops automatically");
    }

    @Override
    public void onPonderLevelRestore(PonderLevel ponderLevel) {
        PonderWorldBlockEntityFix.fixControllerBlockEntities(ponderLevel);
    }

    @Override
    public void indexExclusions(IndexExclusionHelper helper) {
        helper.excludeBlockVariants(ValveHandleBlock.class, AllBlocks.COPPER_VALVE_HANDLE.get());
        helper.excludeBlockVariants(PostboxBlock.class, AllBlocks.PACKAGE_POSTBOXES.get(DyeColor.WHITE).get());
        helper.excludeBlockVariants(TableClothBlock.class, AllBlocks.TABLE_CLOTHS.get(DyeColor.WHITE).get());
    }
}