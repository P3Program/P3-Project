package com.p3program.laugin_project_planner.repositories;

import com.p3program.laugin_project_planner.projects.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long>{
}
