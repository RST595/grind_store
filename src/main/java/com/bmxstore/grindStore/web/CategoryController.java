package com.bmxstore.grindStore.web;

import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.Service.CategoryService;
import com.bmxstore.grindStore.dto.Category.CategoryRequest;
import com.bmxstore.grindStore.dto.Category.CategoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public List<CategoryResponse> getAllCategories() {
        return this.categoryService.getAllCategories();
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseApi> addCategory(@RequestBody CategoryRequest newCategory) {
        return this.categoryService.addCategory(newCategory);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseApi> updateCategory(@RequestBody CategoryRequest updatedCategory) {
        return this.categoryService.updateCategory(updatedCategory);
    }

    @DeleteMapping("/delete{title}")
    public ResponseEntity<ResponseApi> deleteCategory(@PathVariable String title) {
        return this.categoryService.deleteCategory(title);
    }
}

