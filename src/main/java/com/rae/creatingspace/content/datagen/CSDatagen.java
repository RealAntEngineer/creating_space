package com.rae.creatingspace.content.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rae.creatingspace.CreatingSpace;

import com.rae.creatingspace.content.datagen.recipe.*;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CSDatagen {
    public static void gatherDataHighPriority(GatherDataEvent event) {
        if (event.getMods().contains(CreatingSpace.MODID))
            addExtraRegistrateData();
    }

	public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(CreatingSpace.MODID))
            return;

		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		if (event.includeClient()) {
			//generator.addProvider(true, AllSoundEvents.provider(generator));
		}
		if (event.includeServer()) {

			//CS Recipes
			generator.addProvider(true, new CSStandardRecipeGen(output, lookupProvider));
			generator.addProvider(true, new CSPressingRecipeGen(output, lookupProvider, CreatingSpace.MODID));
			generator.addProvider(true, new CSCrushingRecipeGen(output, lookupProvider, CreatingSpace.MODID));
			generator.addProvider(true, new CSMixingRecipeGen(output, lookupProvider, CreatingSpace.MODID));
			generator.addProvider(true, new CSWashingRecipeGen(output, lookupProvider, CreatingSpace.MODID));
			generator.addProvider(true, new CSAirLiquefyingRecipeGen(output, lookupProvider));
			generator.addProvider(true, new CSChemicalSynthesisRecipeGen(output, lookupProvider));
			generator.addProvider(true, new CSMechanicalElectrolysisRecipeGen(output, lookupProvider));
			generator.addProvider(true, new CSSequencedAssemblyRecipeGen(output, lookupProvider));
			generator.addProvider(true, new CSLootTableProvider(output, lookupProvider));
            //generator.addProvider(true, new EngineSequencedAssemblyProvider(output));
			// it doesn't quite work. the output is wrong right now and it's missing some of it


            // event.getGenerator().addProvider(true, new RegistrateDataProvider(REGISTRATE, CreatingSpace.MODID, event));
		}
	}

	private static void addExtraRegistrateData() {
		//CreateRegistrateTags.addGenerators();


		CreatingSpace.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
			BiConsumer<String, String> langConsumer = provider::add;

			provideDefaultLang("interface", langConsumer);
			provideDefaultLang("tooltips", langConsumer);
			//AllAdvancements.provideLang(langConsumer);
			//AllSoundEvents.provideLang(langConsumer);
			providePonderLang(langConsumer);
		});

	}

	private static void provideDefaultLang(String fileName, BiConsumer<String, String> consumer) {
		String path = "assets/creatingspace/lang/default/" + fileName + ".json";
		JsonElement jsonElement = FilesHelper.loadJsonResource(path);
		if (jsonElement == null) {
			throw new IllegalStateException(String.format("Could not find default lang file: %s", path));
		}
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().getAsString();
			consumer.accept(key, value);
		}
	}

	private static void providePonderLang(BiConsumer<String, String> consumer) {
		// Register these since FMLClientSetupEvent does not run during datagen
		//AllPonderTags.register();
		//PonderIndex.register();

		//SharedText.gatherText();
		//PonderLocalization.generateSceneLang();

		//GeneralText.provideLang(consumer);
		//PonderLocalization.provideLang(CreatingSpace.MODID, consumer);
	}
}
