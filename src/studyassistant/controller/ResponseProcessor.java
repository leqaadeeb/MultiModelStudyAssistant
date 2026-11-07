package studyassistant.controller;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import studyassistant.model.LLMResponse;
import studyassistant.model.ResponseStatus;
import studyassistant.strategy.LLMStrategy;

/**
 * Runs one strategy request, applies the timeout, and returns a final
 * LLMResponse with a success, failure, timeout, or invalid status.
 */
public class ResponseProcessor {

    public static final long DEFAULT_TIMEOUT_MS = 60_000L;

    private final long timeoutMs;
    private final Executor deliveryExecutor;

    public ResponseProcessor() {
        this(DEFAULT_TIMEOUT_MS, SwingUtilities::invokeLater);
    }

    public ResponseProcessor(long timeoutMs, Executor deliveryExecutor) {
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("Timeout must be positive");
        }
        this.timeoutMs = timeoutMs;
        this.deliveryExecutor = Objects.requireNonNull(deliveryExecutor, "deliveryExecutor");
    }

    public CompletableFuture<LLMResponse> process(
            LLMStrategy strategy,
            String question,
            Consumer<LLMResponse> onResult) {
        Objects.requireNonNull(strategy, "strategy");
        Objects.requireNonNull(question, "question");
        Objects.requireNonNull(onResult, "onResult");

        String modelName = strategy.getDisplayName();
        long startNanos = System.nanoTime();

        CompletableFuture<LLMResponse> request;
        try {
            request = strategy.generate(question);
            if (request == null) {
                request = CompletableFuture.failedFuture(
                        new IllegalStateException("Strategy returned a null future"));
            }
        } catch (RuntimeException error) {
            request = CompletableFuture.failedFuture(error);
        }

        return request
                .orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .handle((response, error) ->
                        toResponse(modelName, startNanos, response, error))
                .thenApply(result -> {
                    deliveryExecutor.execute(() -> onResult.accept(result));
                    return result;
                });
    }

    private static LLMResponse toResponse(
            String modelName,
            long startNanos,
            LLMResponse response,
            Throwable error) {
        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - startNanos);

        if (error != null) {
            Throwable cause = unwrap(error);
            ResponseStatus status = cause instanceof TimeoutException
                    ? ResponseStatus.TIMEOUT
                    : ResponseStatus.FAILED;
            return new LLMResponse(modelName, null, status, elapsedMs, cause.toString());
        }

        if (response == null
                || response.getText() == null
                || response.getText().trim().isEmpty()) {
            return new LLMResponse(
                    modelName,
                    null,
                    ResponseStatus.INVALID_RESPONSE,
                    elapsedMs,
                    "The model returned an empty or invalid response.");
        }

        return response;
    }

    private static Throwable unwrap(Throwable error) {
        Throwable cause = error;
        while (cause instanceof CompletionException && cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}
