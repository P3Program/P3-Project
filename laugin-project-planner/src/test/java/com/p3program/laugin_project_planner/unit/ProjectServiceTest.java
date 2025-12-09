package com.p3program.laugin_project_planner.unit;

import com.p3program.laugin_project_planner.projects.Project;
import com.p3program.laugin_project_planner.repositories.ProjectRepository;
import com.p3program.laugin_project_planner.services.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.parameters.P;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
            // not use any. Focus on what methods the class you test are using

        Project project = new Project();

        when(repository.findMaxSortIndexByStatus("underReview")).thenReturn(5);
        when(repository.save(project)).thenReturn(project);

        // Act
            // Call createProject and save to a separate variable for comparison
        Project result = service.createProject(project);

        // Assert
            // Assert the values and behavior dictated in arrange
        assertEquals(6, result.getSortIndex()); // if findMaxSortIndexByStatus returns 5, getSortIndex is 5 + 1 = 6
        assertEquals("underReview", result.getStatus());
        assertEquals(result.getSortIndex(), project.getSortIndex()); // Direct comparison of mocked and called objects
        assertEquals(result.getStatus(), project.getStatus()); // Direct comparison of mocked and called objects


        verify(repository, times(1)).save(project);
    }

    @Test
    void deleteProject_DeletesProject() {
        // Arrange
            // No arrange, simple methods lead to simple tests

        // Act
            // Call the method with an arbitrary value
        service.deleteProject(2L);

        // Assert
            // No asserts, only verify the behavior; does the service make the call to Spring JPA and only one time?
        verify(repository, times(1)).deleteById(2L);
    }

    @Test
    void moveToStatus_HappyPath() {
        // Arrange
            // Create a project object since the method mutates it. Set the necessary fields with values.
        Project project = new Project();
        project.setId(2L);
        project.setStatus("oldStatus");

            // Arrange the interactions to be tested
        when(repository.findById(2L)).thenReturn(Optional.of(project));
        when(repository.findMaxSortIndexByStatus("newStatus")).thenReturn(5);
        when(repository.save(project)).thenReturn(project);

        // Act
            // Call the method
        service.moveToStatus(2L, "newStatus");
        // Assert
            // Assert the arranged values and behaviours
        assertEquals(6, project.getSortIndex());
        assertEquals(2L, project.getId());
        assertEquals("newStatus", project.getStatus());

        verify(repository, times(1)).findById(2L);
        verify(repository, times(1)).findMaxSortIndexByStatus("newStatus");
        verify(repository, times(1)).save(project);
    }

    @Test
    void moveToStatus_ProjectNotFound_ThrowsException() {
        // Arrange
            // We give arbitrary Long variable to signify "does not exist", therefore returning an empty object
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act + Assert
            // The exception is thrown when moveToStatus is invoked
        assertThrows(NoSuchElementException.class, () ->
                service.moveToStatus(999L, "newStatus")
        );
            // Since the an exception is thrown and the corresponding if statement is never executed,
            // no further methods are called, we check for that
        verify(repository, never()).findMaxSortIndexByStatus(any());
        verify(repository, never()).save(any());
    }
}
