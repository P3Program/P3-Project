package com.p3program.laugin_project_planner.services;

import com.p3program.laugin_project_planner.repositories.AppUserRepository;
import com.p3program.laugin_project_planner.users.AppUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public AppUser createUser(String name, String username, String password, String role) {
        if (appUserRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists!");
        }

        AppUser newUser = new AppUser();
        newUser.setName(name);
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole(role);

        return appUserRepository.save(newUser);
    }

    public AppUser updateUser(Long id, String name, String username, String role) {
        AppUser user = getUserById(id);

        if (!user.getUsername().equals(username) &&
                appUserRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists!");
        }

        user.setName(name);
        user.setUsername(username);
        user.setRole(role);

        return appUserRepository.save(user);
    }

    public void deleteUser(Long id) {
        appUserRepository.deleteById(id);
    }
}