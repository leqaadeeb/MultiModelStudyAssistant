package studyassistant.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import studyassistant.model.LLMResponse;
import studyassistant.model.QuerySession;
import studyassistant.model.ResponseStatus;
import studyassistant.strategy.LLMStrategy;
import studyassistant.strategy.MockLLMStrategy;

/**
 * Tests the response outcomes produced by ResponseProcessor.
 */
class ResponseProcessorTest {

    @Test
    void validResponseResultsInSuccess() throws Exception {
        QuerySession session = new QuerySession();
        LLMStrategy strategy = new MockLLMStrategy("Mock", "answer", 0);
        ResponseProcessor processor = new ResponseProcessor(1000, Runnable::run);

        LLMResponse result = processor.process(
                strategy,
                "question",
                response -> session.updateResponse("Mock", response))
                .get(2, TimeUnit.SECONDS);

        assertEquals(ResponseStatus.SUCCESS, result.getStatus());
        assertEquals("answer", result.getText());
        assertSame(result, session.getResponse("Mock"));
    }

    @Test
    void exceptionalCompletionResultsInFailed() throws Exception {
        LLMStrategy strategy = new LLMStrategy() {
            @Override
            public String getDisplayName() {
                return "Failing";
            }

            @Override
            public CompletableFuture<LLMResponse> generate(String question) {
                return CompletableFuture.failedFuture(
                        new RuntimeException("mock failure"));
            }
        };
        ResponseProcessor processor = new ResponseProcessor(1000, Runnable::run);

        LLMResponse result = processor.process(strategy, "question", response -> { })
                .get(2, TimeUnit.SECONDS);

        assertEquals(ResponseStatus.FAILED, result.getStatus());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("mock failure"));
    }

    @Test
    void slowResponseResultsInTimeout() throws Exception {
        LLMStrategy strategy = new MockLLMStrategy("Slow", "late", 1000);
        ResponseProcessor processor = new ResponseProcessor(100, Runnable::run);

        LLMResponse result = processor.process(strategy, "question", response -> { })
                .get(2, TimeUnit.SECONDS);

        assertEquals(ResponseStatus.TIMEOUT, result.getStatus());
    }

    @Test
    void emptyResponseResultsInInvalidResponse() throws Exception {
        LLMStrategy strategy = new LLMStrategy() {
            @Override
            public String getDisplayName() {
                return "Empty";
            }

            @Override
            public CompletableFuture<LLMResponse> generate(String question) {
                return CompletableFuture.completedFuture(
                        new LLMResponse("Empty", "   ", ResponseStatus.SUCCESS, 0L, null));
            }
        };
        ResponseProcessor processor = new ResponseProcessor(1000, Runnable::run);

        LLMResponse result = processor.process(strategy, "question", response -> { })
                .get(2, TimeUnit.SECONDS);

        assertEquals(ResponseStatus.INVALID_RESPONSE, result.getStatus());
    }
}
