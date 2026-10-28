
/**
 * The Model should contain the current question and one response record per LLM. Each response record should represent the model name, response text, execution status, elapsed time, error information, and any other metadata needed by the interface.
 */
public class LLMResponse {
	private String modelName;
	private String text;
	private ResponseStatus status;
	private long responseTimeMs;
	private String errorMessage;
}
