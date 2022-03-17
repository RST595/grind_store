package com.bmxstore.grind_store.web;

import com.bmxstore.grind_store.response_api.ResponseApi;
import com.bmxstore.grind_store.service.ProductService;
import com.bmxstore.grind_store.dto.product.ProductRequest;
import com.bmxstore.grind_store.dto.product.ProductResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@Tag(name = "Products", description = "Show, add, update or delete products.")
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "Show all products in the shop")
    @GetMapping("/list")
    public Set<ProductResponse> getAllProducts() {
        return this.productService.getAllProducts();
    }

    @Operation(summary = "Add new product to base")
    @PostMapping("/add")
    public ResponseEntity<ResponseApi> addProduct(@RequestBody ProductRequest newProduct) {
        return this.productService.addProduct(newProduct);
    }

    @Operation(summary = "Update product info")
    @PostMapping("/update")
    public ResponseEntity<ResponseApi> updateCategory(@RequestBody ProductRequest updatedProduct,
                                                      @RequestParam Long productId) throws JsonMappingException {
        return this.productService.updateProduct(updatedProduct, productId);
    }

    @Operation(summary = "Delete product from base")
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ResponseApi> deleteProduct(@PathVariable Long productId) {
        return this.productService.deleteProduct(productId);
    }
}

