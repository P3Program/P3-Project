package com.p3program.laugin_project_planner.services;

import com.p3program.laugin_project_planner.repositories.AppUserRepository;
import com.p3program.laugin_project_planner.users.AppUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for user management business logic.
 * Handles validation, password encoding, and database operations.
 */
@Service
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(AppUserRepository appUserRepository,
                       PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }

    public AppUser getUserById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Creates a new user with encrypted password.
     * Checks for duplicate usernames before saving.
     */
    public AppUser createUser(String name, String username, String password, String role) {
        // Check if username already exists in database
        if (appUserRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists!");
        }

        // Create new user object and set all fields
        AppUser newUser = new AppUser();
        newUser.setName(name);
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password)); // Encrypt the password
        newUser.setRole(role);

        // Save to database and return the saved user
        return appUserRepository.save(newUser);
    }

    /**
     * Updates an existing user's information.
     * Only checks for duplicate username if username is being changed.
     */
    public AppUser updateUser(Long id, String name, String username, String role) {
        // Get the existing user from database
        AppUser user = getUserById(id);

        // Only check for duplicate username if it's being changed
        if (!user.getUsername().equals(username) &&
                appUserRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists!");
        }

        // Update user fields
        user.setName(name);
        user.setUsername(username);
        user.setRole(role);

        // Save updated user to database
        return appUserRepository.save(user);
    }

    public void deleteUser(Long id) {
        appUserRepository.deleteById(id);
    }
}