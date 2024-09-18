package com.rae.creatingspace;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Map.Entry;
import java.util.function.BiConsumer;

public class CSDatagen {
	public static void gatherData(GatherDataEvent event) {
		addExtraRegistrateData();

		DataGenerator generator = event.getGenerator();
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
		PonderLocalization.provideLang(CreatingSpace.MODID, consumer);
	}
}
