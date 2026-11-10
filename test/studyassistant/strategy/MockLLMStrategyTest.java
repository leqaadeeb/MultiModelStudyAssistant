package studyassistant.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import studyassistant.model.LLMResponse;
import studyassistant.model.ResponseStatus;

/**
 * Tests that MockLLMStrategy can be used through the LLMStrategy interface.
 */
class MockLLMStrategyTest {

    @Test
    void mockIsUsableThroughStrategyInterface() throws Exception {
        LLMStrategy strategy = new MockLLMStrategy("Mock", "answer", 0);

        assertEquals("Mock", strategy.getDisplayName());

        LLMResponse response = strategy.generate("question")
                .get(2, TimeUnit.SECONDS);

        assertEquals("answer", response.getText());
        assertEquals(ResponseStatus.SUCCESS, response.getStatus());
    }
}
