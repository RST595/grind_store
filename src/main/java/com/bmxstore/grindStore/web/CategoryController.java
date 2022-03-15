package com.bmxstore.grindStore.web;

import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.Service.CategoryService;
import com.bmxstore.grindStore.db.Entity.CategoryEntity;
import com.bmxstore.grindStore.dto.Category.CategoryRequest;
import com.bmxstore.grindStore.dto.Category.CategoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Categories", description = "Add category for items, update or delete.")
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Show all available categories")
    @GetMapping("/list")
    public List<CategoryResponse> getAllCategories() {
        return this.categoryService.getAllCategories();
    }

    @Operation(summary = "Show all available categories with sorting")
    @GetMapping("/sort/{field}")
    public Page<CategoryEntity> getCategoriesWithPaginationAndSort(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") int pageSize,
            @PathVariable String field) {
        return categoryService.findCategoriesWithPaginationAndSorting(pageNumber, pageSize, field);
    }

    @Operation(summary = "Add new category")
    @PostMapping("/add")
    public ResponseEntity<ResponseApi> addCategory(@RequestBody CategoryRequest newCategory) {
        return this.categoryService.addCategory(newCategory);
    }

    @Operation(summary = "Update category description or image URL")
    @PostMapping("/update")
    public ResponseEntity<ResponseApi> updateCategory(@RequestBody CategoryRequest updatedCategory) {
        return this.categoryService.updateCategory(updatedCategory);
    }

    @Operation(summary = "Delete category. Category should be empty")
    @DeleteMapping("/delete/{title}")
    public ResponseEntity<ResponseApi> deleteCategory(@PathVariable String title) {
        return this.categoryService.deleteCategory(title);
    }
}

