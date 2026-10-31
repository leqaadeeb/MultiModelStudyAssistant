package studyassistant.strategy;

import java.util.concurrent.CompletableFuture;

import studyassistant.client.OllamaClient;
import studyassistant.model.LLMResponse;
import studyassistant.model.ResponseStatus;

/**
 * Concrete strategy class for Qwen language model.
 * Uses the shared OllamaClient to send questions to the
 * Qwen3 1.7b model and return the generated response.
 */
public class QwenStrategy implements LLMStrategy{

	private final OllamaClient client;
	
	/**
	 * Constructs a QwenStrategy with the given OllamaClient.
	 * 
	 * @param client the shared client used to communicate with Ollama
	 */
	public QwenStrategy(OllamaClient client)
	{
		this.client = client;
	}
	
	/**
	 * Returns the display name for this language model.
	 * 
	 * @return the model name "Qwen"
	 */
	public String getDisplayName()
	{
		return "Qwen";
	}

	/**
	 * Sends the question to the Qwen3 1.7b model asynchronously.
	 * Converts the generated text into an LLMResponse and returns it through CompletableFuture.
	 * 
	 * @param question the user's question to send to the model
	 * @return a future containing the generated LLMResponse
	 */
    public CompletableFuture<LLMResponse> generate(String question)
    {
    	long startTime = System.nanoTime();

    	return client.generate("qwen3:1.7b", question).thenApply(text -> 
    	{
    		long responseTimeMs = (System.nanoTime() - startTime) / 1000000;
    		return new LLMResponse(getDisplayName(), text, ResponseStatus.SUCCESS, responseTimeMs, null);
    	});
    }
}
