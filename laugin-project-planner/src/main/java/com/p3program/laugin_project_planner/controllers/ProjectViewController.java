package com.p3program.laugin_project_planner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.p3program.laugin_project_planner.projects.Note;
import com.p3program.laugin_project_planner.projects.Project;
import com.p3program.laugin_project_planner.repositories.NoteRepository;
import com.p3program.laugin_project_planner.repositories.ProjectRepository;
import com.p3program.laugin_project_planner.services.ProjectService;

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

            allProjects = asc ? projectRepository.findAllActiveOrderByPriorityCustomAsc()
                            : projectRepository.findAllActiveOrderByPriorityCustomDesc();

            underReview = asc ? projectRepository.findActiveByStatusOrderByPriorityCustomAsc("underReview")
                            : projectRepository.findActiveByStatusOrderByPriorityCustomDesc("underReview");

            inProgress = asc ? projectRepository.findActiveByStatusOrderByPriorityCustomAsc("inProgress")
                            : projectRepository.findActiveByStatusOrderByPriorityCustomDesc("inProgress");

            billing = asc ? projectRepository.findActiveByStatusOrderByPriorityCustomAsc("billing")
                            : projectRepository.findActiveByStatusOrderByPriorityCustomDesc("billing");


        } else {
            Sort.Direction direction = "desc".equalsIgnoreCase(dir) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, mapSortField(sortBy))
                    .and(Sort.by(Sort.Direction.ASC, "estDueDate"))
                    .and(Sort.by(Sort.Direction.ASC, "date"))
                    .and(Sort.by(Sort.Direction.ASC, "id"));

            allProjects = projectRepository.findByEndDateIsNull(sort);
            underReview = projectRepository.findByStatusAndEndDateIsNull("underReview", sort);
            inProgress = projectRepository.findByStatusAndEndDateIsNull("inProgress", sort);
            billing = projectRepository.findByStatusAndEndDateIsNull("billing", sort);

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
    public String saveProject(@ModelAttribute("project") Project project, RedirectAttributes redirectAttributes) {
        try {
            projectService.createProject(project);
            redirectAttributes.addFlashAttribute("message", "Project created successfully now in All and under Review!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to create project: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
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
    // Complete task
    @PostMapping("/projects/{id}/complete")
    public String completeProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            projectService.markCompleted(id);
            redirectAttributes.addFlashAttribute("message", "Project marked completed");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to complete project: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/";
    }

    @PostMapping("/projects/edit/{id}")
    public String editProject(@PathVariable Long id, @ModelAttribute("project") Project project, RedirectAttributes redirectAttributes) {
        try {
            projectService.updateProject(id, project);
            redirectAttributes.addFlashAttribute("message", "Project updated");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to update project: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/";
    }

    @PostMapping("/projects/{id}/reopen")
    public String reopenProject(@PathVariable Long id,
                                @RequestParam(name = "newStatus", required = false) String newStatus,
                                RedirectAttributes redirectAttributes) {
        try {
            projectService.reopenProject(id, newStatus);
            redirectAttributes.addFlashAttribute("message", "Project re-opened");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to re-open project: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/archive";
    }
}
