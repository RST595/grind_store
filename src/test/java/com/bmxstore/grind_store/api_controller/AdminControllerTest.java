package com.bmxstore.grind_store.api_controller;

import com.bmxstore.grind_store.data.entity.CategoryEntity;
import com.bmxstore.grind_store.data.repository.CategoryRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.File;
import java.nio.file.Files;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "username", authorities = {"ADMIN"})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepo categoryRepo;

    @BeforeEach
    void cleanRepo() {
        categoryRepo.deleteAll();
    }

    @Test
    void checkAdminPanelAndExpectOk() throws Exception {
        File login = new ClassPathResource("templates/admin_panel.html").getFile();
        String html = new String(Files.readAllBytes(login.toPath()));
        RequestBuilder request = get("/admin/panel");
        this.mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(html))
                .andDo(print());
    }

    @Test
    void addNewCategoryAndExpectOk() throws Exception {
        assertTrue(categoryRepo.findAll().isEmpty());
        RequestBuilder request = post("/categories/add")
                .param("title", "someName")
                .param("picUrl", "somePic");
        this.mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories/all"));
        assertTrue(categoryRepo.findAll().stream().anyMatch(category ->
                category.getTitle().equals("someName")));
    }

    @Test
    void deleteCategoryAndExpectOk() throws Exception {
        assertTrue(categoryRepo.findAll().isEmpty());
        categoryRepo.save(new CategoryEntity("someName73", "somePic"));
        assertTrue(categoryRepo.findAll().stream().anyMatch(category ->
                category.getTitle().equals("someName73")));
        RequestBuilder request = post("/categories/delete")
                .param("title", "someName73");
        this.mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories/all"));
        assertFalse(categoryRepo.findAll().stream().anyMatch(category ->
                category.getTitle().equals("someName73")));
    }

    @Test
    void displayAllCategoryAndExpectOk() throws Exception {
        assertTrue(categoryRepo.findAll().isEmpty());
        categoryRepo.save(new CategoryEntity("someName", "somePic"));
        assertTrue(categoryRepo.findAll().stream().anyMatch(category ->
                category.getTitle().equals("someName")));
        RequestBuilder request = get("/categories/all");
        this.mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<td>someName</td>")));
    }

    @Test
    void editCategoryAndExpectOk() throws Exception {
        assertTrue(categoryRepo.findAll().isEmpty());
        categoryRepo.save(new CategoryEntity("someName", "somePic"));
        assertTrue(categoryRepo.findAll().stream().anyMatch(category ->
                category.getTitle().equals("someName")));
        RequestBuilder request = get("/categories/edit");
        this.mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"someName\"")));
    }

}
