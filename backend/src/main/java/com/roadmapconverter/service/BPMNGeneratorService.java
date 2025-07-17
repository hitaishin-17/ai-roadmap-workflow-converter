package com.roadmapconverter.service;

import com.roadmapconverter.model.RoadmapItem;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BPMNGeneratorService {

    private static final String OUTPUT_PATH = "bpmn-output/sample-onboarding.bpmn";

    public String generateAndSaveBPMN(List<RoadmapItem> items) {
        String bpmnXml = generateBPMN(items);
        saveToFile(bpmnXml);
        return bpmnXml;
    }

    private void saveToFile(String bpmnXml) {
        try {
            File outputFile = new File(OUTPUT_PATH);
            outputFile.getParentFile().mkdirs(); // Ensure directory exists
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(bpmnXml);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save BPMN file: " + e.getMessage());
        }
    }

    public String generateBPMN(List<RoadmapItem> items) {
        StringBuilder bpmn = new StringBuilder();
        String processId = "Process_" + UUID.randomUUID().toString().substring(0, 8);

        bpmn.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n")
                .append("             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n")
                .append("             xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n")
                .append("             xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\"\n")
                .append("             xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\"\n")
                .append("             targetNamespace=\"http://bpmn.io/schema/bpmn\">\n");

        // Process Start
        bpmn.append("  <process id=\"").append(processId).append("\" isExecutable=\"true\">\n")
                .append("    <startEvent id=\"StartEvent_1\" name=\"Start\"/>\n");

        // Tasks and flows
        String previousId = "StartEvent_1";
        for (int i = 0; i < items.size(); i++) {
            String taskId = "Task_" + i;
            String flowId = "Flow_" + i;
            RoadmapItem item = items.get(i);

            bpmn.append("    <task id=\"").append(taskId).append("\" name=\"")
                    .append(escapeXml(item.getTitle())).append("\"/>\n");

            bpmn.append("    <sequenceFlow id=\"").append(flowId).append("\" sourceRef=\"")
                    .append(previousId).append("\" targetRef=\"").append(taskId).append("\"/>\n");

            previousId = taskId;
        }

        bpmn.append("    <endEvent id=\"EndEvent_1\" name=\"End\"/>\n")
                .append("    <sequenceFlow id=\"Flow_end\" sourceRef=\"").append(previousId)
                .append("\" targetRef=\"EndEvent_1\"/>\n")
                .append("  </process>\n");

        // BPMN Diagram
        bpmn.append("  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">\n")
                .append("    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"").append(processId).append("\">\n");

        int startX = 100;
        int y = 100;
        int step = 160;
        int taskWidth = 100;
        int taskHeight = 56;
        int centerY = y + taskHeight / 2;

        List<Integer> taskXs = new ArrayList<>();

        // Start shape
        bpmn.append("      <bpmndi:BPMNShape id=\"StartEvent_1_di\" bpmnElement=\"StartEvent_1\">\n")
                .append("        <omgdc:Bounds x=\"").append(startX).append("\" y=\"").append(y).append("\" width=\"36\" height=\"36\"/>\n")
                .append("      </bpmndi:BPMNShape>\n");

        // Task shapes
        int x = startX + step;
        for (int i = 0; i < items.size(); i++) {
            String taskId = "Task_" + i;
            taskXs.add(x);

            bpmn.append("      <bpmndi:BPMNShape id=\"").append(taskId).append("_di\" bpmnElement=\"").append(taskId).append("\">\n")
                    .append("        <omgdc:Bounds x=\"").append(x).append("\" y=\"").append(y - 10).append("\" width=\"").append(taskWidth).append("\" height=\"").append(taskHeight).append("\"/>\n")
                    .append("      </bpmndi:BPMNShape>\n");

            x += step;
        }

        // End shape
        int endX = x;
        bpmn.append("      <bpmndi:BPMNShape id=\"EndEvent_1_di\" bpmnElement=\"EndEvent_1\">\n")
                .append("        <omgdc:Bounds x=\"").append(endX).append("\" y=\"").append(y).append("\" width=\"36\" height=\"36\"/>\n")
                .append("      </bpmndi:BPMNShape>\n");

        // Edges between elements
        int currentX = startX;
        for (int i = 0; i < items.size(); i++) {
            String flowId = "Flow_" + i;

            int sourceX = (i == 0) ? currentX + 36 : taskXs.get(i - 1) + taskWidth;
            int targetX = taskXs.get(i);

            bpmn.append("      <bpmndi:BPMNEdge id=\"").append(flowId).append("_di\" bpmnElement=\"").append(flowId).append("\">\n")
                    .append("        <omgdi:waypoint x=\"").append(sourceX).append("\" y=\"").append(centerY).append("\"/>\n")
                    .append("        <omgdi:waypoint x=\"").append(targetX).append("\" y=\"").append(centerY).append("\"/>\n")
                    .append("      </bpmndi:BPMNEdge>\n");

            currentX += step;
        }

        // Edge to end
        int lastTaskX = taskXs.get(taskXs.size() - 1);
        bpmn.append("      <bpmndi:BPMNEdge id=\"Flow_end_di\" bpmnElement=\"Flow_end\">\n")
                .append("        <omgdi:waypoint x=\"").append(lastTaskX + taskWidth).append("\" y=\"").append(centerY).append("\"/>\n")
                .append("        <omgdi:waypoint x=\"").append(endX).append("\" y=\"").append(centerY).append("\"/>\n")
                .append("      </bpmndi:BPMNEdge>\n");

        bpmn.append("    </bpmndi:BPMNPlane>\n")
                .append("  </bpmndi:BPMNDiagram>\n")
                .append("</definitions>");

        return bpmn.toString();
    }

    private String escapeXml(String input) {
        return input == null ? "" : input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}