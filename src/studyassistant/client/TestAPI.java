package studyassistant.client;

import java.util.concurrent.TimeUnit;

/**
 * Test utility for verifying the Ollama API call through the application.
 *
 * How to test:
 *
 * 1. Ensure Ollama is installed and running locally on http://localhost:11434,
 *    using {@code `ollama serve`} or {@code `brew services start ollama`} in CLI.
 * 2. Pull the required model with {@code `ollama pull llama3.2:1b`}, or update the
 *    model name in this test to match an installed model.
 * 3. Compile and copy runtime dependencies from the project root:
 *
 *        mvn compile dependency:copy-dependencies -DincludeScope=runtime
 *
 * 4. Run the test:
 *
 *        java -cp "target/classes;target/dependency/*" studyassistant.client.TestAPI
 *
 *    On macOS/Linux, use ':' instead of ';' in the classpath.
 *
 */
public class TestAPI {

    // Prompt includes a quote, a backslash, and a newline so that JSON
    // escaping is exercised in both directions.
    private static final String PROMPT =
            "Say hello and name the model you are.\n"
            + "Then repeat this exactly on a new line: "
            + "\"a \\ b\" and \"quoted text\"";

    public static void main(String[] args) throws Exception {
        OllamaClient client = new OllamaClient();

        String response = client
                .generate("llama3.2:1b", PROMPT)
                .get(60, TimeUnit.SECONDS);

        System.out.println(response);
    }
}
