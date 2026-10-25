# Multi-Model AI Study Assistant

A Java desktop application developed for the Fall 2026 CS151 team project.

## Overview

The Multi-Model AI Study Assistant lets users submit a study question and compare answers from three AI models running locally through Ollama:

- **Llama 3.2 1B** — `llama3.2:1b`
- **Gemma 3 1B** — `gemma3:1b`
- **Qwen3 1.7B** — `qwen3:1.7b`

The application sends the same question to all three models concurrently. Each answer appears in its own response panel as soon as it is available, without waiting for the other models to finish.

The project focuses on object-oriented design and integrating existing AI models rather than training models.

## Planned Features

- A graphical interface for entering study questions.
- Side-by-side answers from all three models.
- Independent response updates as each request completes.
- Response status and elapsed time for each model.
- Handling for failed requests, timeouts, and invalid responses.

## Architecture

The application follows three required design patterns:

- **Model–View–Controller (MVC):** Separates application state, response displays, and user-input handling.
- **Observer:** The shared Model notifies response Views when its state changes.
- **Strategy:** Each AI model has a Strategy implementing a common interface, with shared HTTP communication handled by the provided `OllamaClient`.

Requests are asynchronous so the application can process each model's response independently.

## Technologies

- Java
- Ollama
- JUnit
- Git and GitHub