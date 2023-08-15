package poollovernathan.fabric.storagent;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import static poollovernathan.fabric.storagent.ExampleMod.id;
import static poollovernathan.fabric.storagent.ExampleMod.vid;

public enum ShelfMaterial {
    OAK("Oak", Blocks.OAK_SLAB, Blocks.OAK_LOG, vid("block/oak_planks"), vid("block/oak_log")),
    BIRCH("Birch", Blocks.BIRCH_SLAB, Blocks.BIRCH_LOG, vid("block/birch_planks"), vid("block/birch_log")),
    SPRUCE("Spruce", Blocks.SPRUCE_SLAB, Blocks.SPRUCE_LOG, vid("block/spruce_planks"), vid("block/spruce_log")),
    JUNGLE("Jungle", Blocks.JUNGLE_SLAB, Blocks.JUNGLE_LOG, vid("block/jungle_planks"), vid("block/jungle_log")),
    ACACIA("Acacia", Blocks.ACACIA_SLAB, Blocks.ACACIA_LOG, vid("block/acacia_planks"), vid("block/acacia_log")),
    DARK_OAK("Dark Oak", Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_LOG, vid("block/dark_oak_planks"), vid("block/dark_oak_log")),
    CRIMSON("Crimson", Blocks.CRIMSON_SLAB, Blocks.CRIMSON_STEM, vid("block/crimson_planks"), vid("block/crimson_stem")),
    WARPED("Warped", Blocks.WARPED_SLAB, Blocks.WARPED_STEM, vid("block/warped_planks"), vid("block/warped_stem"));

    public final String name;
    public final Block slabBlock;
    public final Block logBlock;
    public final Identifier surfaceTexture;
    public final Identifier supportTexture;

    private ShelfMaterial(String name, Block slabBlock, Block logBlock, Identifier surfaceTexture, Identifier supportTexture) {

        this.name = name;
        this.slabBlock = slabBlock;
        this.logBlock = logBlock;
        this.surfaceTexture = surfaceTexture;
        this.supportTexture = supportTexture;
    }
}
