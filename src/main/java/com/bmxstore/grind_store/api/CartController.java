package com.bmxstore.grind_store.api;

import com.bmxstore.grind_store.response_api.ResponseApi;
import com.bmxstore.grind_store.service.CartService;
import com.bmxstore.grind_store.dto.cart.AddToCartRequest;
import com.bmxstore.grind_store.dto.cart.CartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Shopping Cart", description = "Add item to cart, update, delete.")
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "Show all items in user cart")
    @GetMapping("/list")
    public List<CartResponse> getUserCartItems(@RequestParam Long userId) {
        return this.cartService.getUserCartItems(userId);
    }

    @Operation(summary = "Add items to user cart")
    @PostMapping("/add")
    public ResponseEntity<ResponseApi> addToCart(@RequestBody AddToCartRequest addToCartRequest,
                                                 @RequestParam Long userId) {
        return this.cartService.addToCart(addToCartRequest, userId);
    }

    @Operation(summary = "Remove items from user cart")
    @DeleteMapping("/remove")
    public ResponseEntity<ResponseApi> removeFromCart(@RequestParam Long cartId,
                                                 @RequestParam Long userId) {
        return this.cartService.removeFromCart(cartId, userId);
    }

    @Operation(summary = "Update quantity of item in user cart")
    @PutMapping("/update/{quantity}")
    public ResponseEntity<ResponseApi> updateItemQuantity(@PathVariable int quantity,
                                                      @RequestParam Long cartId) {
        return this.cartService.updateItemQuantity(cartId, quantity);
    }

}

