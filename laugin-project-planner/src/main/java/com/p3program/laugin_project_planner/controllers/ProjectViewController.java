package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.services.ProjectService;
import com.p3program.laugin_project_planner.projects.Project;
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
    private ProjectService projectService;

    @GetMapping("/")
    public String viewProjects(Model model) {
        model.addAttribute("projects", projectService.getAllProjects());
        return "projects";
    }

    @GetMapping("/projects/new")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new Project());
        return "create-project";
    }

    @PostMapping("/projects/save")
    public String saveProject(@ModelAttribute("project") Project project) {
        projectService.createProject(project);
        return "redirect:/";
    }

    @GetMapping("/projects/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id) {
        projectService.deleteProject(id);
        return "redirect:/";
    }
}