package com.p3program.laugin_project_planner.repositories;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.p3program.laugin_project_planner.projects.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    long countByPriority(String priority);

    List<Project> findByStatusOrderBySortIndexAsc(String status);

    List<Project> findAllByOrderBySortIndexAsc();

    @Query("SELECT COALESCE(MAX(p.sortIndex), -1) FROM Project p WHERE p.status = :status")
    int findMaxSortIndexByStatus(@Param("status") String status);

    @Query("SELECT COALESCE(SUM(p.hours), 0) FROM Project p WHERE p.priority = :priority")
    int sumHoursByPriority(@Param("priority") String priority);

    // List<Project> findByStatus(String status, Sort sort);
    List<Project> findAll(Sort sort);
    List<Project> findByEndDateIsNull(Sort sort);
    List<Project> findByStatusAndEndDateIsNull(String status, Sort sort);



    @Query("""
       SELECT p FROM Project p
       WHERE p.status = :status AND p.endDate IS NULL
       ORDER BY CASE p.priority
           WHEN 'Red' THEN 1
           WHEN 'Yellow' THEN 2
           WHEN 'Green' THEN 3
           ELSE 999 END ASC,
           p.estDueDate ASC,
           p.date ASC,
           p.id ASC
       """)
    List<Project> findActiveByStatusOrderByPriorityCustomAsc(@Param("status") String status);


    @Query("""
       SELECT p FROM Project p
       WHERE p.status = :status AND p.endDate IS NULL
       ORDER BY CASE p.priority
           WHEN 'Red' THEN 1
           WHEN 'Yellow' THEN 2
           WHEN 'Green' THEN 3
           ELSE 999 END DESC,
           p.estDueDate DESC,
           p.date DESC,
           p.id DESC
       """)
    List<Project> findActiveByStatusOrderByPriorityCustomDesc(@Param("status") String status);


    @Query("""
       SELECT p FROM Project p
       WHERE p.endDate IS NULL
       ORDER BY CASE p.priority
           WHEN 'Red' THEN 1
           WHEN 'Yellow' THEN 2
           WHEN 'Green' THEN 3
           ELSE 999 END ASC,
           p.estDueDate ASC,
           p.date ASC,
           p.id ASC
       """)
    List<Project> findAllActiveOrderByPriorityCustomAsc();


    @Query("""
       SELECT p FROM Project p
       WHERE p.endDate IS NULL
       ORDER BY CASE p.priority
           WHEN 'Red' THEN 1
           WHEN 'Yellow' THEN 2
           WHEN 'Green' THEN 3
           ELSE 999 END DESC,
           p.estDueDate DESC,
           p.date DESC,
           p.id DESC
       """)
    List<Project> findAllActiveOrderByPriorityCustomDesc();


@Query("SELECT p FROM Project p WHERE p.status = 'closed' " +
       "AND (:search IS NULL OR p.title LIKE %:search% OR p.description LIKE %:search%) " +
       "AND (:dateFrom IS NULL OR p.endDate >= :dateFrom) " +
       "AND (:dateTo IS NULL OR p.endDate <= :dateTo) " +
       "ORDER BY p.endDate DESC")
List<Project> findCompletedTasks(@Param("search") String search, 
                                   @Param("dateFrom") Date dateFrom,
                                   @Param("dateTo") Date dateTo);

long countByStatus(String status);

@Query("SELECT COUNT(p) FROM Project p WHERE p.status = 'closed' " +
       "AND FUNCTION('MONTH', p.endDate) = FUNCTION('MONTH', CURRENT_DATE) " +
       "AND FUNCTION('YEAR', p.endDate) = FUNCTION('YEAR', CURRENT_DATE)")
long countCompletedThisMonth();

@Query("SELECT COUNT(p) FROM Project p WHERE p.status = 'closed' " +
       "AND FUNCTION('WEEK', p.endDate) = FUNCTION('WEEK', CURRENT_DATE) " +
       "AND YEAR(p.endDate) = YEAR(CURRENT_DATE)")
long countCompletedThisWeek();

// @Query("SELECT DISTINCT p.name FROM Project p WHERE p.name IS NOT NULL")
// List<String> findDistinctProjects();

@Query("SELECT DISTINCT p.postCode FROM Project p WHERE p.postCode IS NOT NULL ORDER BY p.postCode ASC")
List<Integer> findDistinctPostCodes();

}