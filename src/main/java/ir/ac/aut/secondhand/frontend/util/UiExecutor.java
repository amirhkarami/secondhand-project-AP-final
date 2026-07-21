package ir.ac.aut.secondhand.frontend.util;

import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class UiExecutor implements AutoCloseable {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public <T> void run(Supplier<T> action, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        executor.submit(() -> {
            try {
                T result = action.get();
                Platform.runLater(() -> onSuccess.accept(result));
            } catch (Throwable throwable) {
                Platform.runLater(() -> onError.accept(throwable));
            }
        });
    }

    public void runVoid(Runnable action, Runnable onSuccess, Consumer<Throwable> onError) {
        run(() -> {
            action.run();
            return null;
        }, ignored -> onSuccess.run(), onError);
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
