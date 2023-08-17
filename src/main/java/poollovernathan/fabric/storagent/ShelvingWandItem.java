package poollovernathan.fabric.storagent;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static poollovernathan.fabric.storagent.ExampleMod.between;

public class ShelvingWandItem extends BundleItem {
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
//        new MutableText(
//                Text.translatable("tooltip.storagent.shelving_wand.surface"),
//                List.of(
//                        Text.of(surfaceMaterial(stack).orElse(ShelfSurfaceMaterial.OAK).name)
//                ),
//                Formatting.WHITE
//        );
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

    static protected <T extends Enum<T>> Optional<T> getEnumKey(ItemStack stack, String key, Function<String, T> valueOf) {
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
    static protected <T extends Enum<T>> void putEnumKey(ItemStack stack, String key, T value) {
        var nbt = stack.getOrCreateNbt();
        var name = value.name();
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
    static public void surfaceMaterial(ItemStack stack, ShelfSurfaceMaterial material) {
        putEnumKey(stack, "SurfaceMaterial", material);
    }
    static public void supportMaterial(ItemStack stack, ShelfSupportMaterial material) {
        putEnumKey(stack, "SupportMaterial", material);
    }
    static public void height(ItemStack stack, ShelfHeight material) {
        putEnumKey(stack, "Height", material);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return super.use(world, user, hand);
    }

    public ShelvingWandItem(Settings settings) {
        super(settings);
    }
}
