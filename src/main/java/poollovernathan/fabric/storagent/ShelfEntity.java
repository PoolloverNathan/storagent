package poollovernathan.fabric.storagent;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

import static poollovernathan.fabric.storagent.ExampleMod.SHELF_BLOCK_ENTITY;
import static poollovernathan.fabric.storagent.ExampleMod.pick;

public class ShelfEntity extends BlockEntity implements ImplementedInventory, CappedInventory {
    protected final DefaultedList<ItemStack> items = DefaultedList.ofSize(16, ItemStack.EMPTY);

    public ShelfEntity(BlockPos pos, BlockState state) {
        super(SHELF_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        items.clear();
        Inventories.readNbt(nbt, items);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);
        super.writeNbt(nbt);
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null) {
            var state = world.getBlockState(pos);
            world.updateListeners(pos, state, state, 0);
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        var emptySlots = new ArrayList<Integer>(16);
        var filledSlots = new ArrayList<Integer>(16);
        for (var i = 0; i < size(); i++) {
            (getStack(i).isEmpty() ? emptySlots : filledSlots).add(i);
        }

        assert world != null;
        var source = new Pair<>(
            pick(emptySlots, world.random),
            pick(filledSlots, world.random)
        );

        return Stream.of(source.left(), source.right())
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .toArray();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }
}
