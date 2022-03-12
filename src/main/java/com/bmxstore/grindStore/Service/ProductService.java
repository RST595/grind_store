package com.bmxstore.grindStore.Service;

import com.bmxstore.grindStore.ExHandler.ErrorMessage;
import com.bmxstore.grindStore.ExHandler.ServiceError;
import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.db.Entity.CategoryEntity;
import com.bmxstore.grindStore.db.Entity.ProductEntity;
import com.bmxstore.grindStore.db.Repository.CategoryRepo;
import com.bmxstore.grindStore.db.Repository.ProductRepo;
import com.bmxstore.grindStore.dto.Product.ProductRequest;
import com.bmxstore.grindStore.dto.Product.ProductResponse;
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

    public ResponseEntity<ResponseApi> updateProduct(ProductRequest updatedProduct, Long productId) {
        for (ProductEntity product : productRepo.findAll()) {
            if (product.getId().equals(productId)) {
                if(!updatedProduct.getName().isEmpty()) {
                    product.setDescription(updatedProduct.getName());
                }
                if(!updatedProduct.getProductCode().isEmpty()) {
                    product.setProductCode(updatedProduct.getProductCode());
                }
                if(!updatedProduct.getDescription().isEmpty()) {
                    product.setDescription((updatedProduct.getDescription()));
                }
                if(!updatedProduct.getImageURL().isEmpty()) {
                    product.setImageURL(updatedProduct.getImageURL());
                }
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
