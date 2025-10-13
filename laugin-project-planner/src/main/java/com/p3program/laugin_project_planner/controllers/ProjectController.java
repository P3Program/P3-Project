package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.projects.Project;
import com.p3program.laugin_project_planner.repositories.ProjectRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectRepository projectRepository;

    public ProjectController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @PostMapping("/create")
    public String createProjectFromForm(@RequestParam("name") String name, @RequestParam("address") String address, @RequestParam("phoneNum") String phoneNum) {
        Project project = new Project();
        project.setName(name);
        project.setAddress(address);
        project.setPhoneNum(phoneNum);
        projectRepository.save(project);
        return "Created a new project successfully";
    }

}
