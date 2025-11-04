package com.p3program.laugin_project_planner.services;

import com.p3program.laugin_project_planner.projects.Project;
import com.p3program.laugin_project_planner.repositories.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project createProject(Project project) {
        String status = project.getStatus() != null ? project.getStatus() : "allProjects";
        int max = projectRepository.findMaxSortIndexByStatus(status);
        project.setStatus(status);
        project.setSortIndex(max + 1);
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAllByOrderBySortIndexAsc();
    }

    public List<Project> getByStatus(String status) {
        return projectRepository.findByStatusOrderBySortIndexAsc(status);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Transactional
    public void reorderWithinStatus(String status, List<Long> orderedIds) {
        List<Project> inStatus = projectRepository.findByStatusOrderBySortIndexAsc(status);
        Map<Long, Project> byId = inStatus.stream()
                .collect(Collectors.toMap(Project::getId, p -> p));

        int i = 0;
        for (Long id : orderedIds) {
            Project p = byId.get(id);
            if (p != null) {
                p.setSortIndex(i++);
            }
        }
        projectRepository.saveAll(inStatus);
    }

    @Transactional
    public void moveToStatus(Long projectId, String newStatus) {
        Project p = projectRepository.findById(projectId).orElseThrow();
        if (!Objects.equals(p.getStatus(), newStatus)) {
            int max = projectRepository.findMaxSortIndexByStatus(newStatus);
            p.setStatus(newStatus);
            p.setSortIndex(max + 1);
            projectRepository.save(p);
        }
    }
}
