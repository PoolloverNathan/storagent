package poollovernathan.fabric.storagent;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static poollovernathan.fabric.storagent.ExampleMod.between;

public class ShelvingWandItem extends BundleItem {
    ShelvingWandItem(Settings settings) {
        super(settings);
    }
    enum EditingMode {
        NONE,
        BUNDLE,
        SURFACE,
        SUPPORT,
        HEIGHT
    }
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        final var surface = surfaceMaterial(stack).orElse(ShelfSurfaceMaterial.OAK);
        final var supports = supportMaterial(stack).orElse(ShelfSupportMaterial.OAK);
        int nsurface = 0, nsupports = 0;
        var nbt = stack.getNbt();
        if (nbt != null) {
            var items = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
            for (var stackData : items) {
                var invStack = ItemStack.fromNbt((NbtCompound) stackData);
                if (invStack.getItem() == surface.slab.asItem()) {
                    nsurface += invStack.getCount();
                }
                if (invStack.getItem() == supports.supportsBlock.asItem()) {
                    nsupports += invStack.getCount();
                }
            }
        }
        final var editingMode = editingMode(stack);
        appendProperty(tooltip, "Edit", title(editingMode.name().toLowerCase()), editingActive(stack) ? Formatting.WHITE : editingMode == EditingMode.NONE ? Formatting.GRAY : Formatting.GREEN);
        appendProperty(tooltip, "Surface", surface.name, " x%d".formatted(nsurface), nsurface == 0 ? Formatting.RED : Formatting.WHITE);
        appendProperty(tooltip, "Supports", supports.name, " x%d".formatted(nsupports), nsupports == 0 ? Formatting.RED : Formatting.WHITE);
        final var height = height(stack).orElse(ShelfHeight.MEDIUM);
        appendProperty(tooltip, "Height", height.name, switch (height) {
            case VERY_SHORT -> Formatting.RED;
            case SHORT -> Formatting.YELLOW;
            case MEDIUM -> Formatting.GREEN;
            case TALL -> Formatting.BLUE;
            case VERY_TALL -> Formatting.AQUA;
        });
    }

    static String title(String s) {
        if (s.length() == 0) return s;
        if (s.length() == 1) return s.toUpperCase();
        final var ary = s.split("", 2);
        assert ary.length == 2;
        return ary[0].toUpperCase() + ary[1];
    }

    static <T extends Enum<T>> T increment(T value, int offset) {
        @SuppressWarnings("unchecked") final T[] values = (T[]) value.getClass().getEnumConstants();
        return values[(value.ordinal() + offset) % values.length];
    }
    static <T extends Enum<T>> T increment(T value) {
        return increment(value, 1);
    }

    @SuppressWarnings("unused")
    static <T extends Enum<T>> T decrement(T value) {
        return increment(value, -1);
    }

    void copyFrom(ItemStack stack, ShelfBlock shelf) {
        surfaceMaterial(stack, shelf.surface);
        supportMaterial(stack, shelf.supports);
        height(stack, shelf.height);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (!otherStack.isEmpty()) {
            if (otherStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShelfBlock shelfBlock) {
                if (editingActive(stack)) {
                    copyFrom(stack, shelfBlock);
                    return true;
                } else if (editingMode(stack) != EditingMode.BUNDLE) {
                    switch (editingMode(stack)) {
                        case NONE -> copyFrom(stack, shelfBlock);
                        case BUNDLE -> throw new IllegalStateException("Unreachable");
                        case SURFACE -> surfaceMaterial(stack, shelfBlock.surface);
                        case SUPPORT -> supportMaterial(stack, shelfBlock.supports);
                        case HEIGHT -> height(stack, shelfBlock.height);
                    }
                    return true;
                }
            }
            editingMode(stack, EditingMode.BUNDLE);
            editingActive(stack, false);
            return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
        }
        if (editingActive(stack)) {
            switch (clickType) {
                case LEFT -> editingMode(stack, increment(editingMode(stack)));
                case RIGHT -> editingActive(stack, false);
            }
        } else {
            var mode = editingMode(stack);
            if (mode == EditingMode.NONE) {
                if (clickType == ClickType.RIGHT) {
                    editingActive(stack, true);
                } else {
                    return false;
                }
            } else if (clickType == ClickType.LEFT) {
                editingActive(stack, true);
            } else switch (mode) {
                case NONE -> throw new IllegalStateException("unreachable");
                case BUNDLE -> {
                    return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
                }
                case SURFACE -> surfaceMaterial(stack, increment(surfaceMaterial(stack).orElse(ShelfSurfaceMaterial.OAK)));
                case SUPPORT -> supportMaterial(stack, increment(supportMaterial(stack).orElse(ShelfSupportMaterial.OAK)));
                case HEIGHT -> height(stack, increment(height(stack).orElse(ShelfHeight.MEDIUM)));
            }
        }
        return true;
    }

    void appendProperty(List<Text> tooltip, String name, String value, Formatting... format) {
        appendProperty(tooltip, name, value, "", format);
    }
    void appendProperty(List<Text> tooltip, String name, String value, String tag, Formatting... format) {
        tooltip.add(Text.of(name).copy().append(": ").setStyle(Style.EMPTY.withFormatting(Formatting.GRAY)).append(Text.of(value).copy().setStyle(Style.EMPTY.withFormatting(format))).append(tag));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (Optional.ofNullable(context.getPlayer()).map(PlayerEntity::isSneaking).orElse(false)) {
            var world = context.getWorld();
            var wbs = world.getBlockState(context.getBlockPos());
            if (wbs.getBlock() instanceof ShelfBlock shelfBlock) {
                var blockRelPos = context.getHitPos();
                blockRelPos = new Vec3d(blockRelPos.x % 1, blockRelPos.y % 1, blockRelPos.z % 1);
                switch (context.getSide()) {
                    case UP, DOWN -> {
                        var nextBlock = ShelfBlock.get(shelfBlock.surface, shelfBlock.supports, between(blockRelPos.x, 0.125, 0.875) && between(blockRelPos.z, 0.125, 0.875) ? shelfBlock.height.increment() : shelfBlock.height.decrement());
                        if (world.setBlockState(context.getBlockPos(), nextBlock.getDefaultState())) {
                            return ActionResult.SUCCESS;
                        }
                    }
                    default -> {
                        return ActionResult.PASS;
                    }
                }
            }
        }
        var block = block(context.getStack(), ShelfSurfaceMaterial.OAK, ShelfSupportMaterial.OAK, ShelfHeight.MEDIUM);
        var creative = Optional.ofNullable(context.getPlayer()).map(PlayerEntity::isCreative).orElse(false);
        NbtList items = null;
        NbtCompound surfaceStack = null;
        NbtCompound supportStack = null;
        if (!creative) {
            var nbt = context.getStack().getNbt();
            if (nbt == null) return ActionResult.FAIL;
            items = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
            for (var stackData : items) {
                var stack = ItemStack.fromNbt((NbtCompound) stackData);
                if (surfaceStack == null && stack.getCount() >= 2 && stack.getItem() == block.surface.slab.asItem()) {
                    surfaceStack = (NbtCompound) stackData;
                }
                if (supportStack == null && stack.getCount() >= 2 && stack.getItem() == block.supports.supportsBlock.asItem()) {
                    supportStack = (NbtCompound) stackData;
                }
            }
            if (surfaceStack == null || supportStack == null) return ActionResult.FAIL;
        }
        var nestedResult = block.asItem().useOnBlock(context);
        if (nestedResult.isAccepted()) {
            if (!creative) {
                surfaceStack.putInt("Count", surfaceStack.getInt("Count") - 2);
                if (surfaceStack.getInt("Count") <= 0) {
                    if (!items.remove(surfaceStack)) {
                        throw new IllegalStateException("Surface stack does not exist in the item, despite being copied from the item list - this should never happen");
                    }
                }
                supportStack.putInt("Count", supportStack.getInt("Count") - 2);
                if (supportStack.getInt("Count") <= 0) {
                    if (!items.remove(supportStack)) {
                        throw new IllegalStateException("Support stack does not exist in the item, despite being copied from the item list - this should never happen");
                    }
                }
                context.getStack().increment(1); // workaround for BlockItem consuming the item
            }
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.FAIL;
        }
    }

    @Contract(pure = true)
    static public ShelfBlock block(ItemStack stack, ShelfSurfaceMaterial defaultSurface, ShelfSupportMaterial defaultSupport, ShelfHeight defaultHeight) {
        return ShelfBlock.get(surfaceMaterial(stack).orElse(defaultSurface), supportMaterial(stack).orElse(defaultSupport), height(stack).orElse(defaultHeight));
    }

    static protected <T> Optional<T> getEnumKey(ItemStack stack, String key, Function<String, T> valueOf) {
        var nbt = stack.getNbt();
        if (nbt == null) return Optional.empty();
        if (nbt.contains(key, NbtElement.STRING_TYPE)) {
            var name = nbt.getString(key);
            try {
                return Optional.of(valueOf.apply(name.toUpperCase()));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
    static protected <T> void putEnumKey(ItemStack stack, String key, T value) {
        var nbt = stack.getOrCreateNbt();
        var name = value instanceof Enum<?> e ? e.name() : value.toString();
        nbt.putString(key, name.toLowerCase());
    }
    static public Optional<ShelfSurfaceMaterial> surfaceMaterial(ItemStack stack) {
        return getEnumKey(stack, "SurfaceMaterial", ShelfSurfaceMaterial::valueOf);
    }
    static public Optional<ShelfSupportMaterial> supportMaterial(ItemStack stack) {
        return getEnumKey(stack, "SupportMaterial", ShelfSupportMaterial::valueOf);
    }
    static public Optional<ShelfHeight> height(ItemStack stack) {
        return getEnumKey(stack, "Height", ShelfHeight::valueOf);
    }
    static public EditingMode editingMode(ItemStack stack) {
        return getEnumKey(stack, "EditingMode", EditingMode::valueOf).orElse(EditingMode.NONE);
    }
    static public boolean editingActive(ItemStack stack) {
        var nbt = stack.getNbt();
        if (nbt == null) return false;
        return nbt.getBoolean("EditingActive");
    }
    static public void surfaceMaterial(ItemStack stack, ShelfSurfaceMaterial material) {
        putEnumKey(stack, "SurfaceMaterial", material);
    }
    static public void supportMaterial(ItemStack stack, ShelfSupportMaterial material) {
        putEnumKey(stack, "SupportMaterial", material);
    }
    static public void height(ItemStack stack, ShelfHeight material) {
        putEnumKey(stack, "Height", material);
    }
    static public void editingMode(ItemStack stack, EditingMode mode) {
        putEnumKey(stack, "EditingMode", mode);
    }
    static public void editingActive(ItemStack stack, boolean active) {
        var nbt = stack.getOrCreateNbt();
        nbt.putBoolean("EditingActive", active);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand);
    }
}
