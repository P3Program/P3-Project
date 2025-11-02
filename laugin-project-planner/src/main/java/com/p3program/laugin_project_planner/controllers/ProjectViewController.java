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


@Controller
public class ProjectViewController {

    @Autowired
    private ProjectRepository projectRepository;

    @GetMapping("/")
    public String viewProjects(Model model) {
        model.addAttribute("projects", projectRepository.findAll());
        model.addAttribute("project", new Project());

        return "projects";
    }

    @PostMapping("/projects/save")
    public String saveProject(@ModelAttribute("project") Project project) {
        projectRepository.save(project);
        return "redirect:/";
    }

    @PostMapping("/projects/update")
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
        return "redirect:/";
    }

    @GetMapping("/projects/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id) {
        projectRepository.deleteById(id);
        return "redirect:/";
    }
}