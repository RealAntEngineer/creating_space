package com.rae.creatingspace.init;

import com.rae.creatingspace.CreatingSpace;
import com.rae.creatingspace.client.ponders.FluidScene;
import com.rae.creatingspace.client.ponders.RocketScene;
import com.rae.creatingspace.init.ingameobject.BlockInit;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;

public class PonderInit {

    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(CreatingSpace.MODID);

    public static void register(){
        // Register storyboards here
        // (!) Added entries require re-launch
        // (!) Modifications inside storyboard methods only require re-opening the ui
        //TODO add ponder for :
        //  electrolyzer
        //  rocket generator
        //  clamps
        //  flowmeter
        //

        HELPER.forComponents(BlockInit.CHEMICAL_SYNTHESIZER)
                .addStoryBoard("chemical_synthesizer/chemical_synthesizer", FluidScene::chemicalSynthesizer);//AllPonderTags.FLUIDS);

        HELPER.forComponents(BlockInit.SMALL_ROCKET_ENGINE,BlockInit.BIG_ROCKET_ENGINE,BlockInit.ROCKET_CONTROLS,BlockInit.FLIGHT_RECORDER)
                .addStoryBoard("rocket/rocket_building", RocketScene::rocketBuild)
                .addStoryBoard("rocket/rocket_building",RocketScene::rocketDebug);
    }
}
