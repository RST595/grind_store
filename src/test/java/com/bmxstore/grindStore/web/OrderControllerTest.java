package com.bmxstore.grindStore.web;

import com.bmxstore.grindStore.db.Entity.*;
import com.bmxstore.grindStore.db.Repository.*;
import com.bmxstore.grindStore.dto.Enums.Color;
import com.bmxstore.grindStore.dto.Enums.OrderStatus;
import com.bmxstore.grindStore.dto.Enums.Role;
import com.bmxstore.grindStore.dto.Enums.UserStatus;
import com.bmxstore.grindStore.dto.Order.PaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepo userRepo;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    ProductRepo productRepo;

    @BeforeEach
    void cleanRepo() {
        orderRepo.deleteAll();
        cartRepo.deleteAll();
        productRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void createValidOrderAndExpectOk() throws Exception {
        int qnt = 5;
        userRepo.save(new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>()));
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), qnt, users.get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        assert (cartRepo.findAll().isEmpty());
        assert (!orderRepo.findAll().isEmpty());
    }

    @Test
    void tryCreateOrderWithNoSuchUserAndExpectFail() throws Exception {
        int qnt = 5;
        userRepo.save(new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>()));
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), qnt, users.get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId() + 1)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        assert (!cartRepo.findAll().isEmpty());
        assert (orderRepo.findAll().isEmpty());
    }

    @Test
    void tryCreateOrderWithNoItemsUserAndExpectFail() throws Exception {
        userRepo.save(new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>()));
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        assert (cartRepo.findAll().isEmpty());
        assert (orderRepo.findAll().isEmpty());
    }

    @Test
    void tryPayForOrderAndExpectOk() throws Exception {
        int qnt = 5;
        userRepo.save(new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>()));
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), qnt, users.get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(post("/order/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("orderId", String.valueOf(orderRepo.findAll().get(0).getId()))
                        .content(objectMapper.writeValueAsString(new PaymentRequest("4400550065433211",
                                2025, 10, "999", "Ivanov"))))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        assert (cartRepo.findAll().isEmpty());
        OrderEntity newOrder = new OrderEntity();
        for(OrderEntity order : orderRepo.findAll()){
            if(order.getId().equals(orderRepo.findAll().get(0).getId())){
                newOrder = order;
            }
        }
        assert (newOrder.getStatus().equals(OrderStatus.PAID));
    }

    @Test
    void tryPayForOrderWithExpireCardAndExpectFail() throws Exception {
        int qnt = 5;
        userRepo.save(new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>()));
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), qnt, users.get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(post("/order/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("orderId", String.valueOf(orderRepo.findAll().get(0).getId()))
                        .content(objectMapper.writeValueAsString(new PaymentRequest("4400550065433211",
                                2020, 10, "999", "Ivanov"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        assert (cartRepo.findAll().isEmpty());
        OrderEntity newOrder = new OrderEntity();
        for(OrderEntity order : orderRepo.findAll()){
            if(order.getId().equals(orderRepo.findAll().get(0).getId())){
                newOrder = order;
            }
        }
        assert (newOrder.getStatus().equals(OrderStatus.PAYMENT_FAILED));
    }

    @Test
    void tryPayForOrderWhichNotExistAndExpectFail() throws Exception {
        int qnt = 5;
        userRepo.save(new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>()));
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), qnt, users.get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(post("/order/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("orderId", String.valueOf(orderRepo.findAll().get(0).getId() + 1))
                        .content(objectMapper.writeValueAsString(new PaymentRequest("4400550065433211",
                                2020, 10, "999", "Ivanov"))))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        assert (cartRepo.findAll().isEmpty());
        OrderEntity newOrder = new OrderEntity();
        for(OrderEntity order : orderRepo.findAll()){
            if(order.getId().equals(orderRepo.findAll().get(0).getId())){
                newOrder = order;
            }
        }
        assert (newOrder.getStatus().equals(OrderStatus.NEW));
    }

    @Test
    void tryChangeOrderStatusAndExpectOk() throws Exception {
        int qnt = 5;
        userRepo.save(new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>()));
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), qnt, users.get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(post("/order/changeStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("orderId", String.valueOf(orderRepo.findAll().get(0).getId()))
                        .param("status", String.valueOf(OrderStatus.SHIPPED)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        assert (cartRepo.findAll().isEmpty());
        OrderEntity newOrder = new OrderEntity();
        for(OrderEntity order : orderRepo.findAll()){
            if(order.getId().equals(orderRepo.findAll().get(0).getId())){
                newOrder = order;
            }
        }
        assert (newOrder.getStatus().equals(OrderStatus.SHIPPED));
    }

    @Test
    void tryChangeOrderStatusWhichNotExistAndExpectFail() throws Exception {
        int qnt = 5;
        userRepo.save(new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>()));
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), qnt, users.get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(post("/order/changeStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("orderId", String.valueOf(orderRepo.findAll().get(0).getId() + 1))
                        .param("status", String.valueOf(OrderStatus.SHIPPED)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        assert (cartRepo.findAll().isEmpty());
        OrderEntity newOrder = new OrderEntity();
        for(OrderEntity order : orderRepo.findAll()){
            if(order.getId().equals(orderRepo.findAll().get(0).getId())){
                newOrder = order;
            }
        }
        assert (newOrder.getStatus().equals(OrderStatus.NEW));
    }

    @Test
    void tryChangeOrderStatusWithWrongStatusAndExpectFail() throws Exception {
        int qnt = 5;
        userRepo.save(new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>()));
        categoryRepo.save(new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
        List<CategoryEntity> categories = categoryRepo.findAll();
        productRepo.save(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                categories.get(0)));
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), qnt, users.get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        this.mockMvc.perform(post("/order/changeStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("orderId", String.valueOf(orderRepo.findAll().get(0).getId()))
                        .param("status", "SHIPPED2"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        assert (cartRepo.findAll().isEmpty());
        OrderEntity newOrder = new OrderEntity();
        for(OrderEntity order : orderRepo.findAll()){
            if(order.getId().equals(orderRepo.findAll().get(0).getId())){
                newOrder = order;
            }
        }
        assert (newOrder.getStatus().equals(OrderStatus.NEW));
    }

}
