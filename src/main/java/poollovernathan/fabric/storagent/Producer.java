package poollovernathan.fabric.storagent;

@FunctionalInterface
public interface Producer<T> {
    T get();
}
