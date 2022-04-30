package com.bmxstore.grind_store.api_controller;

import com.bmxstore.grind_store.data.entity.user.UserEntity;
import com.bmxstore.grind_store.data.entity.user.UserRole;
import com.bmxstore.grind_store.data.repository.UserRepo;
import com.bmxstore.grind_store.valid_object.ReturnValidObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.File;
import java.nio.file.Files;

import static com.bmxstore.grind_store.data.entity.user.UserRole.ADMIN;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SignInUpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @AfterEach
    void cleanRepo() {
        userRepo.deleteAll();
    }

    @Test
    void addNewAdminAndExpectOk() throws Exception {
        RequestBuilder request = post("/admin/registration")
                .param("firstName", "nn")
                .param("lastName", "gg")
                .param("keyWord", "GRIND")
                .param("email", "nn@gg.com")
                .param("password", "string")
                .param("confirmPassword", "string");
        this.mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        assertTrue(userRepo.findAll().stream().anyMatch(user ->
                user.getEmail().equals("nn@gg.com")));
    }

    @Test
    void addInvalidAdminAndExpectFail() throws Exception {
        File login = new ClassPathResource("templates/registration_error.html").getFile();
        String html = new String(Files.readAllBytes(login.toPath()));
        RequestBuilder request = post("/admin/registration")
                .param("firstName", "nn")
                .param("lastName", "gg")
                .param("keyWord", "GRIND1234")
                .param("email", "nn@gg.com")
                .param("password", "string")
                .param("confirmPassword", "string");
        this.mockMvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(content().string(html))
                .andDo(print());
        assertTrue(userRepo.findAll().isEmpty());
    }

    @Test
    void loginWithValidUserAndExpectOk() throws Exception {
        UserEntity newUser = ReturnValidObject.getValidUser();
        String newUserPassword = newUser.getPassword();
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        userRepo.save(newUser);
        RequestBuilder request = post("/login")
                .param("username", newUser.getEmail())
                .param("password", newUserPassword);
        this.mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/panel"));
    }
}
