package com.bmxstore.grind_store.api_controller;

import com.bmxstore.grind_store.data.entity.user.UserEntity;
import com.bmxstore.grind_store.data.repository.*;
import com.bmxstore.grind_store.data.entity.user.UserRole;
import com.bmxstore.grind_store.dto.user.AdminRequest;
import com.bmxstore.grind_store.dto.user.UserRequest;
import com.bmxstore.grind_store.valid_object.ReturnValidObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "username", authorities = {"ADMIN"})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @AfterEach
    void cleanRepo() {
        orderRepo.deleteAll();
        cartRepo.deleteAll();
        productRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void getAllUsers() throws Exception {
        this.mockMvc.perform(get("/user/list")
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void addNewUserAndExpectOk() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Ivan", "Ivanov",
                                "Saint Petersburg", "ivanov@mail.ru", UserRole.USER, "12345", "12345"))))
                .andDo(print())
                .andExpect(status().isCreated());
        assertTrue(userRepo.findAll().stream().anyMatch(user ->
                user.getEmail().equals("ivanov@mail.ru")));
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
    void addUserWithSameEmailTwiceAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "ivanov@mail.ru", UserRole.USER, "54321", "54321"))))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void addUserWithEmptyEmailAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "", UserRole.USER, "54321", "54321"))))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void addUserWithEmailFromSpacesAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "   ", UserRole.USER, "54321", "54321"))))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void addUserWithEmptyPasswordAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "shin@gmail.com", UserRole.USER, "", ""))))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void addUserWithPasswordFromSpacesAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "shin@gmail.com", UserRole.USER, "   ", "   "))))
                .andDo(print())
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void searchUserWithValidRequestAndExpectOk() throws Exception {
        userRepo.saveAll(IntStream.rangeClosed(1, 100)
                .mapToObj(i -> new UserEntity("Firstname" + i, "Lastname" + i, "Address" + i,
                        "email" + i, UserRole.USER, bCryptPasswordEncoder.encode("string" + i)))
                .collect(Collectors.toList()));
        ResultActions perform = this.mockMvc.perform(get("/user/search")
                        .param("page number", "0")
                        .param("page size", "10")
                        .param("Sort direction", "ASC")
                        .param("sort by", "email")
                        .param("Firs name", "")
                        .param("Last name", "")
                        .param("e-mail", "email10")
                        .param("Address", ""))
                .andDo(print())
                .andExpect(status().isOk());
        MvcResult mvcResult = perform.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        assertTrue(content.contains("email10"));
    }

    @Test
    void searchNotExistingUserWithValidRequestAndExpectNotFound() throws Exception {
        userRepo.saveAll(IntStream.rangeClosed(1, 50)
                .mapToObj(i -> new UserEntity("Firstname" + i, "Lastname" + i, "Address" + i,
                        "email" + i, UserRole.USER, bCryptPasswordEncoder.encode("string" + i)))
                .collect(Collectors.toList()));
        ResultActions perform = this.mockMvc.perform(get("/user/search")
                        .param("page number", "0")
                        .param("page size", "10")
                        .param("Sort direction", "ASC")
                        .param("sort by", "email")
                        .param("Firs name", "")
                        .param("Last name", "")
                        .param("e-mail", "email60")
                        .param("Address", ""))
                .andDo(print())
                .andExpect(status().isOk());
        MvcResult mvcResult = perform.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        assertFalse(content.contains("email60"));
    }

    @Test
    void searchUserWithInValidRequestAndExpectFail() throws Exception {
        userRepo.saveAll(IntStream.rangeClosed(1, 100)
                .mapToObj(i -> new UserEntity("Firstname" + i, "Lastname" + i, "Address" + i,
                        "email" + i, UserRole.USER, bCryptPasswordEncoder.encode("string" + i)))
                .collect(Collectors.toList()));
        ResultActions perform = this.mockMvc.perform(get("/user/search")
                        .param("page number", "0")
                        .param("page size", "10")
                        .param("Sort direction", "ASC213")
                        .param("sort by", "email")
                        .param("Firs name", "")
                        .param("Last name", "")
                        .param("e-mail", "email10")
                        .param("Address", ""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void updateUserInfoAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        this.mockMvc.perform(post("/user/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Jeff", "Carrot",
                                "Chicago", "ivanov@mail.ru", UserRole.USER, null, null)))
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().isOk());
        List<UserEntity> users = userRepo.findAll();
        assertTrue(userRepo.findAll().stream().anyMatch(user ->
                user.getEmail().equals("ivanov@mail.ru") && user.getPassword().equals("12345")
                        && user.getLastName().equals("Carrot")));
    }

    @Test
    void updateUserWhichNotExist() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        this.mockMvc.perform(post("/user/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Jeff", "Carrot",
                                "Chicago", "ivanov@mail.ru", UserRole.USER, "   ", "   ")))
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId() + 1)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        this.mockMvc.perform(delete("/user/delete/{userId}",
                Long.toString(userRepo.findAll().get(0).getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        assertFalse(userRepo.findAll().stream().anyMatch(user ->
                user.getEmail().equals("ivanov@mail.ru") && user.isEnabled()));
    }

    @Test
    void deleteUserWhichNotExist() throws Exception {
        this.mockMvc.perform(delete("/user/delete/{userId}",
                        "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
