package studyassistant.strategy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import studyassistant.model.LLMResponse;
import studyassistant.model.ResponseStatus;

/**
 * Simulates an AI response for testing
 */
public class MockLLMStrategy implements LLMStrategy {
	private final String modelName;
	private final String answer;
	private final long delayMs;

	public MockLLMStrategy(String modelName, String answer, long delayMs) {
		if(modelName == null || modelName.trim().isEmpty()){
			throw new IllegalArgumentException("Model name cannot be blank");
		}
		if (answer == null || answer.trim().isEmpty()){
			throw new IllegalArgumentException("Answer cannot be blank");
		}
		if (delayMs < 0){
			throw new IllegalArgumentException("Delay cannot be negative");
		}


		this.modelName = modelName;
		this.answer = answer;
		this.delayMs = delayMs;
	}

	@Override
	public String getDisplayName() {
		return modelName;
	}

	@Override
	public CompletableFuture<LLMResponse> generate(String question){
		long startTime = System.nanoTime();

		return CompletableFuture.supplyAsync(()->{
			long elapsedMilliseconds = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);


		return new LLMResponse(modelName, answer, ResponseStatus.SUCCESS, elapsedMilliseconds, null);

		}, CompletableFuture.delayedExecutor(delayMs, TimeUnit.MILLISECONDS));
	}
}
