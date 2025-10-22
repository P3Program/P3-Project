package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KpiController {

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/kpi")
    public String viewKpi(Model model) {
        // Count tasks by priority color
        long redCount = projectRepository.countByPriority("Red");
        long yellowCount = projectRepository.countByPriority("Yellow");
        long greenCount = projectRepository.countByPriority("Green");

        // Add to model for Thymeleaf
        model.addAttribute("redCount", redCount);
        model.addAttribute("yellowCount", yellowCount);
        model.addAttribute("greenCount", greenCount);

        return "KPI";
    }
}