package com.bmxstore.grind_store.dto.product;

import com.bmxstore.grind_store.data.entity.product.ProductColor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    private String name;
    private String productCode;
    private String imageURL;
    private double price;
    private double weight;
    private String description;
    private ProductColor productColor;
    private String categoryTitle;
}
