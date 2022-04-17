package com.bmxstore.grind_store.api_controller;

import com.bmxstore.grind_store.data.entity.CategoryEntity;
import com.bmxstore.grind_store.data.repository.CategoryRepo;
import com.bmxstore.grind_store.dto.category.CategoryRequest;
import com.bmxstore.grind_store.valid_object.ReturnValidObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    ObjectMapper objectMapper;

    @AfterEach
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
                        .content(objectMapper.writeValueAsString(new CategoryRequest("stem", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().isCreated());
        assertTrue(categoryRepo.findAll().stream().anyMatch(category ->
                category.getTitle().equals("stem")));
    }

    @Test
    void addSameCategoryTwiceAndExpectFail() throws Exception {
        categoryRepo.save(new CategoryEntity("stem", "stem.jpg"));
        this.mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("stem", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().isConflict());
        assertEquals(1, categoryRepo.findAll().size());
    }

    @Test
    void addCategoryWithEmptyTitleAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
        assertTrue(categoryRepo.findAll().isEmpty());
    }

    @Test
    void addCategoryWithTitleFromSpacesAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("    ", "stem.jpg"))))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
        assertTrue(categoryRepo.findAll().isEmpty());
    }

    @Test
    void updateCategoryInfoAndExpectOk() throws Exception {
        categoryRepo.save(ReturnValidObject.getValidCategory());
        this.mockMvc.perform(post("/category/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("stem", "NewPic"))))
                .andDo(print())
                .andExpect(status().isOk());
        assertTrue(categoryRepo.findAll().stream().anyMatch(category ->
                category.getPicUrl().equals("NewPic")));
    }

    @Test
    void updateCategoryWhichNotExist() throws Exception {
        categoryRepo.save(ReturnValidObject.getValidCategory());
        this.mockMvc.perform(post("/category/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CategoryRequest("stem2", "NewPic"))))
                .andDo(print())
                .andExpect(status().isNotFound());
        assertTrue(categoryRepo.findAll().stream().anyMatch(category ->
                category.getPicUrl().equals("stem.jpg")));
    }

    @Test
    void deleteCategoryAndExpectOk() throws Exception {
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        this.mockMvc.perform(delete("/category/delete/{title}", "stem")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        assertTrue(categoryRepo.findAll().isEmpty());
        assertFalse(categoryRepo.findAll().stream().anyMatch(category ->
                category.getTitle().equals("stem")));
    }

    @Test
    void deleteCategoryWhichNotExist() throws Exception {
        categoryRepo.save(ReturnValidObject.getValidCategory());
        this.mockMvc.perform(delete("/category/delete/{title}", "stem2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
        assertFalse(categoryRepo.findAll().isEmpty());
        assertTrue(categoryRepo.findAll().stream().anyMatch(category ->
                category.getTitle().equals("stem")));
    }

    @Test
    void getCategoriesWithValidPaginationAndExpectOk() throws Exception {
        List<CategoryEntity> category = IntStream.rangeClosed(1, 200)
                .mapToObj(i -> new CategoryEntity("title" + i,
                        "picURL" + i))
                .collect(Collectors.toList());
        categoryRepo.saveAll(category);
        ResultActions perform = this.mockMvc.perform(get("/category/sort/{field}", "id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("pageNumber", "5")
                .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk());
        MvcResult mvcResult = perform.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        assertTrue(content.contains("title51"));
    }

    @Test
    void getCategoriesWithUnValidSortingAndExpectFail() throws Exception {
        List<CategoryEntity> category = IntStream.rangeClosed(1, 200)
                .mapToObj(i -> new CategoryEntity("title" + i,
                        "picURL" + i))
                .collect(Collectors.toList());
        categoryRepo.saveAll(category);
        ResultActions perform = this.mockMvc.perform(get("/category/sort/{field}", "i2d")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber", "5")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getCategoriesWithUnValidPaginationAndExpectFail() throws Exception {
        List<CategoryEntity> category = IntStream.rangeClosed(1, 200)
                .mapToObj(i -> new CategoryEntity("title" + i,
                        "picURL" + i))
                .collect(Collectors.toList());
        categoryRepo.saveAll(category);
        ResultActions perform = this.mockMvc.perform(get("/category/sort/{field}", "id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber", "25")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk());
        MvcResult mvcResult = perform.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        assertTrue(content.contains("\"numberOfElements\":0"));
    }
}
