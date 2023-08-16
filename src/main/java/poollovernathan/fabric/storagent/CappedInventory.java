package poollovernathan.fabric.storagent;

import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface CappedInventory extends SidedInventory {
    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return getStack(slot).getCount() + stack.getCount() <= getMaxCountPerStack();
    }

    @Override
    default boolean isValid(int slot, ItemStack stack) {
        return SidedInventory.super.isValid(slot, stack) && stack.getCount() <= getMaxCountPerStack();
    }
}
