package com.bmxstore.grind_store.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//@Tag(name = "Login", description = "Login to application")
@RequestMapping("/")
public class LoginController {

    @GetMapping("login")
    public String getLogin() {
        return "login";
    }

    @GetMapping("panel")
    public String getCourses() {
        return "admin_panel";
    }
}
