package poollovernathan.fabric.storagent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;

public class ShelfRenderer implements BlockEntityRenderer<ShelfEntity> {
    public ShelfRenderer(BlockEntityRendererFactory.Context ctx) {}
    @Override
    public void render(ShelfEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        for (var x = 0; x < 4; x++) {
            for (var z = 0; z < 4; z++) {
                var slot = z * 4 + x;
                var stack = entity.getStack(slot);
                if (!stack.isEmpty()) {
                    matrices.push();
                    matrices.translate((x / 4f) + 0.125, (entity.getCachedState().getBlock() instanceof ShelfBlock shelf ? shelf.height.height : 0.5), (z / 4f) + 0.125);
                    itemRenderer.renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0);
                    matrices.pop();
                }
            }
        }
    }
}
