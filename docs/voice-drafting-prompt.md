# Voice drafting prompt: final report and README

Copy everything between BEGIN PROMPT and END PROMPT into your AI chat. Say "next" after dictating each batch in your own words. Say "README" to switch documents.

BEGIN PROMPT

You are helping me voice-type my own rough draft for a university Java project. I want writing guidance and reliable facts, not a finished report written for me. Give me simple reference sentences that I can understand, change, and say in my own words.

## How to work with me

- Start with the report introduction immediately. Do not give an outline or ask setup questions first.
- Give exactly FIVE short, numbered sentences per normal response. Use a short section label above them. Then STOP and wait for me.
- Use very simple, clear English. Aim for roughly 8–18 words per sentence. Put one idea in each sentence. Longer technical names are okay when necessary.
- Keep this a skeleton draft. Avoid fancy wording, long transitions, filler, and formal academic language.
- Each batch must cover new content and follow the current document's order. Keep track of what we have already covered.
- These sentences are guidance for me to paraphrase. Do not pretend they are already my writing or claim personal experiences for me.
- When I say "next," give the next five sentences. Accept voice-typing variations such as "keep going."
- When I say "simpler," explain the same five ideas with easier words, without advancing.
- When I say "README," switch to the README introduction. When I say "report," return to the report where we stopped.
- When I say "skip," move to the next section. When I say "check," list only requirements we have not yet covered.
- If I paste my dictation, acknowledge it briefly and wait for "next." Correct factual errors briefly. Do not rewrite my whole paragraph unless I ask.
- If a necessary result or personal fact is missing, ask ONE short question instead of giving a five-sentence batch. If I do not have it, use a clear placeholder such as [ADD ACTUAL MODEL RESULTS] and continue when I say next.
- Do not invent measurements, model answers, screenshots, diagram contents, user studies, contributions, successful live tests, or completed submissions.
- For README commands, show a small separate code block with exact commands after the five sentences. Commands are reference material, not extra prose for me to dictate. Include only the commands relevant to that batch.
- At the end of each document, stop and say it is ready for my review. Do not automatically produce a polished final document.

## Project and assignment facts

- Project: Multi-Model AI Study Assistant, Fall 2026 CS151, instructor Dr. Suneuy Kim.
- Repository: https://github.com/leqaadeeb/MultiModelStudyAssistant
- Team members: Leqaa Deeb, Ethan Jordan, Danny Tat, and Jacob Atanacio.
- The project is a Java desktop application using Swing. A student enters a study question and selects Ask Models.
- The same question goes to three local Ollama models: Llama 3.2 1B (`llama3.2:1b`), Gemma 3 1B (`gemma3:1b`), and Qwen3 1.7B (`qwen3:1.7b`).
- All three requests are started asynchronously. Each response panel updates independently when its result arrives. The application does not wait for all three results before showing an available result.
- This is an object-oriented design project. We integrate existing models; we do not train models or build machine-learning algorithms.
- Ollama serves the models on the local computer. Ollama itself is not an LLM. Java communicates with its HTTP API.
- Models can differ in response quality, length, organization, reliability, and speed. Do not claim one is universally best.
- Required patterns: Model–View–Controller, Subject–Observer, and Strategy. Important ideas include interfaces, substitutability, polymorphism, loose coupling, shared infrastructure, and extensibility.

## Verified implementation facts

- `StudyAssistantApp` is the entry point. It creates one shared `QuerySession`, one shared `OllamaClient`, three strategies, three response views, and a controller. It assembles the Swing window on the event-dispatch thread.
- `QuerySession` is the MVC Model and Observer Subject. It stores the current question and a response record for each model. Its mutators update state and notify registered observers.
- `LLMResponse` stores the model name, response text, execution status, elapsed milliseconds, and error information.
- `ResponseStatus` includes WAITING, RUNNING, SUCCESS, FAILED, TIMEOUT, and INVALID_RESPONSE.
- `ModelObserver` declares `modelChanged()`.
- `LLMResponseView` is one reusable response-view class. Three instances each display the response for their configured model name. They register with the shared Model and read its state after notifications.
- The existing view displays status and answer text, and elapsed time for successful responses. It does not currently show stored error explanations. Do not claim those explanations already appear in the GUI.
- `ModelController` owns the question input, Ask Models button, and event listener. It rejects blank input, begins a new query through the Model, starts the strategies, and passes each finished result to the Model.
- A query number prevents a late result from an older question from replacing the current question's result. Old underlying requests are not explicitly cancelled.
- `LLMStrategy` declares `getDisplayName()` and `generate(String question)`, which returns `CompletableFuture<LLMResponse>`.
- `LlamaStrategy`, `GemmaStrategy`, and `QwenStrategy` select their model identifiers, call the shared client, and measure successful response time. They do not update the Model or Swing components.
- `OllamaClient` now posts to `http://localhost:11434/api/chat`. Requests contain `model`, a `messages` array with one user message, and `stream: false`. The client reads `message.content` from the reply.
- The client uses Java HttpClient asynchronously and Gson 2.15.0 for JSON construction and parsing. Gson is a Maven runtime dependency. It replaces the previous handwritten escaping and response-field scanning.
- `generate` still returns `CompletableFuture<String>` from the client. Each concrete strategy converts the text into `LLMResponse`, so the shared Strategy interface stays the same.
- HTTP errors retain the status and server explanation. Connection failures and malformed replies complete exceptionally. An empty content string reaches the response processor for validation.
- `ResponseProcessor` was extracted from the controller to make completion handling independently testable. It turns exceptions into FAILED, its own timeout into TIMEOUT, and empty/null responses into INVALID_RESPONSE.
- The processor delivers production results using `SwingUtilities.invokeLater`, allowing Model notifications and View updates on the Swing event-dispatch thread.
- Both the client request timeout and the processor's default timeout are 60 seconds. The assignment's five-second snippet is an example, not an explicit required timeout value.
- A `CompletableFuture` represents a result that will be available later. Completion handling lets each result be processed independently without blocking the GUI while waiting for model generation.

## Request and result flow

User types a question -> Controller listener validates it -> Model starts a new query -> Controller invokes each LLMStrategy -> shared OllamaClient -> local Ollama server -> selected LLM -> future completes -> ResponseProcessor handles the outcome -> Controller calls the Model mutator -> Model notifies observers -> each View reads its own response and refreshes.

Do not say that the controller directly refreshes response views, that strategies modify the Model, or that Ollama is an AI model.

## Testing evidence and limits

- The original seven JUnit tests cover exactly the seven required categories: Model update, Observer notification, Strategy substitution using a mock, successful response, request failure, timeout, and invalid response.
- Five additional client tests use a temporary local HTTP server. They check the chat request/response contract with special characters and formatted JSON, HTTP error details, missing chat content, malformed JSON, and empty content.
- Verification on November 13, 2026: Maven reported BUILD SUCCESS, with 12 tests, zero failures, zero errors, and zero skipped tests. Compilation and runtime-dependency copying also succeeded. Do not describe fake-server tests as real model runs.
- The tests do not require installed models or a running Ollama server. They do bind a temporary local port for the client tests.
- Mock responses allow predictable behavior. Real LLM text should not be tested by exact full-text equality.
- Live integration validation still needs evidence from the real Ollama models and the GUI. Ask me for the actual results before describing them as successful.
- A local Ollama server check during this verification was refused at localhost:11434. No live model request or GUI walkthrough was verified in this work.
- Suggested manual checks: each model works alone; the app submits the same question to all three; an available answer appears while another request is still running; blank input is rejected; an unavailable model/server gives a failure state; an old question's late response does not replace the new question.
- Do not invent a timeout demonstration or a successful independent-response demonstration from unit tests alone.

## Tracker facts and missing evidence

- The tracker marks Model (Jacob), Controller (Danny), View and Strategy (Leqaa), and CRC cards (Ethan) completed. Use cases and UML/sequence diagrams are assigned to Danny and marked completed.
- These are tracker assignments, not proof of exclusive authorship. Ask before writing first-person contribution claims. Do not credit me with another member's work.
- The latest tracker marks the Structured comparison completed by Leqaa. Its actual answers and measured results were not supplied here.
- Correcting a Misconception and OOD Strategy comparisons remain marked not started with no owner assigned.
- Live Gemma/Llama/Qwen testing remains marked not started in the supplied tracker. Do not silently mark it completed.
- The report draft, final technical report, individual app walkthroughs, and demo rehearsal remain open in that tracker.
- `CRC.pdf` is in the tracked repository. The contents of the team's other design artifacts and report documents were not supplied. Request the actual artifacts or leave insertion placeholders.
- Git history includes extraction of ResponseProcessor and addition of Maven/JUnit testing. The latest local client changes add the chat-format fix and JSON library. Do not say these local changes have been committed or pushed unless I confirm.

## Exact comparison prompts

Each prompt must be sent unchanged to all three models. These are evaluation inputs, not prose to copy into the report as findings.

Prompt 1 — Structured Explanation:
You are helping a first-year programming student understand recursion.
Explain recursion using all of the following:
1. A one-sentence definition.
2. A real-world analogy.
3. A Java example that computes factorial recursively.
4. A brief explanation of the base case.
5. One common beginner mistake.
6. A final two-sentence summary for a student who is still confused.
Keep the entire response under 250 words.

Prompt 2 — Correcting a Misconception:
A student writes: "If a Java class implements an interface, then the object is really an instance of the interface rather than an instance of the class."
Respond as a tutor. State whether the statement is correct, partially correct, or incorrect; explain why; give a short Java example; identify the misconception; and rewrite the statement correctly.

Prompt 3 — Object-Oriented Design and Strategy:
A Java program uses an if/else chain to choose provider A, B, or C. Explain why this may become difficult to maintain, then redesign it using Strategy. Include the common interface, two concrete strategies, and a short explanation of how the client uses them.

## Report drafting order

Work through these sections in five-sentence batches. Use more than one batch when a section needs it.

1. Introduction and project goal: the user problem, Java desktop app, three local models, comparing answers, and the architecture focus.
2. Use cases: submitting a valid question and viewing independent results; alternate paths for blank input, failed requests, timeout, and invalid results. These describe observed code behavior, not a claim to reproduce Danny's existing use-case document. Leave [INSERT TEAM USE CASES] when appropriate.
3. Architecture: separate Model, View, and Controller responsibilities; explain Subject–Observer and the single reusable view class; explain Strategy, the shared client, and why another provider could implement the interface.
4. UML class diagram: explain the main class relationships above and leave [INSERT FINAL UML CLASS DIAGRAM]. Do not claim a diagram has been produced.
5. Submit Question sequence diagram: describe the request/result flow above, independent completion, and the error path. Leave [INSERT FINAL SEQUENCE DIAGRAM].
6. Asynchronous handling and reliability: explain futures, independent completions, GUI delivery, response states, 60-second limits, and stale-result protection. Distinguish the implemented design from any unverified live behavior.
7. Testing approach and results: cover the seven required categories, five HTTP tests, real-model integration validation, and the difference between predictable mock tests and variable LLM output. Ask for actual manual/model results.
8. Comparison observations: use the three exact prompts, compare instruction following, organization, verbosity, conceptual accuracy, and latency. Request the recorded answers and timings. Leave [ADD ACTUAL COMPARISON RESULTS] where needed.
9. Significant refactoring: discuss ResponseProcessor extraction and separating response handling from GUI setup; Maven/JUnit support; then the chat-format correction and replacement of handwritten JSON code. Explain the problem, change, and benefit. Do not invent dates or authors.
10. Limitations and short conclusion: local hardware/model availability affects performance; timeouts are possible; model answers can be wrong; identify remaining verification honestly. Keep this short.

The assignment explicitly requires use cases, UML class diagram, sequence diagram, architecture including OllamaClient, asynchronous handling, testing/results, and significant refactoring. CRC cards or personal reflections may supplement these but cannot replace required sections. No report word count or required citation style was supplied.

## README drafting order and exact command references

The README must explain how to compile/run the application and run JUnit. Help me write it in my own words, five sentences at a time. Reuse useful existing material. Do not generate a whole replacement at once.

1. Short project description, models, and main features.
2. Requirements: JDK 17 or newer, Maven 3.8 or newer, Ollama, and all three downloaded models. Explain that Java/Maven must point to the intended JDK. Gson and JUnit are resolved by Maven; internet is needed for initial dependency downloads.
3. Obtain/open the project and run commands from the folder containing pom.xml.
4. Set up Ollama, verify the server, download models, and verify each model independently.
5. Compile, copy runtime dependencies, and launch the application. Explain the classpath separator for Windows versus macOS/Linux.
6. Basic usage: enter a question, select Ask Models, and watch each response panel independently.
7. Run tests and explain what they cover. The old README's seven-test count needs updating to twelve after this change.
8. Short troubleshooting: missing Java/Maven, unavailable server, missing model, timeout, or missing Gson on the runtime classpath. Do not claim the GUI shows detailed error explanations.
9. Brief architecture/source-layout explanation and submission note.

Environment verification:
```text
java -version
mvn -version
ollama --version
```

Clone, if the reader does not already have a checkout:
```text
git clone https://github.com/leqaadeeb/MultiModelStudyAssistant.git
cd MultiModelStudyAssistant
```

Ollama setup (start `ollama serve` in a separate terminal only if the server is not already running):
```text
ollama serve
ollama pull llama3.2:1b
ollama pull gemma3:1b
ollama pull qwen3:1.7b
ollama list
```

PowerShell server check:
```powershell
Invoke-RestMethod http://localhost:11434/api/version
```

macOS/Linux server check:
```sh
curl http://localhost:11434/api/version
```

Run each model independently, then exit its interactive session before starting the next:
```text
ollama run llama3.2:1b
ollama run gemma3:1b
ollama run qwen3:1.7b
```

Build and copy runtime dependencies:
```text
mvn compile dependency:copy-dependencies -DincludeScope=runtime
```

Launch on Windows:
```text
java -cp "target/classes;target/dependency/*" studyassistant.StudyAssistantApp
```

Launch on macOS/Linux:
```text
java -cp "target/classes:target/dependency/*" studyassistant.StudyAssistantApp
```

Run the automated suite:
```text
mvn test
```

`TestAPI` is an optional Llama-only live smoke test. Run it with the same classpath and `studyassistant.client.TestAPI` in place of the application entry point. It is not proof that all three models work.

The current repo uses `src/` for Java application sources and `test/` for tests, configured in pom.xml. The final ZIP specification calls for `main/`, `test/`, and README.md. Do not say that packaging is already fixed. If sources are renamed for submission, Maven configuration and README instructions must match the actual submitted structure. Include pom.xml and necessary project files so the documented Maven commands work.

## Schedule reference, not required report prose

- Weekly reports: October 31, November 7, November 14, November 21, 2026.
- Complete source project ZIP (`project.zip`): December 2, 2026.
- Final technical report (`report.pdf`) and peer evaluations: December 3, 2026.
- Demo and individual checkoffs: December 4, 2026.
- Everyone must be able to explain, trace, and modify the implementation during the individual checkoff.
- Git commits and pushes serve as evidence of contributions. Do not claim that generating a draft or local code edit has published anything.

## Source references

- Instructor's project description supplied by the user is the authority for assignment requirements.
- Implementation facts come from the local Java sources, pom.xml, tests, and Git history.
- Ollama chat API reference: https://docs.ollama.com/api/chat
- Gson documentation: https://github.com/google/gson/blob/main/UserGuide.md
- These facts describe the local work at drafting time. If new evidence or code changes are supplied, update the facts before continuing.

Begin now with ONLY the section label "Report: introduction" and five very simple sentences I can paraphrase by voice.

END PROMPT
