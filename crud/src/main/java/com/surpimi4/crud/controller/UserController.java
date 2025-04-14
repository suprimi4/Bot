package com.surpimi4.crud.controller;

import com.surpimi4.crud.dto.UserDTO;
import com.surpimi4.crud.service.UserServiceImpl;
import jakarta.validation.Valid;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;

    }

    @GetMapping
    public String getCurrentUserProfile() {
        return "currentUser";
    }

    @GetMapping("/register")
    public String getRegisterForm() {
        return "registerForm";
    }


    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid UserDTO user) {

        try {
            userService.registerUser(user);
        } catch (IllegalArgumentException e) {
            return "registerForm";
        }

        return "redirect:/login";
    }
}
