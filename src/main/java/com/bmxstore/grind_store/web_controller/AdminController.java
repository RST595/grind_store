package com.bmxstore.grind_store.web_controller;

import com.bmxstore.grind_store.data.entity.CategoryEntity;
import com.bmxstore.grind_store.data.repository.CategoryRepo;
import com.bmxstore.grind_store.dto.category.CategoryRequest;
import com.bmxstore.grind_store.dto.category.WebCategoriesDto;
import com.bmxstore.grind_store.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class AdminController {

    public static final String REDIRECT_TO_ALL_CATEGORISE = "redirect:/categories/all";


    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("admin/panel")
    public String getAdminPanel() {
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
    public String saveCategoryChange(@ModelAttribute WebCategoriesDto form, Model model) {
        categoryRepo.saveAll(form.getCategories());

        model.addAttribute("categories", categoryRepo.findAll());

        return REDIRECT_TO_ALL_CATEGORISE;
    }

    @GetMapping(value = "/categories/add")
    public String addNewCategory(Model model) {
        model.addAttribute("CategoryForm", new CategoryRequest());

        return "add_category";
    }

    @PostMapping(value = "/categories/add")
    public String addNewCategory(@ModelAttribute("CategoryForm") CategoryRequest category) {
        categoryService.addCategory(category);
        return REDIRECT_TO_ALL_CATEGORISE;
    }

    @GetMapping(value = "/categories/delete")
    public String deleteCategory() {
        return "delete_category";
    }

    @PostMapping(value = "/categories/delete")
    public String deleteCategory(@ModelAttribute("title") String title) {
        categoryService.deleteCategory(title);
        return REDIRECT_TO_ALL_CATEGORISE;
    }
}

