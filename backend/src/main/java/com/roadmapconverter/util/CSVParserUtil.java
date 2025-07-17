package com.roadmapconverter.util;

import com.roadmapconverter.model.RoadmapItem;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVParserUtil {

    public List<RoadmapItem> parseCSV(InputStream csvInputStream) {
        List<RoadmapItem> items = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {  // skip header
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",", -1); // handles empty values

                if (parts.length >= 2) {
                    String title = parts[0].trim();
                    String description = parts[1].trim();
                    items.add(new RoadmapItem(title, description));
                }
            }
            System.out.println(items);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }
}