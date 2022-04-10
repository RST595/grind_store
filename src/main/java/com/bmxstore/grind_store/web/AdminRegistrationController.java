package com.bmxstore.grind_store.web;

import com.bmxstore.grind_store.db.entity.CategoryEntity;
import com.bmxstore.grind_store.db.repository.CategoryRepo;
import com.bmxstore.grind_store.dto.category.WebCategoriesDto;
import com.bmxstore.grind_store.dto.user.AdminRequest;
import com.bmxstore.grind_store.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class AdminRegistrationController {

    @Value("${variables.keyWord}")
    private String keyWord;

    @Autowired
    private UserService userService;

    @Autowired
    CategoryRepo categoryRepo;

    @GetMapping(value = "/registration")
    public String registration(Model model) {
        model.addAttribute("RegistrationForm", new AdminRequest());

        return "registration";
    }

    @PostMapping(value = "/registration")
    public String registration(@ModelAttribute("RegistrationForm") AdminRequest admin, BindingResult result, Model model) {
        if (result.hasErrors() || !admin.getKeyWord().equals(keyWord)) {
            return "registration_error";
        }
        userService.addAdmin(admin);
        return "redirect:/login";
    }

    @GetMapping("/allCategories")
    public String showAllCategories(Model model) {
        model.addAttribute("categories", categoryRepo.findAll());
        return "all_categories";
    }

    @GetMapping(value = "/edit")
    public String showEditForm(Model model) {
        List<CategoryEntity> categories = new ArrayList<>();
        categoryRepo.findAll()
                .iterator()
                .forEachRemaining(categories::add);

        model.addAttribute("form", new WebCategoriesDto(categories));

        return "edit_categories";
    }

    @PostMapping(value = "/save")
    public String saveBooks(@ModelAttribute WebCategoriesDto form, Model model) {
        categoryRepo.saveAll(form.getCategories());

        model.addAttribute("categories", categoryRepo.findAll());

        return "redirect:/allCategories";
    }
}

