package com.bmxstore.grind_store.dto.Order;

import com.bmxstore.grind_store.dto.Enums.Color;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String name;
    private String productCode;
    private String imageURL;
    private double price;
    private double weight;
    private String description;
    private Color color;
    private String categoryTitle;
}