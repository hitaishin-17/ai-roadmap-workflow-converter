package com.roadmapconverter.controller;

import com.roadmapconverter.service.AIMapperService;
import com.roadmapconverter.service.BPMNGeneratorService;
import com.roadmapconverter.util.CSVParserUtil;
import com.roadmapconverter.model.RoadmapItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/roadmap")
public class RoadmapController {

    @Autowired
    private CSVParserUtil csvParserUtil;

    @Autowired
    private AIMapperService aiMapperService;

    @Autowired
    private BPMNGeneratorService bpmnGeneratorService;

    @PostMapping("/convert")
    public ResponseEntity<?> convertRoadmap(@RequestParam("file") MultipartFile file) {
        try {
            List<RoadmapItem> roadmapItems = csvParserUtil.parseCSV(file.getInputStream());
            System.out.println(roadmapItems);
            String bpmnXml = bpmnGeneratorService.generateAndSaveBPMN(roadmapItems);

            // Option to return as file download
            ByteArrayInputStream stream = new ByteArrayInputStream(bpmnXml.getBytes());
            InputStreamResource resource = new InputStreamResource(stream);

            System.out.println(resource);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=workflow.bpmn");

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_XML)
                    .body(bpmnXml);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<List<RoadmapItem>> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("jhjj");
            List<RoadmapItem> items = csvParserUtil.parseCSV(file.getInputStream());
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/ping")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("API is running");
    }

    @PostMapping("/map-ai")
    public ResponseEntity<String> mapToBPMN(@RequestBody String roadmapText) {
        String bpmnXml = aiMapperService.mapTextToSteps(roadmapText);
        return ResponseEntity.ok(bpmnXml); // so frontend receives XML
    }

    @GetMapping("/download-bpmn")
    public ResponseEntity<Resource> downloadBPMN() throws IOException {
        Path path = Paths.get("bpmn-output/sample-onboarding.bpmn");
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=workflow.bpmn")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}