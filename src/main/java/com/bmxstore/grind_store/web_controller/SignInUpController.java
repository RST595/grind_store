package com.bmxstore.grind_store.web_controller;

import com.bmxstore.grind_store.dto.user.AdminRequest;
import com.bmxstore.grind_store.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class SignInUpController {

    @Value("${variables.keyWord}")
    private String keyWord;

    @Autowired
    private UserService userService;

    @GetMapping("login")
    public String getLogin() {
        return "login";
    }

    @GetMapping(value = "/admin/registration")
    public String registration(Model model) {
        model.addAttribute("RegistrationForm", new AdminRequest());

        return "registration";
    }

    @PostMapping(value = "/admin/registration")
    public String registration(@ModelAttribute("RegistrationForm") AdminRequest admin, BindingResult result, Model model) {
        if (result.hasErrors() || !admin.getKeyWord().equals(keyWord)) {
            return "registration_error";
        }
        return userService.addAdmin(admin);
    }

}
