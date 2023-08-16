package poollovernathan.fabric.storagent;

import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import static poollovernathan.fabric.storagent.ExampleMod.id;
import static poollovernathan.fabric.storagent.ExampleMod.vid;

public enum ShelfSupportMaterial {
    OAK("Oak", Blocks.OAK_LOG, vid("block/oak_log")),
    BIRCH("Birch", Blocks.BIRCH_LOG, vid("block/birch_log")),
    SPRUCE("Spruce", Blocks.SPRUCE_LOG, vid("block/spruce_log")),
    JUNGLE("Jungle", Blocks.JUNGLE_LOG, vid("block/jungle_log")),
    ACACIA("Acacia", Blocks.ACACIA_LOG, vid("block/acacia_log")),
    DARK_OAK("Dark Oak", Blocks.DARK_OAK_LOG, vid("block/dark_oak_log")),
    CRIMSON("Crimson", Blocks.CRIMSON_STEM, vid("block/crimson_stem")),
    WARPED("Warped", Blocks.WARPED_STEM, vid("block/warped_stem"));

    public final String name;
    public final Block supportsBlock;
    public final Identifier sides;
    public final Identifier bottom;
    public final TagKey<Item> itemTagKey;
    public final TagKey<Block> blockTagKey;
    public final TagKey<Item> containItemTagKey;
    public final TagKey<Block> containBlockTagKey;

    ShelfSupportMaterial(String name, Block supportsBlock, Identifier sides, Identifier bottom) {
        this.name = name;
        this.supportsBlock = supportsBlock;
        this.sides = sides;
        this.bottom = bottom;
        this.itemTagKey = TagKey.of(Registry.ITEM_KEY, id("%s_supports".formatted(name().toLowerCase())));
        this.blockTagKey = TagKey.of(Registry.BLOCK_KEY, id("%s_supports".formatted(name().toLowerCase())));
        this.containItemTagKey = TagKey.of(Registry.ITEM_KEY, id("%s_shelves".formatted(name().toLowerCase())));
        this.containBlockTagKey = TagKey.of(Registry.BLOCK_KEY, id("%s_shelves".formatted(name().toLowerCase())));
    }
    ShelfSupportMaterial(String name, Block supportsBlock, Identifier sides) {
        this(name, supportsBlock, sides, new Identifier(sides + "_top"));
    }
}
