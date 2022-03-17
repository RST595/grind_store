package com.bmxstore.grind_store.web;

import com.bmxstore.grind_store.db.entity.CategoryEntity;
import com.bmxstore.grind_store.db.repository.CategoryRepo;
import com.bmxstore.grind_store.dto.category.CategoryRequest;
import com.bmxstore.grind_store.valid_object.ReturnValidObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
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

    @BeforeEach
    void clearRepo() {
        categoryRepo.deleteAll();
    }

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
        assertTrue(categoryRepo.findAll().stream().anyMatch(category ->
                category.getTitle().equals("stem")));
    }

    @Test
    void addSameCategoryTwiceAndExpectFail() throws Exception {
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        this.mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("stem", "To fix bar", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addCategoryWithEmptyTitleAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("", "To fix bar", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addCategoryWithTitleFromSpacesAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("    ", "To fix bar", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void updateCategoryInfoAndExpectOk() throws Exception {
        categoryRepo.save(ReturnValidObject.getValidCategory());
        this.mockMvc.perform(post("/category/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("stem", "New description", "    "))))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        assertTrue(categoryRepo.findAll().stream().anyMatch(category ->
                category.getDescription().equals("New description")));
    }

    @Test
    void updateCategoryWhichNotExist() throws Exception {
        this.mockMvc.perform(post("/category/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("stem2", "To fix bar", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteCategoryAndExpectOk() throws Exception {
        categoryRepo.save(ReturnValidObject.getValidCategory());
        this.mockMvc.perform(delete("/category/delete/{title}", "stem")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        assertFalse(categoryRepo.findAll().stream().anyMatch(category ->
                category.getTitle().equals("stem")));
    }

    @Test
    void deleteCategoryWhichNotExist() throws Exception {
        this.mockMvc.perform(delete("/category/delete/{title}", "stem")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
