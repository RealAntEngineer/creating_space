package com.rae.creatingspace.content.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rae.creatingspace.CreatingSpace;

import com.rae.creatingspace.content.datagen.recipe.CSPressingRecipeGen;
import com.rae.creatingspace.content.datagen.recipe.CSStandardRecipeGen;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static com.rae.creatingspace.CreatingSpace.REGISTRATE;

public class CSDatagen {
	public static void gatherData(GatherDataEvent event) {
		addExtraRegistrateData();

		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

		if (event.includeClient()) {
			//generator.addProvider(true, AllSoundEvents.provider(generator));
		}
		//TODO use this to load the info for dimensions (need to be one file for each dimension)
		if (event.includeServer()) {
			//generator.addProvider(true, new CreateRecipeSerializerTagsProvider(generator, existingFileHelper));

			//generator.addProvider(true, new AllAdvancements(generator));

			//generator.addProvider(true, new StandardRecipeGen(generator));
			//generator.addProvider(true, new MechanicalCraftingRecipeGen(generator));
			//generator.addProvider(true, new SequencedAssemblyRecipeGen(generator));
			//ProcessingRecipeGen.registerAll(generator);

//			AllOreFeatureConfigEntries.gatherData(event);

			//CS Recipes
			generator.addProvider(true, new CSStandardRecipeGen(output, lookupProvider));
			generator.addProvider(true, new CSPressingRecipeGen(output, lookupProvider));

			event.getGenerator().addProvider(true, REGISTRATE.setDataProvider(new RegistrateDataProvider(REGISTRATE, CreatingSpace.MODID, event)));
		}
	}

	private static void addExtraRegistrateData() {
		//CreateRegistrateTags.addGenerators();

		/*
		REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {
			BiConsumer<String, String> langConsumer = provider::add;

			provideDefaultLang("interface", langConsumer);
			provideDefaultLang("tooltips", langConsumer);
			//AllAdvancements.provideLang(langConsumer);
			//AllSoundEvents.provideLang(langConsumer);
			providePonderLang(langConsumer);
		});
		 */
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
