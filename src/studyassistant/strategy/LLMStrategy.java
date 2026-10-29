package studyassistant.strategy;

import java.util.concurrent.CompletableFuture;
import studyassistant.model.LLMResponse;

public interface LLMStrategy {   

    String getDisplayName();    

    CompletableFuture<LLMResponse> generate(String question);

}
