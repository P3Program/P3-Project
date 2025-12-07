package com.p3program.laugin_project_planner.unit;

import com.p3program.laugin_project_planner.dto.SecurityUser;
import com.p3program.laugin_project_planner.repositories.AppUserRepository;
import com.p3program.laugin_project_planner.services.AppUserDetailsService;
import com.p3program.laugin_project_planner.users.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AppUserDetailsServiceTest {

    private AppUserRepository repository;
    private AppUserDetailsService service;

    @BeforeEach
    void setUp() {
        repository = mock(AppUserRepository.class);
        service = new AppUserDetailsService(repository);
    }

    @Test
    void loadUserByUsername_returnsSecurityUser_whenUserExists() {
        // Arrange
        AppUser user = new AppUser();
        user.setUsername("john");
        user.setPassword("secret");
        user.setRole("ROLE_ADMIN");

        when(repository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        // Act
        SecurityUser result = (SecurityUser) service.loadUserByUsername("john");

        //Assert
        assertEquals("john", result.getUsername());
        assertEquals("secret", result.getPassword());
        assertEquals("ROLE_ADMIN", result.getAuthorities().iterator().next().getAuthority());

        verify(repository, times(1)).findByUsername("john");
    }

    @Test
    void loadUserByUsername_throwsException_whenUserNotFound() {
        // Arrange
        when(repository.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing")
        );

        verify(repository, times(1)).findByUsername("missing");

    }
}
