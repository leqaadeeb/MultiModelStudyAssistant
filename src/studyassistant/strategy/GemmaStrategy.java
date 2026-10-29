package studyassistant.strategy;

import java.util.concurrent.CompletableFuture;

import studyassistant.client.OllamaClient;
import studyassistant.model.LLMResponse;

public class GemmaStrategy implements LLMStrategy{
	
	private OllamaClient client;
	
	public GemmaStrategy(OllamaClient client)
	{
		this.client = client;
	}
	
	public String getDisplayName()
	{
		return "Gemma";
	}

	/*
    public CompletableFuture<LLMResponse> generate(String question)
    {
    	return client.generate("llama3.2:1b", question).thenApply(text -> 
    			new LLMResponse("Llama", text)
    	);
    }
    */
}
