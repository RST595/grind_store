package com.bmxstore.grind_store.dto.category;

import com.bmxstore.grind_store.db.entity.CategoryEntity;

import java.util.List;

public class WebCategoriesDto {

    private List<CategoryEntity> categories;

    public WebCategoriesDto(List<CategoryEntity> categories) {
        this.categories = categories;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

}
