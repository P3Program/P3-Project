package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.repositories.AppUserRepository;
import com.p3program.laugin_project_planner.users.AppUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/users")
    public String users(Model model) {
        List<AppUser> users = appUserRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("activePage", "users");
        return "users";

    }

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
}
