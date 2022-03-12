package com.bmxstore.grindStore.web;

import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.Service.OrderService;
import com.bmxstore.grindStore.dto.Enums.OrderStatus;
import com.bmxstore.grindStore.dto.Order.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<ResponseApi> createOrder(@RequestParam Long userId) {
        return this.orderService.createOrder(userId);
    }

    @PostMapping("/payment")
    public ResponseEntity<ResponseApi> payForOrder(@RequestParam Long orderId,
                                                   @RequestBody PaymentRequest card) {
        return this.orderService.payForOrder(orderId, card);
    }

    @PostMapping("/changeStatus")
    public ResponseEntity<ResponseApi> changeOrderStatus(@RequestParam OrderStatus status,
                                                         @RequestParam Long orderId) {
        return this.orderService.changeOrderStatus(status, orderId);
    }
}
