package com.bmxstore.grind_store.api;

import com.bmxstore.grind_store.dto.order.OrderResponse;
import com.bmxstore.grind_store.response_api.ResponseApi;
import com.bmxstore.grind_store.service.OrderService;
import com.bmxstore.grind_store.db.entity.order.OrderStatus;
import com.bmxstore.grind_store.dto.order.PaymentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Orders", description = "Create new order, make payment for order, change status of orders.")
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "Show all user orders")
    @GetMapping("/list")
    public List<OrderResponse> getAllListOfUserOrders(@RequestParam Long userId) {
        return this.orderService.getListOfUserOrders(userId);
    }

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
