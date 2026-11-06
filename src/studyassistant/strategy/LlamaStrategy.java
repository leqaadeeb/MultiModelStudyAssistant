package studyassistant.strategy;

import java.util.concurrent.CompletableFuture;
import studyassistant.client.OllamaClient;
import studyassistant.model.LLMResponse;
import studyassistant.model.ResponseStatus;

/**
 * Concrete strategy class for Llama language model.
 * Uses the shared OllamaClient to send questions to the
 * Llama 3.2 1B model and return the generated response.
 */
public class LlamaStrategy implements LLMStrategy{
	
	private final OllamaClient client;
	
	/**
	 * Constructs a LlamaStrategy with the given OllamaClient.
	 * 
	 * @param client the shared client used to communicate with Ollama
	 */
	public LlamaStrategy(OllamaClient client)
	{
		this.client = client;
	}
	
	/**
	 * Returns the display name for this language model.
	 * 
	 * @return the model name "Llama"
	 */
	public String getDisplayName()
	{
		return "Llama";
	}

	/**
	 * Sends the question to the Llama 3.2 1B model asynchronously.
	 * Converts the generated text into an LLMResponse and returns it through CompletableFuture.
	 * 
	 * @param question the user's question to send to the model
	 * @return a future containing the generated LLMResponse
	 */
    public CompletableFuture<LLMResponse> generate(String question)
    {
    	long startTime = System.nanoTime();
    	//thenApply will run when the client.generate("llama3.2:1b", question) completes successfully.
    	//The controller will handle the exceptional completions if an HTTP request failed or if it timed out.
    	return client.generate("llama3.2:1b", question).thenApply(text -> 
    	{
    		//in milliseconds
    		long responseTimeMs = (System.nanoTime() - startTime) / 1000000;
    		return new LLMResponse(getDisplayName(), text, ResponseStatus.SUCCESS, responseTimeMs, null);
    	});
    }
}
