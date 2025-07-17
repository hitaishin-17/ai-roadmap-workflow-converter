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
        String prompt = "Convert this roadmap into a numbered list of short BPMN-ready steps in this format:\n\n"
                + "1. Title: Short title\n"
                + "2. Title: Next task title\n\n"
                + "Do NOT return explanations, only the numbered list.\n\n"
                + roadmapText;

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
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    String title = parts[0].replaceAll("^\\d+\\.\\s*", "").trim(); // remove "1. "
                    String desc = parts[1].trim();
                    steps.add(new RoadmapItem(title, desc));
                }
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