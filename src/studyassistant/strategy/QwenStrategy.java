package studyassistant.strategy;

import java.util.concurrent.CompletableFuture;

import studyassistant.client.OllamaClient;
import studyassistant.model.LLMResponse;

public class QwenStrategy implements LLMStrategy{

	private OllamaClient client;
	
	public QwenStrategy(OllamaClient client)
	{
		this.client = client;
	}
	
	public String getDisplayName()
	{
		return "Qwen";
	}

    public CompletableFuture<LLMResponse> generate(String question)
    {
    	
    }
}
