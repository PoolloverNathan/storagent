package poollovernathan.fabric.storagent;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import static poollovernathan.fabric.storagent.ExampleMod.*;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.registry.Registry;

public class ExampleModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		LOGGER.info("Client initialized!");
		BlockEntityRendererRegistry.register(SHELF_BLOCK_ENTITY, ShelfRenderer::new);
	}
}