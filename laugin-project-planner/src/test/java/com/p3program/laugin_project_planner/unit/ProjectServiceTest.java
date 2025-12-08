package com.p3program.laugin_project_planner.unit;

import com.p3program.laugin_project_planner.projects.Project;
import com.p3program.laugin_project_planner.repositories.ProjectRepository;
import com.p3program.laugin_project_planner.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

/**
 * Project Service Test.
 * ProjectService real code is tested against mocked repository, which fakes database interaction.
 */

public class ProjectServiceTest {


    private ProjectRepository repository;
    private ProjectService service;

    // Mocks the repo, parses it to the real ProjectService
    @BeforeEach
    void setUp() {
        repository = mock(ProjectRepository.class);
        service = new ProjectService(repository);
    }

    @Test
    void createProject_ReturnsProjectObject() {
        // Arrange
            // Create a real project object to test. No fields needed, since ProjectService does
            // not use any. Focus on what methods the class you test are using.

        Project project = new Project();

        when(repository.findMaxSortIndexByStatus("underReview")).thenReturn(5);
        when(repository.save(project)).thenReturn(project);

        Project result = service.createProject(project);

        assertEquals(6, result.getSortIndex());
        assertEquals("underReview", result.getStatus());
        assertEquals(result.getSortIndex(), project.getSortIndex());
        assertEquals(result.getStatus(), project.getStatus());


        verify(repository, times(1)).save(project);
    }

    // TODO not even close to done
    @Test
    void deleteProject_DeletesProject() {
        // Arrange

        when(repository.findById(2L)).thenReturn(Optional.of(project).withid(2L));
        when(repository.deleteById(2L)).thenReturn(null);

        service.deleteProject(2L);

        assertNull(project);
    }

    // TODO make this aswell. Rename
    @Test
    void moveToStatus_ActuallyWorks() {

    }
}
