package poollovernathan.fabric.storagent;

import net.minecraft.entity.player.PlayerEntity;

public enum ShelfHeight {
    VERY_SHORT("Very Short", 0.1875f),
    SHORT("Short", 0.25f),
    MEDIUM("Medium", 0.50f),
    TALL("Tall", 0.75f),
    VERY_TALL("Very Tall", 1.0f);

    public final String name;
    public final float height;

    ShelfHeight(String name, float height) {
        this.name = name;
        this.height = height;
    }
}
