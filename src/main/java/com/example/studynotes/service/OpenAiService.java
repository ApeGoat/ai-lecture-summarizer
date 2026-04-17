package com.example.studynotes.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class OpenAiService {

    private final HttpClient httpClient;
    private final String apiKey;
    private final String model;

    public OpenAiService(@Value("${openai.api.key:}") String apiKey,
                         @Value("${openai.model:gpt-4o-mini}") String model) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = apiKey;
        this.model = model;
    }

    public String generateStudyNotesMarkdown(String lectureText) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OpenAI API key is missing. Set OPENAI_API_KEY or openai.api.key.");
        }

        String prompt = buildPrompt(lectureText);
        String payload = """
                {
                  "model": "%s",
                  "messages": [
                    {
                      "role": "system",
                      "content": "You create study notes from lecture material. Preserve meaning and terminology from the source, focus on key concepts/definitions/mechanisms/processes/relationships, expand important themes for review clarity, include examples only when relevant, do not invent facts, and keep the result concise and structured."
                    },
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ],
                  "temperature": 0.2
                }
                """.formatted(escapeJson(model), escapeJson(prompt));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 300) {
                throw new IllegalStateException("OpenAI API request failed with status " + response.statusCode()
                        + ". Response: " + response.body());
            }
            return extractContent(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("OpenAI API call was interrupted.", e);
        } catch (IOException e) {
            throw new IllegalStateException("OpenAI API call failed.", e);
        }
    }

    private String buildPrompt(String lectureText) {
        return """
                Create study notes from the following lecture text.

                Output requirements (Markdown):
                - Title
                - Short overall summary
                - Clearly separated sections for each key theme
                - Bullet points where useful
                - Examples where applicable
                - Concise final review section

                Instructions:
                - Identify main themes and core ideas
                - Stay close to wording/formulation of the lecture notes
                - Expand each theme to make review easier
                - Avoid unnecessary outside information
                - Do not invent facts not grounded in the lecture text

                Lecture text:
                %s
                """.formatted(lectureText);
    }

    private String extractContent(String json) {
        String marker = "\"content\":";
        int markerIndex = json.indexOf(marker);
        if (markerIndex < 0) {
            throw new IllegalStateException("Could not parse model output from OpenAI response.");
        }

        int start = json.indexOf('"', markerIndex + marker.length());
        if (start < 0) {
            throw new IllegalStateException("Could not find content start in OpenAI response.");
        }

        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        for (int i = start + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                switch (c) {
                    case 'n' -> result.append('\n');
                    case 'r' -> result.append('\r');
                    case 't' -> result.append('\t');
                    case '"' -> result.append('"');
                    case '\\' -> result.append('\\');
                    default -> result.append(c);
                }
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '"') {
                return result.toString().trim();
            }
            result.append(c);
        }

        throw new IllegalStateException("Could not parse content from OpenAI response.");
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
