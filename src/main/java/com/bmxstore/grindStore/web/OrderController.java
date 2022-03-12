package com.bmxstore.grindStore.web;

import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.Service.OrderService;
import com.bmxstore.grindStore.dto.Enums.OrderStatus;
import com.bmxstore.grindStore.dto.Order.PaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Orders", description = "Create new order, make payment for order, change status of orders.")
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "Create new order")
    @PostMapping("/create")
    public ResponseEntity<ResponseApi> createOrder(@RequestParam Long userId) {
        return this.orderService.createOrder(userId);
    }

    @Operation(summary = "Make payment for order")
    @PostMapping("/payment")
    public ResponseEntity<ResponseApi> payForOrder(@RequestParam Long orderId,
                                                   @RequestBody PaymentRequest card) {
        return this.orderService.payForOrder(orderId, card);
    }

    @Operation(summary = "Change status of order")
    @PostMapping("/changeStatus")
    public ResponseEntity<ResponseApi> changeOrderStatus(@RequestParam OrderStatus status,
                                                         @RequestParam Long orderId) {
        return this.orderService.changeOrderStatus(status, orderId);
    }
}
