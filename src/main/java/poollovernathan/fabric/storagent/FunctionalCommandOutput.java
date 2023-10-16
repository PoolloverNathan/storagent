package poollovernathan.fabric.storagent;

import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FunctionalCommandOutput implements CommandOutput {
    protected final Consumer<Text> consumer;
    protected final Supplier<Boolean> shouldReceiveFeedback;
    protected final Supplier<Boolean> shouldTrackOutput;
    protected final Supplier<Boolean> shouldBroadcastConsoleToOps;

    public FunctionalCommandOutput(Consumer<Text> consumer, Supplier<Boolean> shouldReceiveFeedback, Supplier<Boolean> shouldTrackOutput, Supplier<Boolean> shouldBroadcastConsoleToOps) {
        this.consumer = consumer;
        this.shouldReceiveFeedback = shouldReceiveFeedback;
        this.shouldTrackOutput = shouldTrackOutput;
        this.shouldBroadcastConsoleToOps = shouldBroadcastConsoleToOps;
    }

    public ServerCommandSource applyTo(ServerCommandSource source) {
        return source.withOutput(this);
    }

    public FunctionalCommandOutput(Consumer<Text> consumer, boolean shouldReceiveFeedback, boolean shouldTrackOutput, boolean shouldBroadcastConsoleToOps) {
        this(consumer, () -> shouldReceiveFeedback, () -> shouldTrackOutput, () -> shouldBroadcastConsoleToOps);
    }

    @Override
    public void sendMessage(Text message) {

    }

    @Override
    public boolean shouldReceiveFeedback() {
        return false;
    }

    @Override
    public boolean shouldTrackOutput() {
        return false;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return false;
    }
}