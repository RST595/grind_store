package com.bmxstore.grind_store.service;

import com.bmxstore.grind_store.ex_handler.ErrorMessage;
import com.bmxstore.grind_store.ex_handler.ServiceError;
import com.bmxstore.grind_store.response_api.ResponseApi;
import com.bmxstore.grind_store.db.entity.CategoryEntity;
import com.bmxstore.grind_store.db.entity.ProductEntity;
import com.bmxstore.grind_store.db.repository.CategoryRepo;
import com.bmxstore.grind_store.db.repository.ProductRepo;
import com.bmxstore.grind_store.dto.product.ProductRequest;
import com.bmxstore.grind_store.dto.product.ProductResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;

    private final ObjectMapper objectMapper;
    private final CategoryRepo categoryRepo;

    //FIXed
    // TODO: 16.03.2022 use constructor and lombok instead



    public Set<ProductResponse> getAllProducts() {
        Set<ProductResponse> allProducts = new HashSet<>();
        for (ProductEntity product : productRepo.findAll()) {
            ProductResponse productResponse = objectMapper.convertValue(product, ProductResponse.class);
            productResponse.setCategoryTitle(product.getCategoryEntity().getTitle());
            allProducts.add(productResponse);
        }
        return allProducts;
    }

    public ResponseEntity<ResponseApi> addProduct(ProductRequest newProduct) {
        if(newProduct.getName() == null || newProduct.getProductCode() == null
                || newProduct.getName().replace(" ", "").isEmpty() ||
                newProduct.getProductCode().replace(" ", "").isEmpty()){
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("PRODUCT_NOT_EXIST"));
        }
        ProductEntity productEntity = objectMapper.convertValue(newProduct, ProductEntity.class);
        for (ProductEntity product : productRepo.findAll()) {
            if (product.getProductCode().equals(newProduct.getProductCode())) {
                throw new ServiceError(HttpStatus.CONFLICT, ErrorMessage.valueOf("DUPLICATED"));
            }
        }
        for(CategoryEntity category : categoryRepo.findAll()) {
            if(category.getTitle().equals(newProduct.getCategoryTitle())) {
                productEntity.setCategoryEntity(category);
                break;
            } else {
                throw new ServiceError(HttpStatus.BAD_REQUEST, ErrorMessage.valueOf("CATEGORY_NOT_EXIST"));
            }
        }
        productRepo.save(productEntity);
        return new ResponseEntity<>(new ResponseApi(true, "product added"), HttpStatus.CREATED);
    }
    //FIXed, but this method doesn't prevent from strings from spaces
    // TODO: 16.03.2022 ask Andrei how to do it properly
    public ResponseEntity<ResponseApi> updateProduct(ProductRequest updatedProduct, Long productId) throws JsonMappingException {
        Optional<ProductEntity> productById = productRepo.findById(productId);
        ProductEntity oldProduct = productById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("PRODUCT_NOT_EXIST")));
        ProductEntity newProduct = objectMapper.convertValue(updatedProduct, ProductEntity.class);
        if(updatedProduct.getCategoryTitle().replace(" ", "").isEmpty()){
            newProduct.setCategoryEntity(oldProduct.getCategoryEntity());
        } else {
            newProduct.setCategoryEntity(categoryRepo.findByTitle(updatedProduct.getCategoryTitle()));
        }
        oldProduct = objectMapper.updateValue(oldProduct, newProduct);
        productRepo.save(oldProduct);
        return new ResponseEntity<>(new ResponseApi(true, "product updated"), HttpStatus.OK);
    }


    public ResponseEntity<ResponseApi> deleteProduct(Long productId) {
        for(ProductEntity product : productRepo.findAll()){
            if(product.getId().equals(productId)){
                productRepo.deleteById(productId);
                return new ResponseEntity<>(new ResponseApi(true, "product deleted"), HttpStatus.OK);
            }
        }
        throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("NOT_FOUND"));
    }
}
