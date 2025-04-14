package com.surpimi4.crud.controller;




import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;



@Controller
@RequestMapping("/admin")

public class AdminController {


    @GetMapping
    public String getUserList() {
        return "user";
    }


    @GetMapping("/add")
    public String addUserForm() {
        return "addUser";
    }

}
