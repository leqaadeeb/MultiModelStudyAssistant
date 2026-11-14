package studyassistant.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Sends asynchronous, non-streaming chat requests to the local Ollama server.
 * Strategies select the model; this class handles the shared HTTP and JSON work.
 */
public class OllamaClient {
    private static final URI ENDPOINT = URI.create("http://localhost:11434/api/chat");
    private static final Duration TIMEOUT = Duration.ofSeconds(60);

    private final HttpClient client = HttpClient.newHttpClient();
    private final URI endpoint;

    public OllamaClient() {
        this(ENDPOINT);
    }

    // Allows HTTP tests to use a local fake server without contacting Ollama.
    OllamaClient(URI endpoint) {
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint");
    }

    /**
     * Sends the question as a user message and returns the assistant's content.
     * HTTP, connection, and parsing failures complete the future exceptionally
     * so the controller can convert them into response states.
     *
     * @param modelName the installed Ollama model identifier
     * @param prompt the user's question
     * @return a future containing the generated response text
     */
    public CompletableFuture<String> generate(String modelName, String prompt) {
        Objects.requireNonNull(modelName, "modelName");
        Objects.requireNonNull(prompt, "prompt");

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);
        JsonArray messages = new JsonArray();
        messages.add(message);

        JsonObject body = new JsonObject();
        body.addProperty("model", modelName);
        body.add("messages", messages);
        body.addProperty("stream", false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(endpoint)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .timeout(TIMEOUT)
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        throw new IllegalStateException("Ollama HTTP error "
                                + response.statusCode() + ": " + response.body());
                    }
                    return getResponseText(response.body());
                });
    }

    private static String getResponseText(String json) {
        JsonElement reply = JsonParser.parseString(json);
        JsonElement message = reply.isJsonObject()
                ? reply.getAsJsonObject().get("message") : null;
        JsonElement content = message != null && message.isJsonObject()
                ? message.getAsJsonObject().get("content") : null;

        if (content == null || !content.isJsonPrimitive()
                || !content.getAsJsonPrimitive().isString()) {
            throw new IllegalStateException("Missing or invalid message.content in Ollama reply");
        }

        // Empty strings are returned for ResponseProcessor to mark INVALID_RESPONSE.
        return content.getAsString();
    }
}
