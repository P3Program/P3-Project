package com.p3program.laugin_project_planner.repositories;

import com.p3program.laugin_project_planner.projects.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long>{
    long countByPriority(String priority);

    List<Project> findByStatusOrderBySortIndexAsc(String status);


    List<Project> findAllByOrderBySortIndexAsc();

    @Query("SELECT COALESCE(MAX(p.sortIndex), -1) FROM Project p WHERE p.status = :status")
    int findMaxSortIndexByStatus(@Param("status") String status);
}