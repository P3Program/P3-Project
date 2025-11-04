package com.p3program.laugin_project_planner.repositories;

import com.p3program.laugin_project_planner.projects.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByProjectIdOrderByTimestampDesc(Long projectId);
}