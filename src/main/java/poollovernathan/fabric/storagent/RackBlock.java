package poollovernathan.fabric.storagent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static poollovernathan.fabric.storagent.ExampleMod.id;

public class RackBlock extends Block {
    public final RackMaterial surface;
    public final RackMaterial supports;

    RackBlock(Settings settings, RackMaterial surface, RackMaterial supports) {
        super(settings);
        this.surface = surface;
        this.supports = supports;
    }

    public String createId() {
        return "rack_%s_%s".formatted(surface.name().toLowerCase(), supports.name().toLowerCase());
    }

    public CraftingRecipeJsonBuilder createRecipe() {
        return new ShapedRecipeJsonBuilder(asItem(), 1)
                .group(id("racks").toString())
                .input('f', surface.slabBlock)
                .input('p', supports.logBlock)
                .pattern("ff")
                .pattern("pp")
                .criterion(createId() + "_recipe", FabricRecipeProvider.conditionsFromItem(surface.slabBlock));
    }

    public String getTrueName() {
        return surface.name + " Rack";
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

    interface ExposedItemBehavior {
        void precipitationTick(Biome.Precipitation precipitation);
        void randomTick();
    }
}
