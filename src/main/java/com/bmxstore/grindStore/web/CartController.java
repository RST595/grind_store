package com.bmxstore.grindStore.web;

import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.Service.CartService;
import com.bmxstore.grindStore.dto.Cart.AddToCartRequest;
import com.bmxstore.grindStore.dto.Cart.CartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/list")
    public List<CartResponse> getUserCartItems(@RequestParam Long userId) {
        return this.cartService.getUserCartItems(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseApi> addToCart(@RequestBody AddToCartRequest addToCartRequest,
                                                 @RequestParam Long userId) {
        return this.cartService.addToCart(addToCartRequest, userId);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ResponseApi> removeFromCart(@RequestParam Long cartId,
                                                 @RequestParam Long userId) {
        return this.cartService.removeFromCart(cartId, userId);
    }

    @PutMapping("/update/{quantity}")
    public ResponseEntity<ResponseApi> updateItemQuantity(@PathVariable int quantity,
                                                      @RequestParam Long cartId) {
        return this.cartService.updateItemQuantity(cartId, quantity);
    }
}

