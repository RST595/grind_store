package com.bmxstore.grind_store.api;

import com.bmxstore.grind_store.db.repository.*;
import com.bmxstore.grind_store.db.entity.user.UserRole;
import com.bmxstore.grind_store.dto.user.UserRequest;
import com.bmxstore.grind_store.valid_object.ReturnValidObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
                                "Saint Petersburg", "ivanov@mail.ru", UserRole.USER, "12345"))))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        assertTrue(userRepo.findAll().stream().anyMatch(user ->
                user.getEmail().equals("ivanov@mail.ru")));
    }

    @Test
    void addPUserWithSameEmailTwiceAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "ivanov@mail.ru", UserRole.USER, "54321"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addUserWithEmptyEmailAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "", UserRole.USER, "54321"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addUserWithEmailFromSpacesAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "   ", UserRole.USER, "54321"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addUserWithEmptyPasswordAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "shin@gmail.com", UserRole.USER, ""))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addUserWithPasswordFromSpacesAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "shin@gmail.com", UserRole.USER, "   "))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }


    @Test
    void updateUserInfoAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        this.mockMvc.perform(post("/user/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Jeff", "Carrot",
                                "Chicago", "ivanov@mail.ru", UserRole.USER, "")))
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
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
                                "Chicago", "ivanov@mail.ru", UserRole.USER, "   ")))
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId() + 1)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteProductAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        this.mockMvc.perform(delete("/user/delete/{userId}",
                Long.toString(userRepo.findAll().get(0).getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        assertFalse(userRepo.findAll().stream().anyMatch(user ->
                user.getEmail().equals("ivanov@mail.ru") && user.isEnabled()));
    }

    @Test
    void deleteProductWhichNotExist() throws Exception {
        this.mockMvc.perform(delete("/user/delete/{userId}",
                        "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
