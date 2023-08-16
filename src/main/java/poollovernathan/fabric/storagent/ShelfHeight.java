package poollovernathan.fabric.storagent;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

import static poollovernathan.fabric.storagent.ExampleMod.id;

public enum ShelfHeight {
    VERY_SHORT("Very Short", 0.1875f),
    SHORT("Short", 0.25f),
    MEDIUM("Medium", 0.50f),
    TALL("Tall", 0.75f),
    VERY_TALL("Very Tall", 1.0f);

    public final String name;
    public final float height;
    public final TagKey<Item> itemTagKey;
    public final TagKey<Block> blockTagKey;

    ShelfHeight(String name, float height) {
        this.name = name;
        this.height = height;
        this.itemTagKey = TagKey.of(Registry.ITEM_KEY, id("%s_shelves".formatted(name().toLowerCase())));
        this.blockTagKey = TagKey.of(Registry.BLOCK_KEY, id("%s_shelves".formatted(name().toLowerCase())));
    }

    public ShelfHeight increment() {
        var idx = this.ordinal() + 1;
        return ShelfHeight.values()[idx % ShelfHeight.values().length];
    }
    public ShelfHeight decrement() {
        var idx = this.ordinal() - 1;
        return ShelfHeight.values()[idx % ShelfHeight.values().length];
    }
}
