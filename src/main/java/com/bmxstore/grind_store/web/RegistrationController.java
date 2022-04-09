package com.bmxstore.grind_store.web;

import com.bmxstore.grind_store.db.entity.CategoryEntity;
import com.bmxstore.grind_store.db.entity.user.UserEntity;
import com.bmxstore.grind_store.db.repository.CategoryRepo;
import com.bmxstore.grind_store.dto.category.CategoryResponse;
import com.bmxstore.grind_store.dto.user.UserRequest;
import com.bmxstore.grind_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
//@Tag(name = "Registration", description = "Register to application")
@RequestMapping("/")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    CategoryRepo categoryRepo;

    @GetMapping(value = "/registration")
    public String registration(Model model) {
        model.addAttribute("RegistrationForm", new UserRequest());

        return "registration";
    }

    @PostMapping(value = "/registration")
    public String registration(@ModelAttribute("RegistrationForm") UserRequest user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "regError";
        }
        userService.addUser(user);
        return "redirect:/login";
    }

    //FIXME Correct this method
    @RequestMapping(value = "/category/display")
    public String listOfCategory(Model model){
        List<CategoryEntity> list = new ArrayList<>();
        CategoryEntity ent = new CategoryEntity("stem", "picUrl");
        ent.setId(1L);
        list.add(ent);
        model.addAttribute("categoryDisplay", list);
        return "categoryDisplay";
    }
}
