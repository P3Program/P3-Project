package com.p3program.laugin_project_planner.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/error/access-denied";
    }
}
