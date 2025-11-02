package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.projects.Project;
import com.p3program.laugin_project_planner.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

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

        // Fetch actual projects by priority
        List<Project> allProjects = projectRepository.findAll();

        List<Project> redProjects = allProjects.stream()
                .filter(p -> "Red".equals(p.getPriority()))
                .collect(Collectors.toList());

        List<Project> yellowProjects = allProjects.stream()
                .filter(p -> "Yellow".equals(p.getPriority()))
                .collect(Collectors.toList());

        List<Project> greenProjects = allProjects.stream()
                .filter(p -> "Green".equals(p.getPriority()))
                .collect(Collectors.toList());

        // Add counts to model
        model.addAttribute("redCount", redCount);
        model.addAttribute("yellowCount", yellowCount);
        model.addAttribute("greenCount", greenCount);

        // Add project lists to model
        model.addAttribute("redProjects", redProjects);
        model.addAttribute("yellowProjects", yellowProjects);
        model.addAttribute("greenProjects", greenProjects);

        return "KPI";
    }

    @PostMapping("/projects/update-kpi")
    public String updateProject(@ModelAttribute("project") Project project) {
        // Fetch existing project to preserve all fields
        Project existingProject = projectRepository.findById(project.getId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Update fields from form
        existingProject.setName(project.getName());
        existingProject.setPhoneNum(project.getPhoneNum());
        existingProject.setAddress(project.getAddress());
        existingProject.setEmail(project.getEmail());
        existingProject.setSsn(project.getSsn());
        existingProject.setTitle(project.getTitle());
        existingProject.setCaldera(project.isCaldera());
        existingProject.setWarranty(project.isWarranty());
        existingProject.setPriority(project.getPriority());
        existingProject.setHours(project.getHours());
        existingProject.setEstDueDate(project.getEstDueDate());
        existingProject.setDescription(project.getDescription());

        // Preserve status if not provided
        if (project.getStatus() != null && !project.getStatus().isEmpty()) {
            existingProject.setStatus(project.getStatus());
        }

        projectRepository.save(existingProject);
        return "redirect:/kpi";
    }

    @GetMapping("/projects/delete-kpi/{id}")
    public String deleteProjectFromKpi(@PathVariable("id") Long id) {
        projectRepository.deleteById(id);
        return "redirect:/kpi";
    }
}