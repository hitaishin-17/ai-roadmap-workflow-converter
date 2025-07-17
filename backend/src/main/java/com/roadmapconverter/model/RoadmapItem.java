package com.roadmapconverter.model;

import lombok.Data;

@Data
public class RoadmapItem {
    private String title;
    private String description;

    public RoadmapItem(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
