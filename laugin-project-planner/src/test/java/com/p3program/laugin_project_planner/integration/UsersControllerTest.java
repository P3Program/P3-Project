package com.p3program.laugin_project_planner.integration;

import com.p3program.laugin_project_planner.config.SecurityConfig;
import com.p3program.laugin_project_planner.controllers.UsersController;
import com.p3program.laugin_project_planner.services.UserService;
import com.p3program.laugin_project_planner.users.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc(addFilters = true) // Used to enable/disable Spring Security for testing
@ActiveProfiles("prod")
class UsersControllerTest {

    // MockMvc lets us fake HTTP requests to test the controller
    @Autowired
    private MockMvc mockMvc;

    // Now we mock the UserService with a Mockito bean, strictly scoped for testing only.
    @MockitoBean
    private UserService userService;

    /* This ensures that security context is parsed. Purely for debugging the test
       (because Spring Security makes testing hard)

    @Test
    void checkSecurityContext() {
    SecurityContext context = SecurityContextHolder.getContext();
    Authentication auth = context.getAuthentication();
    System.out.println("Auth: " + auth);
    System.out.println("Authorities: " + (auth != null ? auth.getAuthorities() : "null"));
    }*/

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // <-- needed because of @PreAuthorize in UserController
    void shouldCreateAdminSuccessfully() throws Exception {

        // Arrange
            // Within the test, set up the mocks (that is, what the program SHOULD do in real life, but this is all play-pretend)
        AppUser mockUser = new AppUser();
        mockUser.setUsername("testuser");
        mockUser.setRole("ADMIN");
        mockUser.setName("Test Name");

        when(userService.createUser("Test Name", "testuser", "password123", "ADMIN"))
                .thenReturn(mockUser);

        // Act + Assert
            // Now we make the server request (WITH CSRF, else security gets in the way),
            // and assert the expected values and behavior
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

            // Last we verify that the service was called (only once) with the correct parameters
        verify(userService, times(1)).createUser("Test Name", "testuser", "password123", "ADMIN");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void shouldRejectDuplicateUser() throws Exception {

        // Arrange
            // Mock the service to throw an exception when trying to create a duplicate user
            // This simulates what happens when the username already exists
        when(userService.createUser("Test Name", "testuser", "password123", "ADMIN"))
                .thenThrow(new IllegalArgumentException("Username already exists!"));

        // Act + Assert
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

        // Verify that the service was called (only once), and threw the exception
        verify(userService, times(1)).createUser("Test Name", "testuser", "password123", "ADMIN");
    }

    @Test
    @WithMockUser(roles = "USER")
    public void rejectNonAdminUserWhenCreatingNewUser() throws Exception {
        // This test verifies that Spring Security blocks non-admin users
        // The service should never be called because security stops the request first

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

        // Verify service was never called (security blocked it before reaching the controller)
        verify(userService, never()).createUser(any(), any(), any(), any());
    }
}