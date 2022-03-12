package com.bmxstore.grindStore.web;

import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.Service.ProductService;
import com.bmxstore.grindStore.dto.Product.ProductRequest;
import com.bmxstore.grindStore.dto.Product.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public Set<ProductResponse> getAllProducts() {
        return this.productService.getAllProducts();
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseApi> addProduct(@RequestBody ProductRequest newProduct) {
        return this.productService.addProduct(newProduct);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseApi> updateCategory(@RequestBody ProductRequest updatedProduct,
                                                      @RequestParam Long productId) {
        return this.productService.updateProduct(updatedProduct, productId);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<ResponseApi> deleteProduct(@PathVariable Long productId) {
        return this.productService.deleteProduct(productId);
    }
}

