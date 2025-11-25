package com.p3program.laugin_project_planner.controllers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.p3program.laugin_project_planner.projects.Project;
import com.p3program.laugin_project_planner.repositories.ProjectRepository;

@Controller
public class ArchiveController {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @GetMapping("/archive")
    public String showArchive(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Model model) {
        
        Date sqlDateFrom = dateFrom != null ? Date.valueOf(dateFrom) : null;
        Date sqlDateTo = dateTo != null ? Date.valueOf(dateTo) : null;
        
        List<Project> projects = projectRepository.findCompletedTasks(search, sqlDateFrom, sqlDateTo);
        
        model.addAttribute("projects", projects);
        model.addAttribute("totalCompleted", projectRepository.countByStatus("closed"));
        model.addAttribute("completedThisMonth", projectRepository.countCompletedThisMonth());
        model.addAttribute("completedThisWeek", projectRepository.countCompletedThisWeek());
        model.addAttribute("allProjects", new ArrayList<>());
        model.addAttribute("search", search);
        model.addAttribute("dateFrom", dateFrom);
        model.addAttribute("dateTo", dateTo);
        model.addAttribute("activePage", "archive");  
        
        return "archive";
    }
}