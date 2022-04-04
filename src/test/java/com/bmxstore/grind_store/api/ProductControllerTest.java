package com.bmxstore.grind_store.api;

import com.bmxstore.grind_store.db.entity.CategoryEntity;
import com.bmxstore.grind_store.db.entity.product.ProductEntity;
import com.bmxstore.grind_store.db.repository.*;
import com.bmxstore.grind_store.db.entity.product.ProductColor;
import com.bmxstore.grind_store.dto.product.ProductRequest;
import com.bmxstore.grind_store.valid_object.ReturnValidObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
    void cleanRepo() {
        orderRepo.deleteAll();
        cartRepo.deleteAll();
        productRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void getAllProducts() throws Exception {
        this.mockMvc.perform(get("/product/list")
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void addProductAndExpectOk() throws Exception {
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Elementary V3",
                                "PCODE123", "stem.jpg", 5000, 250,
                                "To fix bar", ProductColor.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().isCreated());
        assertTrue(productRepo.findAll().stream().anyMatch(product ->
                product.getProductCode().equals("PCODE123")));
    }

    @Test
    void addProductWithSameCodeTwiceAndExpectFail() throws Exception {
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity newProduct = ReturnValidObject.getValidProduct();
        newProduct.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(newProduct);
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Boss V2",
                                "PCODE123", "stemNew.jpg", 6000, 312,
                                "To fix bar", ProductColor.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void addProductWithEmptyNameAndExpectFail() throws Exception {
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("",
                                "PCODE123", "stem.jpg", 5000, 250,
                                "To fix bar", ProductColor.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void addProductWithNameFromSpacesAndExpectFail() throws Exception {
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("    ",
                                "PCODE123", "stem.jpg", 5000, 250,
                                "To fix bar", ProductColor.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void addProductWithEmptyCodeAndExpectFail() throws Exception {
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Broc",
                                "", "stem.jpg", 5000, 250,
                                "To fix bar", ProductColor.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void addProductWithCodeFromSpacesAndExpectFail() throws Exception {
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Broc",
                                "  ", "stem.jpg", 5000, 250,
                                "To fix bar", ProductColor.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void updateProductInfoAndExpectOk() throws Exception {
        ProductEntity newProduct = ReturnValidObject.getValidProduct();
        CategoryEntity newCategory = ReturnValidObject.getValidCategory();
        newCategory.setProducts(Arrays.asList(newProduct));
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        newProduct.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(newProduct);
        this.mockMvc.perform(post("/product/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Boss",
                                "UPDATED", "stem6.jpg", 7000, 350,
                                null, ProductColor.BLACK, "stem" )))
                        .param("productId", String.valueOf(productRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().isOk());
        assertTrue(productRepo.findAll().stream().anyMatch(product ->
                product.getProductCode().equals("UPDATED") && product.getDescription().equals("To fix bar")
                        && product.getName().equals("Odyssey Boss")));
    }

    @Test
    void updateProductWhichNotExist() throws Exception {
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity newProduct = ReturnValidObject.getValidProduct();
        newProduct.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(newProduct);
        this.mockMvc.perform(post("/product/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Boss",
                                "UPDATED", "stem6.jpg", 7000, 350,
                                "To fix bar", ProductColor.BLACK, "stem" )))
                        .param("productId", String.valueOf(productRepo.findAll().get(0).getId() + 1)))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void deleteProductAndExpectOk() throws Exception {
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity newProduct = ReturnValidObject.getValidProduct();
        newProduct.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(newProduct);
        this.mockMvc.perform(delete("/product/delete/{productId}",
                Long.toString(productRepo.findAll().get(0).getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        assertFalse(productRepo.findAll().stream().anyMatch(product ->
                product.getProductCode().equals("PCODE123")));
    }

    @Test
    void deleteProductWhichNotExist() throws Exception {
        this.mockMvc.perform(delete("/product/delete/{productId}",
                        "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
