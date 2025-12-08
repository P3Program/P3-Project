package com.p3program.laugin_project_planner.unit;

import com.p3program.laugin_project_planner.dto.SecurityUser;
import com.p3program.laugin_project_planner.repositories.AppUserRepository;
import com.p3program.laugin_project_planner.services.AppUserDetailsService;
import com.p3program.laugin_project_planner.users.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * First attempt at a unittest.
 * - Using the "Arrange -> Act -> Assert" testing paradigm.
 *    - You arrange by creating the circumstances for the test,
 *      eg. creating the objects, mocking data etc.
 *    - You act by simulating the interaction you test for
 *    - You assert what output you expect.
 * - Asserting (assert) to test values returned.
 * - Verifying (verify) to test behavior of the code.
 */

public class AppUserDetailsServiceTest {

    // Attributes holding the repository and service variables.
    private AppUserRepository repository;
    private AppUserDetailsService service;

    // The classic testing before each hook.
    // It runs before each test is run in succession,
    // basically creating new, fresh objects to test, each test.
    @BeforeEach
    void setUp() {
        repository = mock(AppUserRepository.class);
        service = new AppUserDetailsService(repository);
    }

    // First test - attempt at transparent naming
    // It tests that an existing user gets called by the repository,
    // it returns the user, matching the attributes.
    @Test
    void loadUserByUsername_returnsSecurityUser_whenUserExists() {

        // Arrange
            // Create the user object, the repository will return.
        AppUser user = new AppUser();
        user.setUsername("john");
        user.setPassword("secret");
        user.setRole("ROLE_ADMIN");

            // This mimics the return from findByUsername in AppUserRepository.
        when(repository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        // Act
            // The result is typecast to a SecurityUser, because the method loadByUsername,
            // returns a UserDetails(Service) object (Spring magic), but our DTO SecurityUser
            // wraps it into a SecurityUser object, so we need to typecast it to match.
        SecurityUser result = (SecurityUser) service.loadUserByUsername("john");

        //Assert
            // Asserts the username and password is parsed and/or returned.
        assertEquals("john", result.getUsername());
        assertEquals("secret", result.getPassword());
            // Complex assert, since the roles are kept in an unordered Collection, we must call it
            // using contains method, and the Spring Security object SimpleGrantedAuthority, assigning a user a role.
        assertTrue(result.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")),
                "Expected ROLE_ADMIN to be present in authorities");

        // Verify
            // Here we verify that the repo is only called once, in order to maintain
            // database efficiency.
        verify(repository, times(1)).findByUsername("john");
    }

    @Test
    void loadUserByUsername_throwsException_whenUserNotFound() {
        // Arrange
            // Here we mock that the username is "missing",
            // soo the repo call returns empty.
        when(repository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        // Act and Assert
            // Spring Security responds with an exception, we assert it.
        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing")
        );

        // Verify
            // Here we verify that the repo is only called once, in order to maintain
            // database efficiency.
        verify(repository, times(1)).findByUsername("missing");
    }
}
