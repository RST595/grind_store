package com.bmxstore.grind_store.dto.order;

import com.bmxstore.grind_store.data.entity.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private OrderStatus status;
    private LocalDate createdDate;
    private String deliveryAddress;
    private Double totalPrice;
}