package com.bmxstore.grindStore.web;

import com.bmxstore.grindStore.db.Entity.CategoryEntity;
import com.bmxstore.grindStore.db.Repository.CategoryRepo;
import com.bmxstore.grindStore.db.Repository.UserRepo;
import com.bmxstore.grindStore.dto.Cart.AddToCartRequest;
import com.bmxstore.grindStore.dto.Category.CategoryRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepo userRepo;

    @Test
    void getAllCategories() throws Exception {
        this.mockMvc.perform(get("/category/list")
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void addCategoryAndExpectOk() throws Exception {
        this.mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("stem", "To fix bar", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        CategoryEntity testEntity = new CategoryEntity();
        for(CategoryEntity category : categoryRepo.findAll()){
            if(category.getTitle().equals("stem")){
                testEntity = category;
            }
        }
        assert(testEntity.getTitle().equals("stem"));
    }

    @Test
    void addSameCategoryTwiceAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("stem", "To fix bar", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("stem", "To fix bar", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateCategory() throws Exception {
        this.mockMvc.perform(post("/category/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("stem2", "To fix bar", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteCategory() throws Exception {
        this.mockMvc.perform(delete("/category/delete{title}", "stem")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
