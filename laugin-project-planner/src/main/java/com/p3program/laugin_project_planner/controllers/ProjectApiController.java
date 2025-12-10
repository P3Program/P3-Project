package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.services.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectApiController {

    private final ProjectService service;

    public ProjectApiController(ProjectService service) {
        this.service = service;
    }

    @PatchMapping("/{id}/move")
    public ResponseEntity<Void> move(@PathVariable Long id, @RequestParam String toStatus) {
        service.moveToStatus(id, toStatus);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestParam String status, @RequestBody List<Long> orderedIds) {
        service.reorderWithinStatus(status, orderedIds);
        return ResponseEntity.noContent().build();
    }
}

