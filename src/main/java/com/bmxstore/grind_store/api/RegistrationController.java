package com.bmxstore.grind_store.api;

import com.bmxstore.grind_store.dto.user.UserRequest;
import com.bmxstore.grind_store.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@Tag(name = "Registration", description = "Register to application")
@RequestMapping("/")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/registration")
    public String registration(Model model) {
        model.addAttribute("RegistrationForm", new UserRequest());

        return "registration";
    }

    @PostMapping(value = "/registration")
    public String registration(@ModelAttribute("RegistrationForm") UserRequest user, Model model) {

        //TODO: add UserRequest validation

        userService.addUser(user);

        //securityService.autoLogin(userForm.getUsername(), userForm.getConfirmPassword());

        return "redirect:/login";
    }
}
