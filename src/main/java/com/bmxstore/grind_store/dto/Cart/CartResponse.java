package com.bmxstore.grind_store.dto.Cart;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private LocalDate createDate;
    private String product;
    private int quantity;

}
