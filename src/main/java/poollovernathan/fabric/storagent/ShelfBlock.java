package poollovernathan.fabric.storagent;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static poollovernathan.fabric.storagent.ExampleMod.SHELF_BLOCKS;
import static poollovernathan.fabric.storagent.ExampleMod.id;

public class ShelfBlock extends Block implements BlockEntityProvider {
    public final ShelfMaterial surface;
    public final ShelfMaterial supports;
    public final ShelfHeight height;

    ShelfBlock(Settings settings, ShelfMaterial surface, ShelfMaterial supports, ShelfHeight height) {
        super(settings);
        this.surface = surface;
        this.supports = supports;
        this.height = height;
    }

    public String createId() {
        return "%s_%s_shelf_%s_supports".formatted(height.name().toLowerCase(), surface.name().toLowerCase(), supports.name().toLowerCase());
    }

    public Optional<CraftingRecipeJsonBuilder> createCraftingRecipe() {
        if (height == ShelfHeight.MEDIUM) {
            return Optional.of(new ShapedRecipeJsonBuilder(asItem(), 1)
                    .group(id("medium_shelves").toString())
                    .input('f', surface.slabBlock)
                    .input('p', supports.logBlock)
                    .pattern("ff")
                    .pattern("pp")
                    .criterion(createId() + "_recipe", FabricRecipeProvider.conditionsFromItem(surface.slabBlock)));
        } else {
            return Optional.empty();
        }
    }

    public CraftingRecipeJsonBuilder createSwitchRecipe() {
        var vals = List.of(ShelfHeight.values());
        var heightIdx = vals.indexOf(height);
        if (heightIdx == 0) heightIdx = vals.size();
        var prevHeight = vals.get(heightIdx - 1);
        return new ShapelessRecipeJsonBuilder(asItem(), 1)
                .group(id(height.name().toLowerCase() + "_shelves").toString())
                .input(get(surface, supports, prevHeight))
                .criterion(createId() + "_recipe", FabricRecipeProvider.conditionsFromItem(get(surface, supports, ShelfHeight.MEDIUM)));
    }

    public static ShelfBlock get(ShelfMaterial surface, ShelfMaterial supports, ShelfHeight height) {
        for (var block: SHELF_BLOCKS) {
            if (block.surface == surface && block.supports == supports && block.height == height) {
                return block;
            }
        }
        throw new IllegalStateException("All possible permutations of arguments should've been generated by now, but (surface=%s supports=%s height=%s) was not".formatted(surface, supports, height));
    }

    public String getTrueName() {
        return "%s %s Shelf".formatted(height.name, surface.name);
    }

    protected static VoxelShape getShape(ShelfHeight height) {
        var legHeight = height.height - 2/16f;
        var legWidth = 2/16f;
        return combineAll(
                VoxelShapes.cuboid(0, legHeight, 0, 1, height.height, 1),
                VoxelShapes.cuboid(0, 0, 0, legWidth, legHeight, legWidth),
                VoxelShapes.cuboid(0, 0, 1 - legWidth, legWidth, legHeight, 1),
                VoxelShapes.cuboid(1 - legWidth, 0, 0, 1, legHeight, legWidth),
                VoxelShapes.cuboid(1 - legWidth, 0, 1 - legWidth, 1, legHeight, 1)
        );
    }

    @Override
    protected ImmutableMap<BlockState, VoxelShape> getShapesForStates(Function<BlockState, VoxelShape> stateToShape) {
        return ImmutableMap.<BlockState, VoxelShape>builder().put(getDefaultState(), getShape(height)).build();
    }
    static protected VoxelShape combineAll(VoxelShape first, VoxelShape... rest) {
        var shape = first;
        for (var newShape: rest) {
            shape = VoxelShapes.combine(shape, newShape, (a, b) -> a || b);
        }
        return shape;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return super.isTranslucent(state, world, pos);
    }

    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        // TODO: Once block entity is added, run ExposedItemBehavior::precipitationTick(precipitation) on all implementing items
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return getShape(height);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(height);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(height);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // TODO: Once block entity is added, run ExposedItemBehavior::randomTick() on all implementing items
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShelfEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        if (hit.getSide() != Direction.UP) return ActionResult.PASS;
        var slotX = (int) Math.floor(Math.abs(hit.getPos().x % 1) * 4);
        var slotZ = (int) Math.floor(Math.abs(hit.getPos().z % 1) * 4);
        var slot = slotZ * 4 + slotX;
        var entity = world.getBlockEntity(pos);
        if (entity instanceof ShelfEntity shelfEntity) {
            var stack = shelfEntity.getStack(slot);
            if (stack.isEmpty()) {
                shelfEntity.setStack(slot, player.getStackInHand(hand).split(1));
            } else {
                player.giveItemStack(stack);
                shelfEntity.setStack(slot, ItemStack.EMPTY);
            }
            shelfEntity.markDirty();
            return ActionResult.SUCCESS;
        } else {
            ExampleMod.LOGGER.error("Shelf at %s %s has no block entity".formatted(world.getRegistryKey().getValue(), pos));
            return ActionResult.FAIL;
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) instanceof ShelfEntity shelf) {
            for (var x = 0; x < 4; x++) {
                for (var z = 0; z < 4; z++) {
                    var slot = z * 4 + x;
                    var stack = shelf.getStack(slot);
                    if (!stack.isEmpty()) {
                        var itemPos = new Vec3d(pos.getX() + ((pos.getX() < 0 ? 3 - x : x) / 4f) + 0.125, pos.getY() + 0.5, pos.getZ() + ((pos.getZ() < 0 ? 3 - z : z) / 4f) + 0.125);
                        var entity = new ItemEntity(world, itemPos.x, itemPos.y, itemPos.z, stack, 0, 0.1, 0);
                        world.spawnEntity(entity);
                        entity.setPos(itemPos.x, itemPos.y, itemPos.z);
                    }
                }
            }
        }
    }

    interface ExposedItemBehavior {
        void precipitationTick(ItemStack stack, Biome.Precipitation precipitation);
        void randomTick(ItemStack stack);
    }
}
