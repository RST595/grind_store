package com.bmxstore.grind_store.api;

import com.bmxstore.grind_store.db.entity.*;
import com.bmxstore.grind_store.db.entity.product.ProductEntity;
import com.bmxstore.grind_store.db.repository.*;
import com.bmxstore.grind_store.db.entity.order.OrderStatus;
import com.bmxstore.grind_store.dto.order.PaymentRequest;
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

    @AfterEach
    void cleanRepo() {
        orderRepo.deleteAll();
        cartRepo.deleteAll();
        productRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void createValidOrderAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        assertTrue(cartRepo.findAll().isEmpty());
        assertFalse(orderRepo.findAll().isEmpty());
        assertTrue(orderRepo.findAll().stream().anyMatch(order ->
                order.getUserEntity().getId().equals(userRepo.findAll().get(0).getId())));
    }

    @Test
    void tryCreateOrderWithNoSuchUserAndExpectFail() throws Exception {
        //FIXed
        // TODO: 16.03.2022 remove ivan copypaste
        userRepo.save(ReturnValidObject.getValidUser());
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId() + 1)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        //FIXed
        // TODO: 16.03.2022 replace with junit assert methods here and in all like this
        assertFalse(cartRepo.findAll().isEmpty());
        assertTrue(orderRepo.findAll().isEmpty());
    }

    @Test
    void tryCreateOrderWithNoItemsUserAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        assertTrue(cartRepo.findAll().isEmpty());
        assertTrue(orderRepo.findAll().isEmpty());
    }

    @Test
    void tryPayForOrderAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
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
        assertTrue(cartRepo.findAll().isEmpty());
        assertTrue(orderRepo.findAll().stream().anyMatch(order ->
                order.getId().equals(orderRepo.findAll().get(0).getId()) && order.getStatus().equals(OrderStatus.PAID)));
    }

    @Test
    void tryPayForOrderWithExpireCardAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
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
        assertTrue(cartRepo.findAll().isEmpty());
        assertTrue(orderRepo.findAll().stream().anyMatch(order ->
                order.getId().equals(orderRepo.findAll().get(0).getId()) && order.getStatus().equals(OrderStatus.PAYMENT_FAILED)));
    }

    @Test
    void tryPayForOrderWhichNotExistAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
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
        assertTrue(cartRepo.findAll().isEmpty());
        assertTrue(orderRepo.findAll().stream().anyMatch(order ->
                order.getId().equals(orderRepo.findAll().get(0).getId()) && order.getStatus().equals(OrderStatus.NEW)));
    }

    @Test
    void tryChangeOrderStatusAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
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
        assertTrue(cartRepo.findAll().isEmpty());
        assertTrue(orderRepo.findAll().stream().anyMatch(order ->
                order.getId().equals(orderRepo.findAll().get(0).getId()) && order.getStatus().equals(OrderStatus.SHIPPED)));
    }

    @Test
    void tryChangeOrderStatusWhichNotExistAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
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
        assertTrue(cartRepo.findAll().isEmpty());
        assertTrue(orderRepo.findAll().stream().anyMatch(order ->
                order.getId().equals(orderRepo.findAll().get(0).getId()) && order.getStatus().equals(OrderStatus.NEW)));
    }

    @Test
    void tryChangeOrderStatusWithWrongStatusAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
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
        assertTrue(cartRepo.findAll().isEmpty());
        assertTrue(orderRepo.findAll().stream().anyMatch(order ->
                order.getId().equals(orderRepo.findAll().get(0).getId()) && order.getStatus().equals(OrderStatus.NEW)));
    }

    @Test
    void getListOfUserOrdersWithValidUserAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        if(categoryRepo.findAll().isEmpty()){
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().isCreated());
        this.mockMvc.perform(get("/order/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().isOk());
//        assertTrue(cartRepo.findAll().isEmpty());
//        assertTrue(orderRepo.findAll().stream().anyMatch(order ->
//                order.getId().equals(orderRepo.findAll().get(0).getId()) && order.getStatus().equals(OrderStatus.NEW)));
    }

    @Test
    void getListOfUserOrdersWithUnValidUserAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        if (categoryRepo.findAll().isEmpty()) {
            categoryRepo.save(ReturnValidObject.getValidCategory());
        }
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
        this.mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().isCreated());
        this.mockMvc.perform(get("/order/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId() + 1)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
