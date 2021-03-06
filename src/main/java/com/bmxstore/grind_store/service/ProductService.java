package com.bmxstore.grind_store.service;

import com.bmxstore.grind_store.exception_handler.ErrorMessage;
import com.bmxstore.grind_store.exception_handler.ServiceError;
import com.bmxstore.grind_store.dto.ServerResponseDTO;
import com.bmxstore.grind_store.data.entity.CategoryEntity;
import com.bmxstore.grind_store.data.entity.product.ProductEntity;
import com.bmxstore.grind_store.data.repository.CategoryRepo;
import com.bmxstore.grind_store.data.repository.ProductRepo;
import com.bmxstore.grind_store.dto.product.ProductRequest;
import com.bmxstore.grind_store.dto.product.ProductResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

    public Set<ProductResponse> getAllProducts() {
        Set<ProductResponse> allProducts = new HashSet<>();
        for (ProductEntity product : productRepo.findAll()) {
            ProductResponse productResponse = objectMapper.convertValue(product, ProductResponse.class);
            productResponse.setCategoryTitle(product.getCategoryEntity().getTitle());
            allProducts.add(productResponse);
        }
        return allProducts;
    }

    public ResponseEntity<ServerResponseDTO> addProduct(ProductRequest newProduct) {
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
            }
        }
        if(productEntity.getCategoryEntity() == null){
            throw new ServiceError(HttpStatus.BAD_REQUEST, ErrorMessage.valueOf("CATEGORY_NOT_EXIST"));
        }
        productRepo.save(productEntity);
        return new ResponseEntity<>(new ServerResponseDTO(true, "product added"), HttpStatus.CREATED);
    }

    public ResponseEntity<ServerResponseDTO> updateProduct(ProductRequest updatedProduct, Long productId) {
        Optional<ProductEntity> productById = productRepo.findById(productId);
        ProductEntity oldProduct = productById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("PRODUCT_NOT_EXIST")));
        ProductEntity newProduct = objectMapper.convertValue(updatedProduct, ProductEntity.class);
        if(updatedProduct.getCategoryTitle().replace(" ", "").isEmpty() ||
                categoryRepo.findByTitle(updatedProduct.getCategoryTitle()) == null){
            newProduct.setCategoryEntity(oldProduct.getCategoryEntity());
        } else {
            newProduct.setCategoryEntity(categoryRepo.findByTitle(updatedProduct.getCategoryTitle()));
        }
        try {
            oldProduct = objectMapper.updateValue(oldProduct, newProduct);
        }catch (JsonMappingException e){
            throw new ServiceError(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.valueOf("SERVER_ERROR"));
        }
        oldProduct.setCategoryEntity(newProduct.getCategoryEntity());
        productRepo.save(oldProduct);
        return new ResponseEntity<>(new ServerResponseDTO(true, "product updated"), HttpStatus.OK);
    }


    public ResponseEntity<ServerResponseDTO> deleteProduct(Long productId) {
        for(ProductEntity product : productRepo.findAll()){
            if(product.getId().equals(productId)){
                productRepo.deleteById(productId);
                return new ResponseEntity<>(new ServerResponseDTO(true, "product deleted"), HttpStatus.OK);
            }
        }
        throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("NOT_FOUND"));
    }
}
