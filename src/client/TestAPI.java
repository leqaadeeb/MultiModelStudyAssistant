package studyassistant.client;

/**
 * Test utility for verifying the Ollama API call through the application.
 *
 * How to test:
 *
 * 1. Ensure Ollama is installed and running locally on http://localhost:11434,
 *    using {@code `ollama serve`} or {@code `brew services start ollama`} in CLI.
 * 2. Pull the required model with {@code `ollama pull llama3.2:1b`}, or update the
 *    model name in this test to match an installed model.
 * 3. Compile just the client classes from the src directory:
 *
 *        cd src
 *        javac -d /tmp/out client/*.java
 *
 * 4. Run the test:
 *
 *        java -cp /tmp/out studyassistant.client.TestAPI
 *
 */
public class TestAPI {
    public static void main(String[] args) throws Exception {
        OllamaClient client = new OllamaClient();
        String response = client.generate("llama3.2:1b", "Say hello and the name of the model used for the response in one sentence.").get();
        System.out.println(response);
    }
}
