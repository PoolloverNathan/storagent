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
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

import static poollovernathan.fabric.storagent.ExampleMod.id;

public class ShelfBlock extends Block implements BlockEntityProvider {
    public final ShelfMaterial surface;
    public final ShelfMaterial supports;

    ShelfBlock(Settings settings, ShelfMaterial surface, ShelfMaterial supports) {
        super(settings);
        this.surface = surface;
        this.supports = supports;
    }

    public String createId() {
        return "shelf_%s_%s".formatted(surface.name().toLowerCase(), supports.name().toLowerCase());
    }

    public CraftingRecipeJsonBuilder createRecipe() {
        return new ShapedRecipeJsonBuilder(asItem(), 1)
                .group(id("shelves").toString())
                .input('f', surface.slabBlock)
                .input('p', supports.logBlock)
                .pattern("ff")
                .pattern("pp")
                .criterion(createId() + "_recipe", FabricRecipeProvider.conditionsFromItem(surface.slabBlock));
    }

    public String getTrueName() {
        return surface.name + " Shelf";
    }

    protected static VoxelShape getShape() {
        var legHeight = 6/16f;
        var topHeight = legHeight + 2/16f;
        var legWidth = 2/16f;
        return combineAll(
                VoxelShapes.cuboid(0, legHeight, 0, 1, topHeight, 1),
                VoxelShapes.cuboid(0, 0, 0, legWidth, legHeight, legWidth),
                VoxelShapes.cuboid(0, 0, 1 - legWidth, legWidth, legHeight, 1),
                VoxelShapes.cuboid(1 - legWidth, 0, 0, 1, legHeight, legWidth),
                VoxelShapes.cuboid(1 - legWidth, 0, 1 - legWidth, 1, legHeight, 1)
        );
    }

    @Override
    protected ImmutableMap<BlockState, VoxelShape> getShapesForStates(Function<BlockState, VoxelShape> stateToShape) {
        return ImmutableMap.<BlockState, VoxelShape>builder().put(getDefaultState(), getShape()).build();
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
        return getShape();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape();
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

    interface ExposedItemBehavior {
        void precipitationTick(ItemStack stack, Biome.Precipitation precipitation);
        void randomTick(ItemStack stack);
    }
}
