package studyassistant.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

/** Checks the HTTP chat contract without requiring Ollama or downloaded models. */
class OllamaClientTest {
    private HttpServer server;
    private final AtomicReference<String> requestBody = new AtomicReference<>();
    private final AtomicReference<String> requestMethod = new AtomicReference<>();
    private final AtomicReference<String> contentType = new AtomicReference<>();

    @AfterEach
    void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void sendsUserMessageAndReadsNestedChatContent() throws Exception {
        String prompt = "Explain \"interfaces\".\nUse C:\\notes and a tab:\t";
        String expected = "An \"interface\" defines a contract.\nPath: C:\\notes; symbol: \u03bb";
        OllamaClient client = startServer(200, """
                {
                  "model": "llama3.2:1b",
                  "content": "ignore this top-level field",
                  "message": {
                    "role": "assistant",
                    "content": "An \\"interface\\" defines a contract.\\nPath: C:\\\\notes; symbol: \\u03bb"
                  },
                  "done": true
                }
                """);

        assertEquals(expected, client.generate("llama3.2:1b", prompt).get(5, TimeUnit.SECONDS));
        assertEquals("POST", requestMethod.get());
        assertEquals("application/json", contentType.get());
        JsonObject body = JsonParser.parseString(requestBody.get()).getAsJsonObject();
        assertEquals("llama3.2:1b", body.get("model").getAsString());
        assertFalse(body.get("stream").getAsBoolean());
        assertFalse(body.has("prompt"));
        assertEquals(1, body.getAsJsonArray("messages").size());
        JsonObject message = body.getAsJsonArray("messages").get(0).getAsJsonObject();
        assertEquals("user", message.get("role").getAsString());
        assertEquals(prompt, message.get("content").getAsString());
    }

    @Test
    void httpFailurePreservesServerExplanation() throws Exception {
        OllamaClient client = startServer(404, "{\"error\":\"model not found\"}");

        ExecutionException error = assertThrows(ExecutionException.class,
                () -> client.generate("missing", "question").get(5, TimeUnit.SECONDS));

        assertTrue(error.getCause().getMessage().contains("404"));
        assertTrue(error.getCause().getMessage().contains("model not found"));
    }

    @Test
    void missingChatContentFailsInsteadOfReadingLegacyResponse() throws Exception {
        OllamaClient client = startServer(200, "{\"response\":\"legacy text\",\"message\":{\"role\":\"assistant\"}}");

        ExecutionException error = assertThrows(ExecutionException.class,
                () -> client.generate("llama3.2:1b", "question").get(5, TimeUnit.SECONDS));

        assertTrue(error.getCause().getMessage().contains("message.content"));
    }

    @Test
    void malformedJsonCompletesExceptionally() throws Exception {
        OllamaClient client = startServer(200, "{\"message\":");

        assertThrows(ExecutionException.class,
                () -> client.generate("llama3.2:1b", "question").get(5, TimeUnit.SECONDS));
    }

    @Test
    void emptyChatContentIsReturnedForControllerValidation() throws Exception {
        OllamaClient client = startServer(200, "{\"message\":{\"content\":\"\"},\"done\":true}");

        assertEquals("", client.generate("llama3.2:1b", "question").get(5, TimeUnit.SECONDS));
    }

    private OllamaClient startServer(int status, String reply) throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/chat", exchange -> {
            requestMethod.set(exchange.getRequestMethod());
            contentType.set(exchange.getRequestHeaders().getFirst("Content-Type"));
            requestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] bytes = reply.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, bytes.length);
            try (var output = exchange.getResponseBody()) {
                output.write(bytes);
            }
            exchange.close();
        });
        server.start();
        return new OllamaClient(URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/api/chat"));
    }
}
