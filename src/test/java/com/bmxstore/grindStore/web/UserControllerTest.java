package com.bmxstore.grindStore.web;

import com.bmxstore.grindStore.ExHandler.ErrorMessage;
import com.bmxstore.grindStore.ExHandler.ServiceError;
import com.bmxstore.grindStore.db.Entity.ProductEntity;
import com.bmxstore.grindStore.db.Entity.UserEntity;
import com.bmxstore.grindStore.db.Repository.CategoryRepo;
import com.bmxstore.grindStore.db.Repository.ProductRepo;
import com.bmxstore.grindStore.db.Repository.UserRepo;
import com.bmxstore.grindStore.dto.Category.CategoryRequest;
import com.bmxstore.grindStore.dto.Enums.Color;
import com.bmxstore.grindStore.dto.Enums.Role;
import com.bmxstore.grindStore.dto.Enums.UserStatus;
import com.bmxstore.grindStore.dto.Product.ProductRequest;
import com.bmxstore.grindStore.dto.User.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void clearRepo() {
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
        UserEntity testEntity = new UserEntity();
        for(UserEntity user : userRepo.findAll()){
            if(user.getEmail().equals("ivanov@mail.ru")){
                testEntity = user;

            }
        }
        assert(testEntity.getEmail().equals("ivanov@mail.ru"));
    }

    @Test
    void addPUserWithSameEmailTwiceAndExpectFail() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Ivan", "Ivanov",
                                "Saint Petersburg", "ivanov@mail.ru", Role.USER, "12345"))))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
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
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Ivan", "Ivanov",
                                "Saint Petersburg", "ivanov@mail.ru", Role.USER, "12345"))))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(post("/user/update/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Jeff", "Carrot",
                                "Chicago", "ivanov@mail.ru", Role.USER, "   ")))
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        UserEntity testEntity = new UserEntity();
        for(UserEntity user : userRepo.findAll()){
            if(user.getEmail().equals("ivanov@mail.ru") && user.getPassword().equals("12345")
            && user.getLastName().equals("Carrot")){
                testEntity = user;
            }
        }
        assert(testEntity.getPassword().equals("12345"));
    }

    @Test
    void updateUserWhichNotExist() throws Exception {
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Ivan", "Ivanov",
                                "Saint Petersburg", "ivanov@mail.ru", Role.USER, "12345"))))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
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
        this.mockMvc.perform(post("/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserRequest("Ivan", "Ivanov",
                                "Saint Petersburg", "ivanov@mail.ru", Role.USER, "12345"))))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(delete("/user/delete/{userId}",
                Long.toString(userRepo.findAll().get(0).getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        for(UserEntity user : userRepo.findAll()){
            if(user.getEmail().equals("ivanov@mail.ru") && user.getStatus().equals(UserStatus.ACTIVE)){
                throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("NOT_EMPTY"));
            }
        }
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
