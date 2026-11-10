package studyassistant.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * Tests response storage and observer notification in QuerySession.
 */
class QuerySessionTest {

    @Test
    void updateResponseStoresExpectedResponse() {
        QuerySession session = new QuerySession();
        LLMResponse expected = new LLMResponse(
                "Mock", "answer", ResponseStatus.SUCCESS, 12L, null);

        session.updateResponse("Mock", expected);

        assertSame(expected, session.getResponse("Mock"));
    }

    @Test
    void observerIsNotifiedAfterModelStateChanges() {
        QuerySession session = new QuerySession();
        AtomicInteger notifications = new AtomicInteger();
        session.addObserver(() -> notifications.incrementAndGet());

        session.updateResponse(
                "Mock",
                new LLMResponse("Mock", "answer", ResponseStatus.SUCCESS, 5L, null));

        assertEquals(1, notifications.get());
    }
}
