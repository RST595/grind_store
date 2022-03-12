package com.bmxstore.grindStore.Service;

import com.bmxstore.grindStore.ExHandler.ErrorMessage;
import com.bmxstore.grindStore.ExHandler.ServiceError;
import com.bmxstore.grindStore.FeignClient.Currency;
import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.db.Entity.CartEntity;
import com.bmxstore.grindStore.db.Entity.ProductEntity;
import com.bmxstore.grindStore.db.Entity.UserEntity;
import com.bmxstore.grindStore.db.Repository.CartRepo;
import com.bmxstore.grindStore.db.Repository.ProductRepo;
import com.bmxstore.grindStore.db.Repository.UserRepo;
import com.bmxstore.grindStore.dto.Cart.AddToCartRequest;
import com.bmxstore.grindStore.dto.Cart.CartResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepo cartRepo;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    public List<CartResponse> getUserCartItems(Long userId) {
        List<CartResponse> userCartItems = new ArrayList<>();
        for(CartEntity cartElement : cartRepo.findAll()){
            if(cartElement.getUserEntity() == userRepo.getById(userId)) {
                CartResponse cartResponse = objectMapper.convertValue(cartElement, CartResponse.class);
                cartResponse.setCreateDate(cartElement.getCreatedDate());
                cartResponse.setProduct(cartElement.getProductEntity().getName());
                userCartItems.add(cartResponse);
            }
        }
        return userCartItems;
    }

    public ResponseEntity<ResponseApi> addToCart(AddToCartRequest addToCartRequest, Long userId) {
        Optional<UserEntity> user = userRepo.findById(userId);
        UserEntity userEntity = user.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.USER_ID_NOT_FOUND));
        Optional<ProductEntity> product = productRepo.findById(addToCartRequest.getProductId());
        ProductEntity productEntity = product.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.PRODUCT_NOT_EXIST));
        CartEntity cartEntity = new CartEntity(productEntity, addToCartRequest.getQuantity(), userEntity);
        cartRepo.save(cartEntity);
        return new ResponseEntity<>(new ResponseApi(true, "Added to cart"), HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseApi> removeFromCart(Long cartId, Long userId) {
        Optional<UserEntity> userById = userRepo.findById(userId);
        userById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.USER_ID_NOT_FOUND));
        Optional<CartEntity> cartItem = cartRepo.findById(cartId);
        CartEntity cartEntity = cartItem.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.CART_ITEM_NOT_FOUND));
        if(!cartEntity.getUserEntity().getId().equals(userId)) {
            return new ResponseEntity<>(new ResponseApi(false, "User doesn't have this item"), HttpStatus.NOT_ACCEPTABLE);
        }
        cartRepo.delete(cartEntity);
        return new ResponseEntity<>(new ResponseApi(true, "Item deleted"), HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseApi> updateItemQuantity(Long cartId, int quantity) {
        Optional<CartEntity> cartItem = cartRepo.findById(cartId);
        CartEntity cartEntity = cartItem.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.CART_ITEM_NOT_FOUND));
        cartEntity.setQuantity(quantity);
        cartRepo.save(cartEntity);
        return new ResponseEntity<>(new ResponseApi(true, "Quantity edited"), HttpStatus.CREATED);
    }

}
