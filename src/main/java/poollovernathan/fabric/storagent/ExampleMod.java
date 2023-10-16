package poollovernathan.fabric.storagent;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockAccessor;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockSettingsAccessor;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "storagent";
    public static final Logger LOGGER = LoggerFactory.getLogger("Your Storage Problem");
	public static final ShelfBlock[] SHELF_BLOCKS = new ShelfBlock[ShelfSurfaceMaterial.values().length * ShelfSupportMaterial.values().length * ShelfHeight.values().length];
	public static final Lazy<BlockEntityType<ShelfEntity>> SHELF_BLOCK_ENTITY = new Lazy<>(() -> FabricBlockEntityTypeBuilder.create(ShelfEntity::new, SHELF_BLOCKS).build());
	public static final Item SHELVING_WAND_ITEM = new ShelvingWandItem(new FabricItemSettings().rarity(Rarity.RARE).group(ItemGroup.TOOLS).maxCount(1));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		int i = 0;
		for (var surface: ShelfSurfaceMaterial.values()) {
			for (var support : ShelfSupportMaterial.values()) {
				for (var height : ShelfHeight.values()) {
					assert i < SHELF_BLOCKS.length;
					//noinspection UnstableApiUsage
					var settigns = (AbstractBlockSettingsAccessor)(((AbstractBlockAccessor)surface.slab).getSettings());
					//noinspection UnstableApiUsage
					var mcp = (Function<BlockState, MapColor>) settigns.getMapColorProvider();
					//noinspection UnstableApiUsage
					var block = new ShelfBlock(FabricBlockSettings.of(settigns.getMaterial(), mcp.apply(surface.slab.getDefaultState())).solidBlock((a, b, c) -> false).nonOpaque(), surface, support, height);
					SHELF_BLOCKS[i++] = block;
					Registry.register(Registry.BLOCK, id(block.createId()), block);
					Registry.register(Registry.ITEM, id(block.createId()), new BlockItem(block, new FabricItemSettings().group(Items.CHEST.getGroup())));
				}
			}
		}
		LOGGER.info("Created %s dynamic models.".formatted(SHELF_BLOCKS.length));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, id("shelf"), SHELF_BLOCK_ENTITY.get());
		Registry.register(Registry.ITEM, id("shelving_wand"), SHELVING_WAND_ITEM);
	}

	public static <T> T pick(List<T> list, Random random) {
		return switch (list.size()) {
			case 0 -> null;
			case 1 -> list.get(0);
			default -> list.get(random.nextInt(list.size()));
		};
	}

	public static <T> T pick(T[] array, Random random) {
		return switch (array.length) {
			case 0 -> null;
			case 1 -> array[0];
			default -> array[random.nextInt(array.length)];
		};
	}

	@Contract(value = "!null -> !null; _ -> fail", pure = true)
	public static Identifier id(String name) {
		return id(MOD_ID, name);
	}
	@Contract(value = "!null, !null -> !null; _, _ -> fail", pure = true)
	public static Identifier id(String namespace, String name) {
		return new Identifier(namespace, name);
	}
	@Contract(value = "!null -> !null; _ -> fail", pure = true)
	public static Identifier vid(String name) {
		return id("minecraft", name);
	}

	public static boolean between(float value, float min, float max) {
		return between(value, min, max, false);
	}

	@Contract(value = "_ -> fail", pure = true)
	public static <T extends Throwable, R> R fail(@NotNull T error) throws T {
		throw error;
	}

	public static boolean between(float value, float min, float max, boolean exclusive) {
		if (exclusive) {
			return value > min && value < max;
		} else {
			return value >= min && value <= max;
		}
	}
	
	public static boolean between(double value, double min, double max) {
		return between(value, min, max, false);
	}

	public static boolean between(double value, double min, double max, boolean exclusive) {
		if (exclusive) {
			return value > min && value < max;
		} else {
			return value >= min && value <= max;
		}
	}
}