package studyassistant.strategy;

import java.util.concurrent.CompletableFuture;

import studyassistant.client.OllamaClient;
import studyassistant.model.LLMResponse;

public class LlamaStrategy implements LLMStrategy{
	
	private OllamaClient client;
	
	public LlamaStrategy(OllamaClient client)
	{
		this.client = client;
	}
	
	public String getDisplayName()
	{
		return "Llama";
	}

    public CompletableFuture<LLMResponse> generate(String question)
    {
    	
    }
}
