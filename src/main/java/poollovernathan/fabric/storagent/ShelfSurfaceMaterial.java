package poollovernathan.fabric.storagent;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

import static poollovernathan.fabric.storagent.ExampleMod.vid;

public enum ShelfSurfaceMaterial {
    OAK("Oak", Blocks.OAK_SLAB, vid("block/oak_planks")),
    BIRCH("Birch", Blocks.BIRCH_SLAB, vid("block/birch_planks")),
    SPRUCE("Spruce", Blocks.SPRUCE_SLAB, vid("block/spruce_planks")),
    JUNGLE("Jungle", Blocks.JUNGLE_SLAB, vid("block/jungle_planks")),
    ACACIA("Acacia", Blocks.ACACIA_SLAB, vid("block/acacia_planks")),
    DARK_OAK("Dark Oak", Blocks.DARK_OAK_SLAB, vid("block/dark_oak_planks")),
    CRIMSON("Crimson", Blocks.CRIMSON_SLAB, vid("block/crimson_planks")),
    WARPED("Warped", Blocks.WARPED_SLAB, vid("block/warped_planks"));

    public final String name;
    public final Block slab;
    public final Identifier surface;

    private ShelfSurfaceMaterial(String name, Block slab, Identifier surface) {

        this.name = name;
        this.slab = slab;
        this.surface = surface;
    }
}
