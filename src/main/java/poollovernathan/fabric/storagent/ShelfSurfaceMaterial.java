package poollovernathan.fabric.storagent;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static poollovernathan.fabric.storagent.ExampleMod.id;
import static poollovernathan.fabric.storagent.ExampleMod.vid;

public enum ShelfSurfaceMaterial {
    OAK("Oak", Blocks.OAK_SLAB, vid("block/oak_planks")),
    BIRCH("Birch", Blocks.BIRCH_SLAB, vid("block/birch_planks")),
    SPRUCE("Spruce", Blocks.SPRUCE_SLAB, vid("block/spruce_planks")),
    JUNGLE("Jungle", Blocks.JUNGLE_SLAB, vid("block/jungle_planks")),
    ACACIA("Acacia", Blocks.ACACIA_SLAB, vid("block/acacia_planks")),
    DARK_OAK("Dark Oak", Blocks.DARK_OAK_SLAB, vid("block/dark_oak_planks")),
    CRIMSON("Crimson", Blocks.CRIMSON_SLAB, vid("block/crimson_planks")),
    WARPED("Warped", Blocks.WARPED_SLAB, vid("block/warped_planks")),
    DEEPSLATE("Deepslate", Blocks.POLISHED_DEEPSLATE_SLAB, vid("block/polished_deepslate")),
    SMOOTH_STONE("Smooth Stone", Blocks.SMOOTH_STONE_SLAB, vid("block/smooth_stone")),
    POLISHED_ANDESITE("Polished Andesite", Blocks.POLISHED_ANDESITE_SLAB, vid("block/polished_andesite")),
    POLISHED_DIORITE("Polished Diorite", Blocks.POLISHED_DIORITE_SLAB, vid("block/polished_diorite")),
    POLISHED_GRANITE("Polished Granite", Blocks.POLISHED_GRANITE_SLAB, vid("block/polished_granite")),
    BLACKSTONE("Blackstone", Blocks.BLACKSTONE_SLAB, vid("block/blackstone")),
    ;

    public final String name;
    public final Block slab;
    public final Identifier surface;
    public final TagKey<Item> itemTagKey;
    public final TagKey<Block> blockTagKey;
    public final TagKey<Item> containItemTagKey;
    public final TagKey<Block> containBlockTagKey;

    private ShelfSurfaceMaterial(String name, Block slab, Identifier surface) {
        this.name = name;
        this.slab = slab;
        this.surface = surface;
        this.itemTagKey = TagKey.of(Registry.ITEM_KEY, id("%s_supports".formatted(name().toLowerCase())));
        this.blockTagKey = TagKey.of(Registry.BLOCK_KEY, id("%s_supports".formatted(name().toLowerCase())));
        this.containItemTagKey = TagKey.of(Registry.ITEM_KEY, id("%s_shelves".formatted(name().toLowerCase())));
        this.containBlockTagKey = TagKey.of(Registry.BLOCK_KEY, id("%s_shelves".formatted(name().toLowerCase())));
    }
}
