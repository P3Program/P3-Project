package com.p3program.laugin_project_planner.repositories;

import com.p3program.laugin_project_planner.projects.Project;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    long countByPriority(String priority);

    List<Project> findByStatusOrderBySortIndexAsc(String status);

    List<Project> findAllByOrderBySortIndexAsc();

    @Query("SELECT COALESCE(MAX(p.sortIndex), -1) FROM Project p WHERE p.status = :status")
    int findMaxSortIndexByStatus(@Param("status") String status);

    @Query("SELECT COALESCE(SUM(p.hours), 0) FROM Project p WHERE p.priority = :priority")
    int sumHoursByPriority(@Param("priority") String priority);

    List<Project> findByStatus(String status, Sort sort);
    List<Project> findAll(Sort sort);

    @Query("""
           SELECT p FROM Project p
           WHERE p.status = :status
           ORDER BY CASE p.priority
               WHEN 'Red' THEN 1
               WHEN 'Yellow' THEN 2
               WHEN 'Green' THEN 3
               ELSE 999 END ASC,
               p.estDueDate ASC,
               p.date ASC,
               p.id ASC
           """)
    List<Project> findByStatusOrderByPriorityCustomAsc(@Param("status") String status);

    @Query("""
           SELECT p FROM Project p
           WHERE p.status = :status
           ORDER BY CASE p.priority
               WHEN 'Red' THEN 1
               WHEN 'Yellow' THEN 2
               WHEN 'Green' THEN 3
               ELSE 999 END DESC,
               p.estDueDate DESC,
               p.date DESC,
               p.id DESC
           """)
    List<Project> findByStatusOrderByPriorityCustomDesc(@Param("status") String status);

    @Query("""
           SELECT p FROM Project p
           ORDER BY CASE p.priority
               WHEN 'Red' THEN 1
               WHEN 'Yellow' THEN 2
               WHEN 'Green' THEN 3
               ELSE 999 END ASC,
               p.estDueDate ASC,
               p.date ASC,
               p.id ASC
           """)
    List<Project> findAllOrderByPriorityCustomAsc();

    @Query("""
           SELECT p FROM Project p
           ORDER BY CASE p.priority
               WHEN 'Red' THEN 1
               WHEN 'Yellow' THEN 2
               WHEN 'Green' THEN 3
               ELSE 999 END DESC,
               p.estDueDate DESC,
               p.date DESC,
               p.id DESC
           """)
    List<Project> findAllOrderByPriorityCustomDesc();

}