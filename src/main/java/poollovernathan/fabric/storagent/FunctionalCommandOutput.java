package poollovernathan.fabric.storagent;

import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class FunctionalCommandOutput implements CommandOutput {
    protected final Consumer<Text> consumer;
    protected final Producer<Boolean> shouldReceiveFeedback;
    protected final Producer<Boolean> shouldTrackOutput;
    protected final Producer<Boolean> shouldBroadcastConsoleToOps;

    public FunctionalCommandOutput(Consumer<Text> consumer, Producer<Boolean> shouldReceiveFeedback, Producer<Boolean> shouldTrackOutput, Producer<Boolean> shouldBroadcastConsoleToOps) {
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