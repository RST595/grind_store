package com.bmxstore.grind_store.web;

import com.bmxstore.grind_store.database.entity.CategoryEntity;
import com.bmxstore.grind_store.database.repository.CategoryRepo;
import com.bmxstore.grind_store.dto.category.CategoryRequest;
import com.bmxstore.grind_store.dto.category.WebCategoriesDto;
import com.bmxstore.grind_store.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class AdminController {



    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("admin/panel")
    public String getCourses() {
        return "admin_panel";
    }

    @GetMapping("/categories/all")
    public String showAllCategories(Model model) {
        model.addAttribute("categories", categoryRepo.findAll());
        return "all_categories";
    }

    @GetMapping(value = "/categories/edit")
    public String showEditForm(Model model) {
        List<CategoryEntity> categories = new ArrayList<>();
        categoryRepo.findAll()
                .iterator()
                .forEachRemaining(categories::add);

        model.addAttribute("form", new WebCategoriesDto(categories));

        return "edit_categories";
    }

    @PostMapping(value = "/categories/save")
    public String saveBooks(@ModelAttribute WebCategoriesDto form, Model model) {
        categoryRepo.saveAll(form.getCategories());

        model.addAttribute("categories", categoryRepo.findAll());

        return "redirect:/categories/all";
    }

    @GetMapping(value = "/categories/add")
    public String registration(Model model) {
        model.addAttribute("CategoryForm", new CategoryRequest());

        return "add_category";
    }

    @PostMapping(value = "/categories/add")
    public String registration(@ModelAttribute("CategoryForm") CategoryRequest category, BindingResult result, Model model) {
        categoryService.addCategory(category);
        return "redirect:/categories/all";
    }
}

