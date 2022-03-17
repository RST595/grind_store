package com.bmxstore.grind_store.web;

import com.bmxstore.grind_store.ex_handler.ErrorMessage;
import com.bmxstore.grind_store.ex_handler.ServiceError;
import com.bmxstore.grind_store.db.entity.CartEntity;
import com.bmxstore.grind_store.db.entity.ProductEntity;
import com.bmxstore.grind_store.db.entity.UserEntity;
import com.bmxstore.grind_store.db.repository.CartRepo;
import com.bmxstore.grind_store.db.repository.CategoryRepo;
import com.bmxstore.grind_store.db.repository.ProductRepo;
import com.bmxstore.grind_store.db.repository.UserRepo;
import com.bmxstore.grind_store.dto.cart.AddToCartRequest;
import com.bmxstore.grind_store.valid_object.ReturnValidObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {
    //
    // FIXME: 16.03.2022 almost all falls with .DataIntegrityViolationException

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepo userRepo;

    @Autowired
    CartRepo cartRepo;

    @Autowired
    CategoryRepo categoryRepo;

    @Autowired
    ProductRepo productRepo;

    @BeforeEach
    void cleanRepo(){
        cartRepo.deleteAll();
        productRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    void getUserCartItemsWhichNotExists() throws Exception {
        ResultActions perform = this.mockMvc.perform(get("/cart/list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("userId", String.valueOf(1)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        //FIXed
        // TODO: 16.03.2022 add assert
        MvcResult mvcResult = perform.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        String content = response.getContentAsString();
        assertTrue(content.contains("NOT_FOUND"));

    }

    @Test
    void getUserCartItemsAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        this.mockMvc.perform(get("/cart/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        //FIXed in previous method. Not sure it's good method.
        // TODO: 16.03.2022 add assert

    }

    @Test
    void addToCartAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        this.mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(productRepo.findAll().get(0).getId(),5)))
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        assertFalse(cartRepo.findAll().isEmpty());
        assertTrue(cartRepo.findAll().stream().anyMatch(cart ->
                cart.getProductEntity().getId().equals(productRepo.findAll().get(0).getId())));
    }

    @Test
    void addToCartWithSameUserAndProductAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
        this.mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(productRepo.findAll().get(0).getId(),ReturnValidObject.quantity)))
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        assertTrue(cartRepo.findAll().stream().anyMatch(cart ->
                cart.getQuantity() == ReturnValidObject.quantity * 2));
    }

    @Test
    void addToCartWithNoSuchUserAndExpectFail() throws Exception {
        categoryRepo.save(ReturnValidObject.getValidCategory());
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        this.mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(productRepo.findAll().get(0).getId(),5)))
                        .param("userId", String.valueOf(1)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addToCartWithNoSuchProductAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        this.mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(1L,5)))
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @Test
    void removeFromCartAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
        this.mockMvc.perform(delete("/cart/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId()))
                        .param("cartId", String.valueOf(cartRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        assertTrue(cartRepo.findAll().isEmpty());
    }

    @Test
    void removeFromCartItemWhichNotExistAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
        this.mockMvc.perform(delete("/cart/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId()))
                        .param("cartId", String.valueOf(2)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        assertTrue(cartRepo.findAll().stream().anyMatch(cart ->
                cart.getQuantity() == ReturnValidObject.quantity));
        assertFalse(cartRepo.findAll().isEmpty());
    }

    @Test
    void removeFromUserWhichNotExistCartItemAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
        this.mockMvc.perform(delete("/cart/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(3))
                        .param("cartId", String.valueOf(cartRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        assertFalse(cartRepo.findAll().isEmpty());
        assertTrue(cartRepo.findAll().stream().anyMatch(cart ->
                cart.getQuantity() == ReturnValidObject.quantity));
    }

    @Test
    void updateItemQuantityAndExpectOk() throws Exception {
        int newQuantity = 5;
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        cartRepo.save(new CartEntity(productRepo.findAll().get(0), ReturnValidObject.quantity, userRepo.findAll().get(0)));
        this.mockMvc.perform(put("/cart/update/{quantity}", newQuantity)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cartId", String.valueOf(cartRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        Optional<CartEntity> cartById = cartRepo.findById(cartRepo.findAll().get(0).getId());
        CartEntity cart = cartById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_ACCEPTABLE,
                ErrorMessage.valueOf("CART_ITEM_NOT_FOUND")));
        cartRepo.findById(cartRepo.findAll().get(0).getId());
        assertEquals(cart.getQuantity(), newQuantity);
    }

    @Test
    void updateCartItemQuantityWhichNotExistAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        ProductEntity product = ReturnValidObject.getValidProduct();
        product.setCategoryEntity(categoryRepo.findAll().get(0));
        productRepo.save(product);
        this.mockMvc.perform(put("/cart/update/{quantity}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cartId", String.valueOf(2)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
