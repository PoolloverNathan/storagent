package poollovernathan.fabric.storagent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.client.*;
import net.minecraft.data.server.BlockTagProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.util.registry.Registry;

import java.util.function.Consumer;

import static poollovernathan.fabric.storagent.ExampleMod.SHELF_BLOCKS;
import static poollovernathan.fabric.storagent.ExampleMod.id;

public class ExampleModDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		fabricDataGenerator.addProvider(ModelGenerator::new);
		fabricDataGenerator.addProvider(RecipeGenerator::new);
		fabricDataGenerator.addProvider(LootGenerator::new);
		fabricDataGenerator.addProvider(LangGenerator::new);
		fabricDataGenerator.addProvider(ShelfBlock::provideBlockTags);
		fabricDataGenerator.addProvider(ShelfBlock::provideItemTags);
	}
}

class ModelGenerator extends FabricModelProvider {

	public ModelGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
		for (var block: ExampleMod.SHELF_BLOCKS) {
			var id = Registry.BLOCK.getId(block);
			id = id(id.getNamespace(), "block/" + id.getPath());
			var finalId = id;
			blockStateModelGenerator.blockStateCollector.accept(new BlockStateSupplier() {
				private boolean ran = false;

				@Override
				public JsonElement get() {
					if (ran) {
						return null;
					} else {
						ran = true;
						return new JsonBuilder()
								.add("variants", builder -> builder
										.add("", builder2 -> builder2
												.add("model", finalId.toString())
										)
								)
								.build();
					}
				}

				@Override
				public Block getBlock() {
					return block;
				}
			});
			blockStateModelGenerator.modelCollector.accept(id, () -> {
				final var model = new JsonObject();
				model.addProperty("parent", id("block/base_shelf_" + block.height.name().toLowerCase()).toString());
				final var textures = new JsonObject();
				textures.addProperty("supports_top", block.supports.sides.toString() + "_top");
				textures.addProperty("supports_side", block.supports.sides.toString());
				textures.addProperty("surface", block.surface.surface.toString());
				model.add("textures", textures);
				return model;
			});
		}
	}

	@Override
	public void generateItemModels(ItemModelGenerator itemModelGenerator) {

	}
}

class RecipeGenerator extends FabricRecipeProvider {

	public RecipeGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {
		for (var block: SHELF_BLOCKS) {
			block.createSwitchRecipe().offerTo(exporter, block.createId() + "_conversion");
			block.createCraftingRecipe().map(recipe -> {
				recipe.offerTo(exporter, block.createId());
				return null;
			});
		}
	}
}

class LootGenerator extends FabricBlockLootTableProvider {

	protected LootGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateBlockLootTables() {
		for (var block: SHELF_BLOCKS) {
			addDrop(block, block.asItem());
		}
	}
}

class LangGenerator extends FabricLanguageProvider {

	protected LangGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	protected LangGenerator(FabricDataGenerator dataGenerator, String languageCode) {
		super(dataGenerator, languageCode);
	}

	@Override
	public void generateTranslations(TranslationBuilder translationBuilder) {
		for (var block: SHELF_BLOCKS) {
			translationBuilder.add(block, block.getTrueName());
		}
	}
}