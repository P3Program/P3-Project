package com.p3program.laugin_project_planner.integration;

import com.p3program.laugin_project_planner.config.SecurityConfig;
import com.p3program.laugin_project_planner.controllers.UsersController;
import com.p3program.laugin_project_planner.services.UserService;
import com.p3program.laugin_project_planner.users.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsersController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("prod")
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;  // <-- NOW MOCK THE SERVICE, NOT REPO!

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void shouldCreateAdminSuccessfully() throws Exception {
        // Arrange - mock the service layer
        AppUser mockUser = new AppUser();
        mockUser.setUsername("testuser");
        mockUser.setRole("ADMIN");
        mockUser.setName("Test Name");

        when(userService.createUser("Test Name", "testuser", "password123", "ADMIN"))
                .thenReturn(mockUser);

        // Act
        mockMvc.perform(
                        post("/users/create")
                                .with(csrf())
                                .param("name", "Test Name")
                                .param("username", "testuser")
                                .param("password", "password123")
                                .param("role", "ADMIN")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attribute("messageType", "success"));

        // Assert - verify service was called correctly
        verify(userService).createUser("Test Name", "testuser", "password123", "ADMIN");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldRejectDuplicateUser() throws Exception {
        // Arrange - mock service throwing exception
        when(userService.createUser("Test Name", "testuser", "password123", "ADMIN"))
                .thenThrow(new IllegalArgumentException("Username already exists!"));

        // Act
        mockMvc.perform(
                        post("/users/create")
                                .with(csrf())
                                .param("name", "Test Name")
                                .param("username", "testuser")
                                .param("password", "password123")
                                .param("role", "ADMIN")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attribute("messageType", "error"))
                .andExpect(flash().attribute("message", "Username already exists!"));

        // Assert
        verify(userService).createUser("Test Name", "testuser", "password123", "ADMIN");
    }

    @Test
    @WithMockUser(roles = "USER")
    public void rejectNonAdminUserWhenCreatingNewUser() throws Exception {
        mockMvc.perform(
                        post("/users/create")
                                .with(csrf())
                                .param("name", "Test Name")
                                .param("username", "testuser")
                                .param("password", "password123")
                                .param("role", "ADMIN")
                )
                .andExpect(status().isForbidden())
                .andDo(print())
                .andExpect(forwardedUrl("/access-denied"));

        // Verify service was never called (security blocked it)
        verify(userService, never()).createUser(any(), any(), any(), any());
    }
}