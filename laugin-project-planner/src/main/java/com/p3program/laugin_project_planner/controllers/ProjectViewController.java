package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.projects.Note;
import com.p3program.laugin_project_planner.projects.Project;
import com.p3program.laugin_project_planner.repositories.NoteRepository;
import com.p3program.laugin_project_planner.repositories.ProjectRepository;
import com.p3program.laugin_project_planner.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    public String viewProjects(
            @RequestParam(defaultValue = "priority") String sortBy,
            @RequestParam(defaultValue = "asc") String dir,
            Model model

    ) {
        List<Project> projects = projectService.getAllProjects();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User: " + auth.getName() + ", Authorities: " + auth.getAuthorities());

        model.addAttribute("sortBy", sortBy);
        model.addAttribute("dir", dir);

        List<Project> allProjects;
        List<Project> underReview;
        List<Project> inProgress;
        List<Project> billing;

        if ("priority".equalsIgnoreCase(sortBy)) {
            boolean asc = !"desc".equalsIgnoreCase(dir);

            allProjects = asc ? projectRepository.findAllOrderByPriorityCustomAsc()
                    : projectRepository.findAllOrderByPriorityCustomDesc();

            underReview = asc ? projectRepository.findByStatusOrderByPriorityCustomAsc("underReview")
                    : projectRepository.findByStatusOrderByPriorityCustomDesc("underReview");

            inProgress  = asc ? projectRepository.findByStatusOrderByPriorityCustomAsc("inProgress")
                    : projectRepository.findByStatusOrderByPriorityCustomDesc("inProgress");

            billing     = asc ? projectRepository.findByStatusOrderByPriorityCustomAsc("billing")
                    : projectRepository.findByStatusOrderByPriorityCustomDesc("billing");

        } else {
            Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, mapSortField(sortBy))
                    .and(Sort.by(Sort.Direction.ASC, "estDueDate"))
                    .and(Sort.by(Sort.Direction.ASC, "date"))
                    .and(Sort.by(Sort.Direction.ASC, "id"));

            allProjects = projectRepository.findAll(sort);
            underReview = projectRepository.findByStatus("underReview", sort);
            inProgress  = projectRepository.findByStatus("inProgress",  sort);
            billing     = projectRepository.findByStatus("billing",     sort);
        }

        model.addAttribute("project", new Project());
        model.addAttribute("allProjects", allProjects);
        model.addAttribute("underReview", underReview);
        model.addAttribute("inProgress", inProgress);
        model.addAttribute("billing", billing);

        model.addAttribute("activePage", "projects");
        System.out.println("DEBUG: Fetched " + projects.size() + " projects");
        return "projects";
    }

    private String mapSortField(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "due"     -> "estDueDate";
            case "created" -> "date";
            case "customer"-> "name";
            case "hours"   -> "hours";
            case "priority"-> "priority";
            default        -> "estDueDate";
        };
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
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) return null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Note note;
        note = new Note(project, noteText, username);
        return noteRepository.save(note);
    }
}
