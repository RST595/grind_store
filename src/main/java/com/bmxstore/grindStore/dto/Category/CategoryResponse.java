package com.bmxstore.grindStore.dto.Category;

import com.bmxstore.grindStore.db.Entity.ProductEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private Long id;
    private String title;
    private String description;
    private String picUrl;
}