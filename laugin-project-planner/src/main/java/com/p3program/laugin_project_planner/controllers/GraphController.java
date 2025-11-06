package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.repositories.ProjectRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class GraphController {

    private final ProjectRepository graphController;

    public GraphController(ProjectRepository graphController) {
        this.graphController = graphController;
    }

    @GetMapping("/hours-by-priority")
    public Map<String, Integer> getHoursByPriority() {
        Map<String, Integer> data = new HashMap<>();
        data.put("Green", graphController.sumHoursByPriority("Green"));
        data.put("Yellow", graphController.sumHoursByPriority("Yellow"));
        data.put("Red", graphController.sumHoursByPriority("Red"));
        return data;
    }
}
