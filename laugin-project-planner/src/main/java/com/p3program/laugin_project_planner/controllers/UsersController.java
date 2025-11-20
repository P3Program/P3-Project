package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.repositories.AppUserRepository;
import com.p3program.laugin_project_planner.users.AppUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
public class UsersController {

    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;

    public UsersController(PasswordEncoder passwordEncoder,
                           AppUserRepository appUserRepository) {
        this.passwordEncoder = passwordEncoder;
        this.appUserRepository = appUserRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String users(Model model) {
        List<AppUser> users = appUserRepository.findAll();
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

        if(appUserRepository.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("message", "Username already exists!");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/users";
        }

        String encryptedPassword = passwordEncoder.encode(password);

        AppUser newUser = new AppUser();
        newUser.setName(name);
        newUser.setUsername(username);
        newUser.setPassword(encryptedPassword);
        newUser.setRole(role);

        appUserRepository.save(newUser);
        redirectAttributes.addFlashAttribute("message", "user created successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");

        System.out.println("User succesfully created: " + username);


        System.out.println(name);
        System.out.println(username);
        System.out.println(password);
        System.out.println(encryptedPassword);
        System.out.println(role);

        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
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
                           @RequestParam(required = false) String password,
                           RedirectAttributes redirectAttributes) {

        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.getUsername().equals(username) &&
            appUserRepository.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("message", "Username already exists!");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:users/edit/" + id;
        }

        user.setName(name);
        user.setUsername(username);
        user.setRole(role);

        // Only update password if provided
        // TODO dont think this is the way, keeping it here for now. Would maybe rather set a default PW and let the
        // TODO user change it upon login, and let the user request/set a new password, and not the admin.
        // TODO must create some profile page then... * sigh *...
        /* if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }*/

        appUserRepository.save(user);

        redirectAttributes.addFlashAttribute("message" ,"User updated successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Long userId,
                             RedirectAttributes redirectAttributes) {
        appUserRepository.deleteById(userId);
        redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/users";
    }
}
