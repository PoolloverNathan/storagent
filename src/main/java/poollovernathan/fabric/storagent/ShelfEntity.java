package poollovernathan.fabric.storagent;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import static poollovernathan.fabric.storagent.ExampleMod.SHELF_BLOCK_ENTITY;

public class ShelfEntity extends BlockEntity implements ImplementedInventory {
    protected final DefaultedList<ItemStack> items = DefaultedList.ofSize(16, ItemStack.EMPTY);

    public ShelfEntity(BlockPos pos, BlockState state) {
        super(SHELF_BLOCK_ENTITY, pos, state);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }
}
