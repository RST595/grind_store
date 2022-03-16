package com.bmxstore.grind_store.web;

import com.bmxstore.grind_store.ExHandler.ErrorMessage;
import com.bmxstore.grind_store.ExHandler.ServiceError;
import com.bmxstore.grind_store.db.Entity.CategoryEntity;
import com.bmxstore.grind_store.db.Entity.ProductEntity;
import com.bmxstore.grind_store.db.Repository.*;
import com.bmxstore.grind_store.dto.Enums.Color;
import com.bmxstore.grind_store.dto.Product.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

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

    @BeforeEach
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
        categoryRepo.save(new CategoryEntity(1L, "stem",
                "To fix bar", "stem.jpg", new ArrayList<>()));
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Elementary V3",
                                "PCODE123", "stem.jpg", 5000, 250,
                                "To fix bar", Color.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        ProductEntity testEntity = new ProductEntity();
        for(ProductEntity product : productRepo.findAll()){
            if(product.getProductCode().equals("PCODE123")){
                testEntity = product;

            }
        }
        assert(testEntity.getProductCode().equals("PCODE123"));
    }

    @Test
    void addProductWithSameCodeTwiceAndExpectFail() throws Exception {
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar",
                "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Boss V2",
                                "PCODE123", "stemNew.jpg", 6000, 312,
                                "To fix bar", Color.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addProductWithEmptyNameAndExpectFail() throws Exception {
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("",
                                "PCODE123", "stem.jpg", 5000, 250,
                                "To fix bar", Color.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addProductWithNameFromSpacesAndExpectFail() throws Exception {
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("    ",
                                "PCODE123", "stem.jpg", 5000, 250,
                                "To fix bar", Color.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addProductWithEmptyCodeAndExpectFail() throws Exception {
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Broc",
                                "", "stem.jpg", 5000, 250,
                                "To fix bar", Color.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addProductWithCodeFromSpacesAndExpectFail() throws Exception {
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        this.mockMvc.perform(post("/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Broc",
                                "  ", "stem.jpg", 5000, 250,
                                "To fix bar", Color.BLACK, "stem" ))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateProductInfoAndExpectOk() throws Exception {
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        this.mockMvc.perform(post("/product/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Boss",
                                "UPDATED", "stem6.jpg", 7000, 350,
                                "     ", Color.BLACK, "stem" )))
                        .param("productId", String.valueOf(productRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        ProductEntity testEntity = new ProductEntity();
        for(ProductEntity product : productRepo.findAll()){
            if(product.getProductCode().equals("UPDATED") && product.getDescription().equals("To fix bar")
             && product.getName().equals("Odyssey Boss")){
                testEntity = product;
            }
        }
        assert(testEntity.getProductCode().equals("UPDATED"));
    }

    @Test
    void updateProductWhichNotExist() throws Exception {
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        this.mockMvc.perform(post("/product/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProductRequest("Odyssey Boss",
                                "UPDATED", "stem6.jpg", 7000, 350,
                                "To fix bar", Color.BLACK, "stem" )))
                        .param("productId", String.valueOf(productRepo.findAll().get(0).getId() + 1)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteProductAndExpectOk() throws Exception {
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        this.mockMvc.perform(delete("/product/delete/{productId}",
                Long.toString(productRepo.findAll().get(0).getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        for(ProductEntity product : productRepo.findAll()){
            if(product.getProductCode().equals("PCODE123")){
                throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("NOT_EMPTY"));
            }
        }
    }

    @Test
    void deleteProductWhichNotExist() throws Exception {
        this.mockMvc.perform(delete("/product/delete/{productId}",
                        "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
