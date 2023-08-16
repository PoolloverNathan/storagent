package poollovernathan.fabric.storagent;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

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

    ShelfSupportMaterial(String name, Block supportsBlock, Identifier sides, Identifier bottom) {
        this.name = name;
        this.supportsBlock = supportsBlock;
        this.sides = sides;
        this.bottom = bottom;
    }
    ShelfSupportMaterial(String name, Block supportsBlock, Identifier sides) {
        this(name, supportsBlock, sides, new Identifier(sides + "_top"));
    }
}
