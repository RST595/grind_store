package com.bmxstore.grindStore.Service;

import com.bmxstore.grindStore.ExHandler.ErrorMessage;
import com.bmxstore.grindStore.ExHandler.ServiceError;
import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.db.Entity.CategoryEntity;
import com.bmxstore.grindStore.db.Entity.ProductEntity;
import com.bmxstore.grindStore.db.Repository.CategoryRepo;
import com.bmxstore.grindStore.db.Repository.ProductRepo;
import com.bmxstore.grindStore.dto.Category.CategoryRequest;
import com.bmxstore.grindStore.dto.Category.CategoryResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryService {

    private static final String TYPICAL_ERROR_MESSAGE = "CATEGORY_NOT_EXIST";

    private final CategoryRepo categoryRepo;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProductRepo productRepo;

//    @PostConstruct
//    public void initDB() {
//        List<CategoryEntity> category = IntStream.rangeClosed(5, 205)
//                .mapToObj(i -> new CategoryEntity(i*1L, "title" + i,
//                        "description" + i,
//                        "picURL" + i,
//                        new HashSet<>()))
//                .collect(Collectors.toList());
//        categoryRepo.saveAll(category);
//    }

    public List<CategoryResponse> getAllCategories() {
        List<CategoryResponse> allCategories = new ArrayList<>();
        for (CategoryEntity category : categoryRepo.findAll()) {
            CategoryResponse categoryResponse = objectMapper.convertValue(category, CategoryResponse.class);
            allCategories.add(categoryResponse);
            }
        return allCategories;
    }

    public Page<CategoryResponse> findCategoriesWithPaginationAndSorting(int pageNumber, int pageSize, String field){
        Page<CategoryEntity> page = categoryRepo.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(field)));
        return page.map(category -> objectMapper.convertValue(category, CategoryResponse.class));
    }

    public ResponseEntity<ResponseApi> addCategory(CategoryRequest newCategory) {
        CategoryEntity categoryEntity = objectMapper.convertValue(newCategory, CategoryEntity.class);
        if(categoryEntity.getTitle().replace(" ", "").isEmpty()){
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("CATEGORY_NOT_EXIST"));
        }
        for (CategoryEntity category : categoryRepo.findAll()) {
            if (category.getTitle().equals(newCategory.getTitle())) {
                throw new ServiceError(HttpStatus.CONFLICT, ErrorMessage.valueOf("DUPLICATED"));
            }
        }
        categoryRepo.save(categoryEntity);
        return new ResponseEntity<>(new ResponseApi(true, "category added"), HttpStatus.CREATED);

    }

    public ResponseEntity<ResponseApi> updateCategory(CategoryRequest updatedCategory) {
        for (CategoryEntity category : categoryRepo.findAll()) {
            if (category.getTitle().equals(updatedCategory.getTitle())) {
                if(!updatedCategory.getDescription().replace(" ", "").isEmpty()) {
                    category.setDescription(updatedCategory.getDescription());
                }
                if(!updatedCategory.getPicUrl().replace(" ", "").isEmpty()) {
                    category.setPicUrl(updatedCategory.getPicUrl());
                }
                categoryRepo.save(category);
                return new ResponseEntity<>(new ResponseApi(true, "category updated"), HttpStatus.OK);
            }
        }
        throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf(TYPICAL_ERROR_MESSAGE));
    }


    public ResponseEntity<ResponseApi> deleteCategory(String title) {
        for (CategoryEntity category : categoryRepo.findAll()) {
            if (category.getTitle().equals(title)) {
                for (ProductEntity product : productRepo.findAll()) {
                    if (product.getCategoryEntity().getTitle().equals(category.getTitle())) {
                        throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("NOT_EMPTY"));
                    }
                }
                categoryRepo.deleteById(category.getId());
                return new ResponseEntity<>(new ResponseApi(true, "category deleted"), HttpStatus.OK);
            }
        }
        throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf(TYPICAL_ERROR_MESSAGE));
    }
}

