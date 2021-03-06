package com.bmxstore.grind_store.api_controller;

import com.bmxstore.grind_store.dto.ServerResponseDTO;
import com.bmxstore.grind_store.dto.category.CategoryRequest;
import com.bmxstore.grind_store.dto.category.CategoryResponse;
import com.bmxstore.grind_store.service.CategoryService;
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
    public Page<CategoryResponse> getCategoriesWithPaginationAndSort(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "5") int pageSize,
            @PathVariable String field) {
        return categoryService.findCategoriesWithPaginationAndSorting(pageNumber, pageSize, field);
    }

    @Operation(summary = "Add new category")
    @PostMapping("/add")
    public ResponseEntity<ServerResponseDTO> addCategory(@RequestBody CategoryRequest newCategory) {
        return this.categoryService.addCategory(newCategory);
    }

    @Operation(summary = "Update category description or image URL")
    @PostMapping("/update")
    public ResponseEntity<ServerResponseDTO> updateCategory(@RequestBody CategoryRequest updatedCategory) {
        return this.categoryService.updateCategory(updatedCategory);
    }

    @Operation(summary = "Delete category. Category should be empty")
    @DeleteMapping("/delete/{title}")
    public ResponseEntity<ServerResponseDTO> deleteCategory(@PathVariable String title) {
        return this.categoryService.deleteCategory(title);
    }
}

