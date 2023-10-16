//package poollovernathan.fabric.storagent;
//
//import com.google.common.collect.ImmutableMap;
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
//import net.minecraft.client.gui.screen.narration.NarrationPart;
//import net.minecraft.client.gui.widget.ButtonWidget;
//import net.minecraft.client.gui.widget.ClickableWidget;
//import net.minecraft.client.render.GameRenderer;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.text.Text;
//import net.minecraft.util.Lazy;
//import org.jetbrains.annotations.Contract;
//
//import java.util.Map;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//
//public class ShelfWandScreen extends Screen {
//    @Contract(mutates = "param1")
//    static <K, V> ImmutableMap<K, V> buildByIdx(ImmutableMap.Builder<K, V> map, K[] keys, BiFunction<K, Integer, V> valueFactory) {
//        for (var i = 0; i < keys.length; i++) {
//            map.put(keys[i], valueFactory.apply(keys[i], i));
//        }
//        return map.build();
//    }
//    @Contract(mutates = "param1")
//    static <K, V> ImmutableMap<K, V> buildBy(ImmutableMap.Builder<K, V> map, Iterable<K> keys, Function<K, V> valueFactory) {
//        for (var key: keys) {
//            map.put(key, valueFactory.apply(key));
//        }
//        return map.build();
//    }
//    @Contract(mutates = "param1")
//    static <K, V, M extends Map<K, V>> M putByIdx(M map, K[] keys, BiFunction<K, Integer, V> valueFactory) {
//        for (var i = 0; i < keys.length; i++) {
//            map.put(keys[i], valueFactory.apply(keys[i], i));
//        }
//        return map;
//    }
//    @Contract(mutates = "param1")
//    static <K, V, M extends Map<K, V>> M putBy(M map, Iterable<K> keys, Function<K, V> valueFactory) {
//        for (var key: keys) {
//            map.put(key, valueFactory.apply(key));
//        }
//        return map;
//    }
//    static Lazy<Map<ShelfSurfaceMaterial, ClickableWidget>> rebuildSurfaces(Supplier<ShelfSurfaceMaterial> selected, int x, int y) {
//        return putByIdx(new ImmutableMap.Builder<ShelfSurfaceMaterial, ClickableWidget>(), ShelfSurfaceMaterial.values(), (ShelfSurfaceMaterial surface, int i) -> new ClickableWidget(x + 18 * i, y, 18, 18, "Surface: %s".formatted(surface.name)) {
//
//            @Override
//            public void appendNarrations(NarrationMessageBuilder builder) {
//                builder.put(NarrationPart.USAGE, "Click to set surface to " + surface.name);
//            }
//
//            @Override
//            public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//                RenderSystem.setShader(GameRenderer::getPositionTexShader);
//                RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
//                drawTexture(matrices, 0, 0, 1, 1, 20, 20);
//            }
//        }).build();
//    }
//
//    protected ShelfWandScreen(Text title) {
//        super(title);
//    }
//
//    @Override
//    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//        super.render(matrices, mouseX, mouseY, delta);
//    }
//}
