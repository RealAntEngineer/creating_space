package com.rae.creatingspace.init;

import com.rae.creatingspace.content.ponders.RocketScene;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class PonderInit {


    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper){
        // Register storyboards here
        // (!) Added entries require re-launch
        // (!) Modifications inside storyboard methods only require re-opening the ui
        //TODO add ponder for :
        //  electrolyzer
        //  rocket generator
        //  clamps
        //  flowmeter
        PonderSceneRegistrationHelper<ItemProviderEntry<?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(BlockInit.SMALL_ROCKET_ENGINE,BlockInit.BIG_ROCKET_ENGINE,BlockInit.ROCKET_CONTROLS,BlockInit.FLIGHT_RECORDER)
                .addStoryBoard("rocket/rocket_building", RocketScene::rocketBuild)
                .addStoryBoard("rocket/rocket_building",RocketScene::rocketDebug);
    }
}
