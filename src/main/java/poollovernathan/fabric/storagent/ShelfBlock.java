package poollovernathan.fabric.storagent;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static poollovernathan.fabric.storagent.ExampleMod.*;

public class ShelfBlock extends Block implements BlockEntityProvider {
    public static final TagKey<Block> BLOCK_TAG_KEY = TagKey.of(Registry.BLOCK_KEY, id("shelves"));
    public static final TagKey<Item> ITEM_TAG_KEY = TagKey.of(Registry.ITEM_KEY, id("shelves"));
    public final ShelfSurfaceMaterial surface;
    public final ShelfSupportMaterial supports;
    public final ShelfHeight height;

    ShelfBlock(Settings settings, ShelfSurfaceMaterial surface, ShelfSupportMaterial supports, ShelfHeight height) {
        super(settings);
        this.surface = surface;
        this.supports = supports;
        this.height = height;
    }

    public static FabricTagProvider.BlockTagProvider provideBlockTags(FabricDataGenerator gen) {
        return new FabricTagProvider.BlockTagProvider(gen) {
            @Override
            protected void generateTags() {
                for (var block: SHELF_BLOCKS) {
                    block.sendBlockTags(this::getOrCreateTagBuilder);
                }
            }
        };
    }
    public static FabricTagProvider.ItemTagProvider provideItemTags(FabricDataGenerator gen) {
        return new FabricTagProvider.ItemTagProvider(gen) {
            @Override
            protected void generateTags() {
                for (var block: SHELF_BLOCKS) {
                    block.sendItemTags(this::getOrCreateTagBuilder);
                }
            }
        };
    }

    public String createId() {
        return "%s_%s_shelf_%s_supports".formatted(height.name().toLowerCase(), surface.name().toLowerCase(), supports.name().toLowerCase());
    }

    public Optional<CraftingRecipeJsonBuilder> createCraftingRecipe() {
        if (height == ShelfHeight.MEDIUM) {
            return Optional.of(new ShapedRecipeJsonBuilder(asItem(), 1)
                    .group(id("medium_shelves").toString())
                    .input('f', surface.slab)
                    .input('p', supports.supportsBlock)
                    .pattern("ff")
                    .pattern("pp")
                    .criterion(createId() + "_recipe", FabricRecipeProvider.conditionsFromItem(surface.slab)));
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

    public static ShelfBlock get(ShelfSurfaceMaterial surface, ShelfSupportMaterial supports, ShelfHeight height) {
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
        return combineAll(
                VoxelShapes.cuboid(0, height.height - 0.125f, 0, 1, height.height, 1),
                VoxelShapes.cuboid(0, 0, 0, 0.125f, height.height - 0.125f, 0.125f),
                VoxelShapes.cuboid(0, 0, 1 - 0.125f, 0.125f, height.height - 0.125f, 1),
                VoxelShapes.cuboid(1 - 0.125f, 0, 0, 1, height.height - 0.125f, 0.125f),
                VoxelShapes.cuboid(1 - 0.125f, 0, 1 - 0.125f, 1, height.height - 0.125f, 1)
        );
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() instanceof ShelfBlock && newState.getBlock() instanceof ShelfBlock) {
            return;
        } else if (!moved) {
            dropContents(world, pos, world.getBlockEntity(pos) instanceof ShelfEntity shelf ? shelf : fail(new IllegalStateException("Shelf does not have shelf entity")), false);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
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
        var items = getItems(world, pos);
        if (items.isPresent()) {
            var i = 0;
            for (var stack: items.get()) {
                if (stack.getItem() instanceof ExposedItemBehavior exposedItem) {
                    var result = exposedItem.precipitationTick(stack, precipitation);
                    if (result.isPresent()) {
                        items.get().set(i, result.get());
                    }
                }
                i++;
            }
        }
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

    protected Optional<DefaultedList<ItemStack>> getItems(World world, BlockPos pos) {
        return world.getBlockEntity(pos, SHELF_BLOCK_ENTITY.get()).map((entity) -> entity.items);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        var items = getItems(world, pos);
        if (items.isPresent()) {
            var i = 0;
            for (var stack: items.get()) {
                if (stack.getItem() instanceof ExposedItemBehavior exposedItem) {
                    var result = exposedItem.randomTick(stack);
                    if (result.isPresent()) {
                        items.get().set(i, result.get());
                    }
                }
                i++;
            }
        }
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
            if (player.isSneaking()) {
                dropContents(world, pos, shelfEntity, true);
            } else {
                var stack = shelfEntity.getStack(slot);
                if (stack.isEmpty()) {
                    shelfEntity.setStack(slot, player.getStackInHand(hand).split(1));
                } else {
                    var craftedItem = craftViaShelfInteraction(player, pos, stack);
                    if (craftedItem.isPresent()) {
                        shelfEntity.setStack(slot, craftedItem.get().getLeft());
                        world.playSound(hit.getPos().x, hit.getPos().y, hit.getPos().z, craftedItem.get().getRight(), SoundCategory.PLAYERS, 1, 1, false);
                    } else {
                        player.giveItemStack(stack);
                        shelfEntity.setStack(slot, ItemStack.EMPTY);
                    }
                }
            }
            shelfEntity.markDirty();
            return ActionResult.SUCCESS;
        } else {
            ExampleMod.LOGGER.error("Shelf at %s %s has no block entity".formatted(world.getRegistryKey().getValue(), pos));
            return ActionResult.FAIL;
        }
    }

    public Optional<Pair<ItemStack, SoundEvent>> craftViaShelfInteraction(PlayerEntity player, BlockPos pos, ItemStack shelfStack) {
        var mainhand = player.getStackInHand(Hand.MAIN_HAND);
        var offhand = player.getStackInHand(Hand.OFF_HAND);
        if (mainhand.getItem() == Items.BUNDLE && shelfStack.getItem() == Items.STICK && offhand.getItem() == Items.ENDER_PEARL) {
            mainhand.use(player.world, player, Hand.MAIN_HAND);
            mainhand.decrement(1);
            offhand.decrement(1);
            return Optional.of(new Pair<>(SHELVING_WAND_ITEM.getDefaultStack(), new SoundEvent(vid("item.axe.strip"))));
        }
        if (shelfStack.getItem() == Items.COMMAND_BLOCK && mainhand.getItem() == Items.WRITABLE_BOOK && offhand.getItem() == Items.REDSTONE_TORCH && WritableBookItem.isValid(mainhand.getNbt())) {
            assert mainhand.getNbt() != null;
            NbtList pages = mainhand.getNbt().getList("pages", NbtElement.STRING_TYPE);
            var server = player.world.getServer();
            assert server != null;
            var source = server.getCommandSource().withEntity(player).withPosition(Vec3d.ofCenter(pos)).withOutput(player);
            var resultBook = Items.WRITTEN_BOOK.getDefaultStack();
            var resultPages = new NbtList();
            var resultNbt = resultBook.getOrCreateNbt();
            resultNbt.put("pages", resultPages);
            resultNbt.putString("title", "Execution Results");
            resultNbt.putString("author", "Command Block");
            var i = 0;
            for (var page: pages) {
                var localI = i++;
                var command = page.asString();
                server.getCommandManager().executeWithPrefix(source.withOutput(new CommandOutput() {
                    final int i = localI;
                    @Override
                    public void sendMessage(Text message) {
                        resultPages.add(NbtString.of(Text.Serializer.toJson(message)));
                    }

                    @Override
                    public boolean shouldReceiveFeedback() {
                        return true;
                    }

                    @Override
                    public boolean shouldTrackOutput() {
                        return true;
                    }

                    @Override
                    public boolean shouldBroadcastConsoleToOps() {
                        return false;
                    }
                }), command);
            }

            return Optional.of(new Pair<>(resultBook, new SoundEvent(vid("block.amethyst_block.step"))));
        }
        if (shelfStack.getItem() == Items.ORANGE_GLAZED_TERRACOTTA && mainhand.getItem() == Items.HEAVY_WEIGHTED_PRESSURE_PLATE && offhand.getItem() == Items.REDSTONE) {
            mainhand.decrement(1);
            offhand.decrement(1);
            return Optional.of(new Pair<>(Items.COMMAND_BLOCK.getDefaultStack(), new SoundEvent(vid("block.enchantment_table.use"))));
        }
        return Optional.empty();
    }

    public void dropContents(World world, BlockPos pos, ShelfEntity shelf, boolean clearItems) {
        for (var x = 0; x < 4; x++) {
            for (var z = 0; z < 4; z++) {
                var slot = z * 4 + x;
                var stack = shelf.getStack(slot);
                if (!stack.isEmpty()) {
                    var itemPos = new Vec3d(pos.getX() + ((pos.getX() < 0 ? 3 - x : x) / 4f) + 0.125, pos.getY() + height.height, pos.getZ() + ((pos.getZ() < 0 ? 3 - z : z) / 4f) + 0.125);
                    var entity = new ItemEntity(world, itemPos.x, itemPos.y, itemPos.z, stack, 0, 0.1, 0);
                    entity.age = slot; // should help prevent item deletion
                    world.spawnEntity(entity);
                    entity.setPos(itemPos.x, itemPos.y, itemPos.z);
                }
            }
        }
        if (clearItems) {
            shelf.items.clear();
            shelf.markDirty();
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        var items = getItems(world, pos).orElse(DefaultedList.ofSize(16, ItemStack.EMPTY));
        var o = 16;
        for (var stack: items) {
            if (stack.isEmpty()) o--;
        }
        return o;
    }

    public void sendBlockTags(Function<TagKey<Block>, AbstractTagProvider.ObjectBuilder<Block>> getBlockBuilder) {
        for (var blockTag : List.of(surface.blockTagKey, surface.containBlockTagKey, supports.blockTagKey, supports.containBlockTagKey, height.blockTagKey, BLOCK_TAG_KEY)) {
            getBlockBuilder.apply(blockTag).add(this);
        }
    }

    public void sendItemTags(Function<TagKey<Item>, AbstractTagProvider.ObjectBuilder<Item>> getItemBuilder) {
        for (var itemTag : List.of(surface.itemTagKey, surface.containItemTagKey, supports.itemTagKey, supports.containItemTagKey, height.itemTagKey, ITEM_TAG_KEY)) {
            getItemBuilder.apply(itemTag).add(asItem());
        }
    }

    static interface ExposedItemBehavior {
        Optional<ItemStack> precipitationTick(ItemStack stack, Biome.Precipitation precipitation);
        Optional<ItemStack> randomTick(ItemStack stack);
    }
}
