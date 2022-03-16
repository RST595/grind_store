package com.bmxstore.grind_store.Service;

import com.bmxstore.grind_store.ExHandler.ErrorMessage;
import com.bmxstore.grind_store.ExHandler.ServiceError;
import com.bmxstore.grind_store.ResponseApi.ResponseApi;
import com.bmxstore.grind_store.db.Entity.CartEntity;
import com.bmxstore.grind_store.db.Entity.ProductEntity;
import com.bmxstore.grind_store.db.Entity.UserEntity;
import com.bmxstore.grind_store.db.Repository.CartRepo;
import com.bmxstore.grind_store.db.Repository.ProductRepo;
import com.bmxstore.grind_store.db.Repository.UserRepo;
import com.bmxstore.grind_store.dto.Cart.AddToCartRequest;
import com.bmxstore.grind_store.dto.Cart.CartResponse;
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
        Optional<UserEntity> userById = userRepo.findById(userId);
        UserEntity user = userById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("USER_NOT_EXIST")));
        List<CartResponse> userCartItems = new ArrayList<>();
        for(CartEntity cartElement : cartRepo.findAll()){
            if(cartElement.getUserEntity() == user) {
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
        UserEntity userEntity = user.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("USER_ID_NOT_FOUND")));
        Optional<ProductEntity> product = productRepo.findById(addToCartRequest.getProductId());
        ProductEntity productEntity = product.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("PRODUCT_NOT_EXIST")));
        CartEntity newCartItem = new CartEntity(productEntity, addToCartRequest.getQuantity(), userEntity);
        for(CartEntity cartItem : cartRepo.findAll()){
            if(cartItem.getProductEntity().getId().equals(newCartItem.getProductEntity().getId())
            && cartItem.getUserEntity().getId().equals(newCartItem.getUserEntity().getId())){
                cartItem.setQuantity(cartItem.getQuantity() + newCartItem.getQuantity());
                cartRepo.save(cartItem);
                return new ResponseEntity<>(new ResponseApi(true, "Added to cart"), HttpStatus.CREATED);
            }
        }
        cartRepo.save(newCartItem);
        return new ResponseEntity<>(new ResponseApi(true, "Added to cart"), HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseApi> removeFromCart(Long cartId, Long userId) {
        Optional<UserEntity> userById = userRepo.findById(userId);
        userById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("USER_ID_NOT_FOUND")));
        Optional<CartEntity> cartItem = cartRepo.findById(cartId);
        CartEntity cartEntity = cartItem.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("CART_ITEM_NOT_FOUND")));
        if(!cartEntity.getUserEntity().getId().equals(userId)) {
            return new ResponseEntity<>(new ResponseApi(false, "User doesn't have this item"), HttpStatus.NOT_ACCEPTABLE);
        }
        cartRepo.delete(cartEntity);
        return new ResponseEntity<>(new ResponseApi(true, "Item deleted"), HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseApi> updateItemQuantity(Long cartId, int quantity) {
        Optional<CartEntity> cartItem = cartRepo.findById(cartId);
        CartEntity cartEntity = cartItem.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("CART_ITEM_NOT_FOUND")));
        cartEntity.setQuantity(quantity);
        cartRepo.save(cartEntity);
        return new ResponseEntity<>(new ResponseApi(true, "Quantity edited"), HttpStatus.CREATED);
    }

}
