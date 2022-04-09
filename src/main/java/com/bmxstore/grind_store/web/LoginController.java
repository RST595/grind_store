package com.bmxstore.grind_store.web;

import io.swagger.v3.oas.annotations.tags.Tag;
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
        return "panel";
    }
}
