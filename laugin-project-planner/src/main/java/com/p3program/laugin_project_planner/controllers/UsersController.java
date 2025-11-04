package com.p3program.laugin_project_planner.controllers;

import com.p3program.laugin_project_planner.users.AppUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsersController {

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("user", new AppUser());
        return "users";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute AppUser appUser, Model model) {
        model.addAttribute("user", appUser);
        return "usercreated";

    }
}
