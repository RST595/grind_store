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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;

    @Autowired
    ObjectMapper objectMapper;
    // TODO: 16.03.2022 use constructor and lombok instead
    @Autowired
    CategoryRepo categoryRepo;


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
        if(newProduct.getName().replace(" ", "").isEmpty() ||
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

    // TODO: 16.03.2022 ask Andrei how to do it properly
    public ResponseEntity<ResponseApi> updateProduct(ProductRequest updatedProduct, Long productId) {
        for (ProductEntity product : productRepo.findAll()) {
            if (product.getId().equals(productId)) {
                if(!updatedProduct.getName().replace(" ", "").isEmpty()) {
                    product.setName(updatedProduct.getName());
                }
                if(!updatedProduct.getProductCode().replace(" ", "").isEmpty()) {
                    product.setProductCode(updatedProduct.getProductCode());
                }
                if(!updatedProduct.getDescription().replace(" ", "").isEmpty()) {
                    product.setDescription((updatedProduct.getDescription()));
                }
                if(!updatedProduct.getImageURL().replace(" ", "").isEmpty()) {
                    product.setImageURL(updatedProduct.getImageURL());
                }
//                if(updatedProduct.getCategoryTitle().replace(" ", "").isEmpty()){
//                    product.setCategoryEntity(categoryRepo.findByTitle(updatedProduct.getCategoryTitle()));
//                }
                if(updatedProduct.getPrice() != 0){
                    product.setPrice(updatedProduct.getPrice());
                }
                if(updatedProduct.getWeight() != 0){
                    product.setWeight(updatedProduct.getWeight());
                }
                productRepo.save(product);
                return new ResponseEntity<>(new ResponseApi(true, "product updated"), HttpStatus.OK);
            }
        }
        throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("PRODUCT_NOT_EXIST"));

//        Optional<ProductEntity> productById = productRepo.findById(productId);
//        ProductEntity oldProduct = productById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("PRODUCT_NOT_EXIST")));
//        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
//        ProductEntity newProduct = objectMapper.convertValue(updatedProduct, ProductEntity.class);
//        newProduct = objectMapper.updateValue(newProduct,oldProduct);
//        productRepo.save(newProduct);
//        return new ResponseEntity<>(new ResponseApi(true, "product updated"), HttpStatus.OK);
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
