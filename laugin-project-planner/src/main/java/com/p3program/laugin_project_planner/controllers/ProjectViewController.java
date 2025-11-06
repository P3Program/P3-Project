package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.projects.Note;
import com.p3program.laugin_project_planner.projects.Project;
import com.p3program.laugin_project_planner.repositories.NoteRepository;
import com.p3program.laugin_project_planner.repositories.ProjectRepository;
import com.p3program.laugin_project_planner.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class ProjectViewController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private NoteRepository noteRepository;


    @GetMapping("/")
    public String viewProjects(Model model) {
        model.addAttribute("projects", projectService.getAllProjects());
        model.addAttribute("project", new Project());
        model.addAttribute("activePage", "projects");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User: " + auth.getName() + ", Authorities: " + auth.getAuthorities());

        return "projects";
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

    // Get all notes for a project
    @GetMapping("/projects/{id}/notes")
    @ResponseBody
    public List<Note> getNotes(@PathVariable long id) {
        return noteRepository.findByProjectIdOrderByTimestampDesc(id);
    }

    // Add a new note to a project
    @PostMapping("/projects/{id}/addNote")
    @ResponseBody
    public Note addNote(@PathVariable long id, @RequestParam String noteText) {
        // Find the project
        Project project = projectRepository.findById(id).orElse(null);

        if (project == null) {
            return null;
        }

        // Create and save the note
        Note note = new Note(project, noteText);
        return noteRepository.save(note);
    }
}