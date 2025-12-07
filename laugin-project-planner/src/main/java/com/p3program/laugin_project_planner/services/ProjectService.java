package com.p3program.laugin_project_planner.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.p3program.laugin_project_planner.projects.Project;
import com.p3program.laugin_project_planner.repositories.ProjectRepository;

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
    // Complete task
    @Transactional
    public void markCompleted(Long projectId) {
        Project p = projectRepository.findById(projectId).orElseThrow();
        if (!Objects.equals(p.getStatus(), "closed")) {
            int max = projectRepository.findMaxSortIndexByStatus("closed");
            p.setStatus("closed");
            p.setSortIndex(max + 1);
            p.setEndDate(java.sql.Date.valueOf(java.time.LocalDate.now()));
            projectRepository.save(p);
        }
    }

    // Reopen task
    @Transactional
    public void reopenProject(Long projectId, String newStatus) {
        Project p = projectRepository.findById(projectId).orElseThrow();
        p.setEndDate(null);
        String statusToSet = (newStatus == null || newStatus.isEmpty()) ? "underReview" : newStatus;
        p.setStatus(statusToSet);
        int max = projectRepository.findMaxSortIndexByStatus(statusToSet);
        p.setSortIndex(max + 1);
        projectRepository.save(p);
    }

    // Update task
    @Transactional
    public void updateProject(Long projectId, Project updated) {
        Project p = projectRepository.findById(projectId).orElseThrow();

        p.setName(updated.getName());
        p.setPhoneNum(updated.getPhoneNum());
        p.setAddress(updated.getAddress());
        p.setSsn(updated.getSsn());
        p.setEmail(updated.getEmail());
        p.setTitle(updated.getTitle());
        p.setHours(updated.getHours());
        p.setEstDueDate(updated.getEstDueDate());
        p.setDescription(updated.getDescription());
        p.setCaldera(updated.isCaldera());
        p.setWarranty(updated.isWarranty());
        p.setPriority(updated.getPriority());

        projectRepository.save(p);
    }
}