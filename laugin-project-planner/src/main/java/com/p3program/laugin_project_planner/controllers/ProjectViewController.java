package com.p3program.laugin_project_planner.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectViewController {


    @GetMapping("/")
    public String homeRedirect() {
        return "redirect:/projects";
    }

    @GetMapping("/projects")
    public String showProjects() {

        return "projects";
    }
}
