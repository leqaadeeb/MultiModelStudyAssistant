package studyassistant.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Sends prompts to the local Ollama server
 * and returns the generated response text.
 */
public class OllamaClient {

    // Address of the local Ollama generate endpoint.
    private static final String ENDPOINT =
            "http://localhost:11434/api/generate";

    // Maximum time allowed for a single request.
    private static final Duration TIMEOUT =
            Duration.ofSeconds(60);

    // Create one HTTP client for sending requests to Ollama.
    private final HttpClient client =
            HttpClient.newHttpClient();

    /**
     * Sends a prompt to a specified Ollama model.
     *
     * @param modelName the Ollama model to query
     * @param prompt the user prompt, which may contain any characters
     * @return a future holding the generated response text
     */
    public CompletableFuture<String> generate(
            String modelName,
            String prompt) {

        // Build the JSON request body, escaping user text so that
        // quotes, backslashes, and newlines stay valid JSON.
        String json =
                "{\"model\":\"" + escapeJson(modelName)
                + "\",\"prompt\":\"" + escapeJson(prompt)
                + "\",\"stream\":false}";

        // Build the HTTP request.
        HttpRequest request =
                HttpRequest.newBuilder()

                        // Specify the Ollama API address.
                        .uri(URI.create(ENDPOINT))

                        // Tell Ollama that the request body contains JSON.
                        .header("Content-Type", "application/json")

                        // Send the JSON data using HTTP POST.
                        .POST(HttpRequest.BodyPublishers.ofString(json))

                        // Do not wait forever if Ollama stops responding.
                        .timeout(TIMEOUT)

                        // Finish building the request.
                        .build();

        // Send the HTTP request asynchronously.
        return client.sendAsync(
                request,
                HttpResponse.BodyHandlers.ofString())

                // Process the response when it becomes available.
                .thenApply(response -> {

                    // Check whether Ollama returned a successful response.
                    if (response.statusCode() != 200) {

                        // Report an error that includes Ollama's explanation.
                        throw new RuntimeException(
                                "Ollama HTTP error "
                                + response.statusCode()
                                + ": "
                                + response.body());
                    }

                    // Extract and return the generated text.
                    return getResponseText(response.body());
                });
    }

    /**
     * Converts a string into a JSON string literal body, escaping
     * quotation marks, backslashes, and control characters.
     */
    private static String escapeJson(String value) {
        StringBuilder escaped =
                new StringBuilder(value.length() + 16);

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            switch (c) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        escaped.append(
                                String.format("\\u%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
            }
        }

        return escaped.toString();
    }

    /**
     * Extracts the generated text from Ollama's JSON response,
     * unescaping quotes, backslashes, and newlines.
     */
    private String getResponseText(String json) {

        // Locate the beginning of the "response" value.
        String marker = "\"response\":\"";
        int start = json.indexOf(marker);

        if (start < 0) {
            throw new IllegalStateException(
                    "No \"response\" field in Ollama reply: " + json);
        }

        start += marker.length();

        StringBuilder text = new StringBuilder();

        // Read until an unescaped closing quotation mark.
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"') {
                return text.toString();
            }

            if (c != '\\') {
                text.append(c);
                continue;
            }

            // A backslash introduces an escape sequence.
            if (i + 1 >= json.length()) {
                break;
            }

            char next = json.charAt(++i);

            switch (next) {
                case 'n':
                    text.append('\n');
                    break;
                case 'r':
                    text.append('\r');
                    break;
                case 't':
                    text.append('\t');
                    break;
                case 'b':
                    text.append('\b');
                    break;
                case 'f':
                    text.append('\f');
                    break;
                case 'u':
                    if (i + 4 >= json.length()) {
                        throw new IllegalStateException(
                                "Truncated unicode escape in Ollama reply");
                    }
                    text.append((char) Integer.parseInt(
                            json.substring(i + 1, i + 5), 16));
                    i += 4;
                    break;
                default:
                    // Covers \" and \\ and any unknown escape.
                    text.append(next);
            }
        }

        throw new IllegalStateException(
                "Unterminated \"response\" value in Ollama reply: " + json);
    }
}
