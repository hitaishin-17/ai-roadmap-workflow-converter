package com.roadmapconverter.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.roadmapconverter.model.RoadmapItem;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AIMapperService {

    @Value("${openai.api-key}")
    private String openaiApiKey;

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(60)) // Allow more time for Ollama
            .build();

    public List<String> mapToWorkflowTasks(List<RoadmapItem> items) {
        return items.stream()
                .map(item -> item.getTitle() + " - " + item.getDescription())
                .collect(Collectors.toList());
    }

    public String mapTextToSteps(String roadmapText) {
        String prompt = "Convert this roadmap into a numbered list of short steps in this exact format:\n\n" +
                "1. Step Title: Short description\n" +
                "2. Step Title: Another description\n\n" +
                "⚠️ Do NOT return explanations or extra text — only the numbered list in this exact format.\n\n" +
                roadmapText;

        Gson gson = new GsonBuilder().create();

        Map<String, Object> payload = Map.of(
                "model", "mistral",
                "prompt", prompt,
                "stream", false
        );

        String requestBody = gson.toJson(payload);

        Request request = new Request.Builder()
                .url("http://localhost:11434/api/generate")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = Objects.requireNonNull(response.body()).string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

            String content = json.get("response").getAsString();

            System.out.println("📦 Raw AI response:\n" + content);

            List<RoadmapItem> steps = new ArrayList<>();
            String[] lines = content.split("\\n");

            for (String line : lines) {
                line = line.trim();

                if (line.isEmpty()) continue;

                // Remove "1.", "2." etc.
                line = line.replaceAll("^\\d+\\.\\s*", "");

                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    String title = parts[0].trim();
                    String desc = parts[1].trim();
                    steps.add(new RoadmapItem(title, desc));
                } else {
                    steps.add(new RoadmapItem(line, "")); // title only
                }
            }

            if (steps.isEmpty()) {
                throw new IllegalStateException("❌ No steps parsed from AI response.");
            }

            System.out.println("✅ Parsed steps:");
            for (RoadmapItem item : steps) {
                System.out.println(item.getTitle() + " → " + item.getDescription());
            }

            BPMNGeneratorService bpmnService = new BPMNGeneratorService();
            return bpmnService.generateBPMN(steps); // return XML

        } catch (IOException e) {
            e.printStackTrace();
            return "<!-- Error generating BPMN -->";
        }
    }
}