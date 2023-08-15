package poollovernathan.fabric.storagent;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "storagent";
    public static final Logger LOGGER = LoggerFactory.getLogger("Your Storage Problem");
	public static final ShelfBlock[] SHELF_BLOCKS = new ShelfBlock[ShelfMaterial.values().length * ShelfMaterial.values().length];
	public static final BlockEntityType<ShelfEntity> SHELF_BLOCK_ENTITY = FabricBlockEntityTypeBuilder.create(ShelfEntity::new).build();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		int i = 0;
		for (var surface: ShelfMaterial.values()) {
			for (var support: ShelfMaterial.values()) {
				assert i < SHELF_BLOCKS.length;
				var settigns = reflexiveBlockSettingsDarkMagicDoNotUse(surface.slabBlock, "material", "mapColorProvider");
				var mcp = (Function<BlockState, MapColor>) settigns[1];
				var block = new ShelfBlock(FabricBlockSettings.of((Material) settigns[0], mcp.apply(surface.slabBlock.getDefaultState())).solidBlock((a, b, c) -> false).nonOpaque(), surface, support);
				SHELF_BLOCKS[i++] = block;
				Registry.register(Registry.BLOCK, id(block.createId()), block);
				Registry.register(Registry.ITEM, id(block.createId()), new BlockItem(block, new FabricItemSettings().group(Items.CHEST.getGroup())));
			}
		}
		Registry.register(Registry.BLOCK_ENTITY_TYPE, id("shelf"), SHELF_BLOCK_ENTITY);
	}

	public static Object[] reflexiveBlockSettingsDarkMagicDoNotUse(AbstractBlock block, String... keys) {
		AbstractBlock.Settings settings;
		try {
			var field = block.getClass().getField("settings");
			field.setAccessible(true);
			settings = (AbstractBlock.Settings)(field.get(block));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Reflexive settings access failed - this should never happen", e);
		}
		var got = new Object[keys.length];
		for (int i = 0; i < keys.length; i++) {
			try {
				var field = AbstractBlock.Settings.class.getField(keys[i]);
				field.setAccessible(true);
				got[i] = field.get(settings);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException("Failed to reflexively access settings field " + keys[i], e);
			}
		}
		return got;
	}

	public static Identifier id(String name) {
		return id(MOD_ID, name);
	}
	public static Identifier id(String namespace, String name) {
		return new Identifier(namespace, name);
	}
	public static Identifier vid(String name) {
		return id("minecraft", name);
	}
}