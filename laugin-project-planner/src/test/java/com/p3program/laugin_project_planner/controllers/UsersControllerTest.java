package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.config.SecurityConfig;
import com.p3program.laugin_project_planner.repositories.AppUserRepository;
import com.p3program.laugin_project_planner.users.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UsersController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("prod")
    class UsersControllerTest {

        // So basically we have returned to Spring doing magic behind the scenes with these initiations:
        // Spring sends us the objects we need for the test.
        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AppUserRepository appUserRepository;

        @MockitoBean
        private PasswordEncoder passwordEncoder;

        // This ensures that security context is parsed
        // purely debugging the test (because Spring Security makes testing hard)
        /*
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

            // Within the test, setup the mocks (that is, what the program SHOULD do if real life, but this is all play-pretend)
            when(appUserRepository.existsByUsername("testuser")).thenReturn(false); // Mocks the duplicate check in UserController
            when(passwordEncoder.encode("password123")).thenReturn("$encoded$"); // Mocks the encoded password

            // Now we make the server request
            mockMvc.perform(post("/users/create")
                    .with(csrf())
                    .param("name", "Test Name")
                    .param("username", "testuser")
                    .param("password", "password123")
                    .param("role", "ADMIN"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/users"))
                    .andExpect(flash().attribute("messageType", "success"));

            // Last we verify that the user is saved to the database via the appuserrepo

            verify(appUserRepository).save(any(AppUser.class));
            verify(appUserRepository).save(argThat(user ->
                    user.getUsername().equals("testuser") &&
                    user.getRole().equals("ADMIN") &&
                    user.getName().equals("Test Name") &&
                    user.getPassword().equals("$encoded$")
            ));
        }

        @Test
        @WithMockUser(username = "admin", roles = {"ADMIN"})
        public void shouldRejectDuplicateUser() throws Exception {
            when(appUserRepository.existsByUsername("testuser")).thenReturn(true);
            when(passwordEncoder.encode("password123")).thenReturn("$encoded$");

            mockMvc.perform(post("/users/create")
                    .with(csrf())
                    .param("name", "Test Name")
                    .param("username", "testuser")
                    .param("password", "password123")
                    .param("role", "ADMIN"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/users"))
                    .andExpect(flash().attribute("messageType", "error"))
                    .andExpect(flash().attribute("message", "Username already exists!"));

            verify(appUserRepository).existsByUsername("testuser");
            verify(appUserRepository, never()).save(any(AppUser.class));
        }
    }