package poollovernathan.fabric.storagent;

import java.util.function.Supplier;

public final class Lazy<T> {
    private State state;
    private Lazy(State state) {
        this.state = state;
    }
    public Lazy(Supplier<T> create) {
        this(new Uninit<>(create));
    }

    public T get() {
        return switch (state) {
            case Init<T>(T v) -> v;
            case Uninit<T>(Supplier<T> s) -> {
                state = new Init<>(s.get());
                yield get();
            }
        };
    }

    private sealed interface State permits Init, Uninit {}
    private record Init<T>(T value) implements State {}
    private record Uninit<T>(Supplier<T> create) implements State {}
}