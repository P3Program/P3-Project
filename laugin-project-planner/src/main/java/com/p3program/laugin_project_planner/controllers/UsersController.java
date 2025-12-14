package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.services.UserService;
import com.p3program.laugin_project_planner.users.AppUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String users(Model model) {
        List<AppUser> users = userService.getAllUsers();
        model.addAttribute("users", users);
        model.addAttribute("activePage", "users");
        return "users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/create")
    public String createUser(@RequestParam String name,
                             @RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String role,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(name, username, password, role);
            redirectAttributes.addFlashAttribute("message", "User created successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        AppUser user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("activePage", "users");
        return "edit-user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id,
                           @RequestParam String name,
                           @RequestParam String username,
                           @RequestParam String role,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, name, username, role);
            redirectAttributes.addFlashAttribute("message", "User updated successfully!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/users/edit/" + id;
        }
        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Long userId,
                             RedirectAttributes redirectAttributes) {
        userService.deleteUser(userId);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/users";
    }
}