package com.bmxstore.grind_store.web;

import com.bmxstore.grind_store.db.Entity.UserEntity;
import com.bmxstore.grind_store.db.Repository.*;
import com.bmxstore.grind_store.dto.Enums.Role;
import com.bmxstore.grind_store.dto.Enums.UserStatus;
import com.bmxstore.grind_store.dto.User.UserRequest;
import com.bmxstore.grind_store.validObjects.ReturnValidObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

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

    @BeforeEach
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
                                "Saint Petersburg", "ivanov@mail.ru", Role.USER, "12345"))))
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
                                "Moscow", "ivanov@mail.ru", Role.USER, "54321"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addUserWithEmptyEmailAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "", Role.USER, "54321"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addUserWithEmailFromSpacesAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "   ", Role.USER, "54321"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addUserWithEmptyPasswordAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "shin@gmail.com", Role.USER, ""))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addUserWithPasswordFromSpacesAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Sergey", "Shin",
                                "Moscow", "shin@gmail.com", Role.USER, "   "))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }


    @Test
    void updateUserInfoAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        this.mockMvc.perform(post("/user/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Jeff", "Carrot",
                                "Chicago", "ivanov@mail.ru", Role.USER, "   ")))
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
                                "Chicago", "ivanov@mail.ru", Role.USER, "   ")))
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
        // TODO: 16.03.2022 use search like this in all places
        assertFalse(userRepo.findAll().stream().anyMatch(user ->
                user.getEmail().equals("ivanov@mail.ru") && user.getStatus().equals(UserStatus.ACTIVE)));
        //List<UserEntity> all = userRepo.findAll();
//        for(UserEntity user : all){
//            if(user.getEmail().equals("ivanov@mail.ru") && user.getStatus().equals(UserStatus.ACTIVE)){
//                throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("NOT_EMPTY"));
//            }
//        }
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
