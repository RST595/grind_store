package com.bmxstore.grindStore.web;

import com.bmxstore.grindStore.ExHandler.ErrorMessage;
import com.bmxstore.grindStore.ExHandler.ServiceError;
import com.bmxstore.grindStore.db.Entity.CartEntity;
import com.bmxstore.grindStore.db.Entity.ProductEntity;
import com.bmxstore.grindStore.db.Entity.UserEntity;
import com.bmxstore.grindStore.db.Repository.CartRepo;
import com.bmxstore.grindStore.db.Repository.CategoryRepo;
import com.bmxstore.grindStore.db.Repository.ProductRepo;
import com.bmxstore.grindStore.db.Repository.UserRepo;
import com.bmxstore.grindStore.dto.Cart.AddToCartRequest;
import com.bmxstore.grindStore.validObjects.ReturnValidObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {
    // FIXME: 16.03.2022 almoust all falls with .DataIntegrityViolationException

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
        this.mockMvc.perform(get("/cart/list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("userId", String.valueOf(1)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        // TODO: 16.03.2022 add assert
    }

    @Test
    void getUserCartItemsAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        this.mockMvc.perform(get("/cart/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        // TODO: 16.03.2022 add assert

    }

    @Test
    void addToCartAndExpectOk() throws Exception {
            userRepo.save(ReturnValidObject.getValidUser());
            categoryRepo.save(ReturnValidObject.getValidCategory());
            productRepo.save(ReturnValidObject.getValidProduct());
        this.mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(productRepo.findAll().get(0).getId(),5)))
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        if(cartRepo.findAll().isEmpty()) {
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("CART_ITEM_NOT_FOUND"));
        }
    }

    @Test
    void addToCartWithSameUserAndProductAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        productRepo.save(ReturnValidObject.getValidProduct());
        cartRepo.save(ReturnValidObject.getValidCart());
        this.mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddToCartRequest(productRepo.findAll().get(0).getId(),ReturnValidObject.quantity)))
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        for(CartEntity cartItems : cartRepo.findAll()){
            if(cartItems.getQuantity() != ReturnValidObject.quantity * 2) {
                throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("CART_ITEM_NOT_FOUND"));
            }
        }
    }

    @Test
    void addToCartWithNoSuchUserAndExpectFail() throws Exception {
        categoryRepo.save(ReturnValidObject.getValidCategory());
        productRepo.save(ReturnValidObject.getValidProduct());
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
        productRepo.save(ReturnValidObject.getValidProduct());
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), ReturnValidObject.quantity, users.get(0)));
        this.mockMvc.perform(delete("/cart/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId()))
                        .param("cartId", String.valueOf(cartRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        if(!cartRepo.findAll().isEmpty()) {
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("CART_ITEM_NOT_FOUND"));
        }
    }

    @Test
    void removeFromCartItemWhichNotExistAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        productRepo.save(ReturnValidObject.getValidProduct());
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), ReturnValidObject.quantity, users.get(0)));
        this.mockMvc.perform(delete("/cart/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userRepo.findAll().get(0).getId()))
                        .param("cartId", String.valueOf(2)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        if(cartRepo.findAll().isEmpty()) {
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("CART_ITEM_NOT_FOUND"));
        }
    }

    @Test
    void removeFromUserWhichNotExistCartItemAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        productRepo.save(ReturnValidObject.getValidProduct());
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), ReturnValidObject.quantity, users.get(0)));
        this.mockMvc.perform(delete("/cart/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(3))
                        .param("cartId", String.valueOf(cartRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        // TODO: 16.03.2022 add search and equals assertion
        if(cartRepo.findAll().isEmpty()) {
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("CART_ITEM_NOT_FOUND"));
        }
    }

    @Test
    void updateItemQuantityAndExpectOk() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        productRepo.save(ReturnValidObject.getValidProduct());
        List<ProductEntity> products = productRepo.findAll();
        List<UserEntity> users = userRepo.findAll();
        cartRepo.save(new CartEntity(products.get(0), ReturnValidObject.quantity, users.get(0)));
        this.mockMvc.perform(put("/cart/update/{quantity}", ReturnValidObject.quantity)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cartId", String.valueOf(cartRepo.findAll().get(0).getId())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        for(CartEntity cartItems : cartRepo.findAll()){
            if(cartItems.getQuantity() != ReturnValidObject.quantity) {
                throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("CART_ITEM_NOT_FOUND"));
            }
        }
    }

    @Test
    void updateCartItemQuantityWhichNotExistAndExpectFail() throws Exception {
        userRepo.save(ReturnValidObject.getValidUser());
        categoryRepo.save(ReturnValidObject.getValidCategory());
        productRepo.save(ReturnValidObject.getValidProduct());
        this.mockMvc.perform(put("/cart/update/{quantity}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cartId", String.valueOf(2)))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
