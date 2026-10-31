package studyassistant.strategy;

import java.util.concurrent.CompletableFuture;
import studyassistant.model.LLMResponse;


/**
 * Contract for requesting an answer from an AI model.
 */
public interface LLMStrategy {

	/**
	 * Returns the model identifier used to match QuerySession responses.
	 */
	String getDisplayName();

	/**
	 * Starts a request for the question
	 */
	CompletableFuture<LLMResponse> generate(String question);
}