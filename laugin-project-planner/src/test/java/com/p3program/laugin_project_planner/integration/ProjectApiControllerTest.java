package com.p3program.laugin_project_planner.integration;

import com.p3program.laugin_project_planner.config.SecurityConfig;
import com.p3program.laugin_project_planner.controllers.ProjectApiController;
import com.p3program.laugin_project_planner.services.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProjectApiController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = true) // Used to enable/disable Spring Security for testing
@ActiveProfiles("prod")
public class ProjectApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService service;

    // Note: method 'perform' requires the test method to throw exception, line below
    @Test
    @WithMockUser
    void move_CallsServiceCorrectly() throws Exception {
        // Arrange
            // Since the controller has no internal logic, nothing needs to be arranged. Simple methods, simple tests

        // Act
            // We do the mock call to move the test, with enabled security (csrf), since 'patch' requires authentication.
            // Projectid is injected in the url below: "5"
            // It is used in the verification under Assert
        mockMvc.perform(
                patch("/api/projects/5/move")
                    .with(csrf())
                    .param("toStatus", "inProgress")
                )
                .andExpect(status().isNoContent());

        // Assert
            // We assert that the method correctly calls the service 1 time
        verify(service, times(1)).moveToStatus(5L, "inProgress");
    }

    @Test
    @WithMockUser
    void reorder_CallsServiceCorrectly() throws Exception {

        // Arrange
            // Since the method takes a list of ids, we need to make one. Furthermore, the reorder method expects
            // the orderedids as a @RequestBody, so it has to be parsed as a json string in the mock, else the test fails.
        List<Long> ids = List.of(1L, 2L, 5L, 9L, 20L);
        String requestJsonBody = "[1, 2, 5, 9, 20]";

        // Act
            // Therefor no params, but this convoluted convert List -> Json string -> define content type -> parse json string.
        mockMvc.perform(
                patch("/api/projects/reorder")
                        .with(csrf())
                        .param("status", "someStatus")
                        .contentType("application/json")
                        .content(requestJsonBody)
                )
                .andExpect(status().isNoContent());

        // Assert
            // Again assert that the service is called correctly onw time, with the mocked values
        verify(service, times(1)).reorderWithinStatus("someStatus", ids);
    }
}
