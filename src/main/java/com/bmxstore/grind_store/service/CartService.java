package com.bmxstore.grind_store.service;

import com.bmxstore.grind_store.exception_handler.ErrorMessage;
import com.bmxstore.grind_store.exception_handler.ServiceError;
import com.bmxstore.grind_store.dto.ServerResponseDTO;
import com.bmxstore.grind_store.data.entity.CartEntity;
import com.bmxstore.grind_store.data.entity.product.ProductEntity;
import com.bmxstore.grind_store.data.entity.user.UserEntity;
import com.bmxstore.grind_store.data.repository.CartRepo;
import com.bmxstore.grind_store.data.repository.ProductRepo;
import com.bmxstore.grind_store.data.repository.UserRepo;
import com.bmxstore.grind_store.dto.cart.AddToCartRequest;
import com.bmxstore.grind_store.dto.cart.CartResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

    private final ObjectMapper objectMapper;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;

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

    public ResponseEntity<ServerResponseDTO> addToCart(AddToCartRequest addToCartRequest, Long userId) {
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
                return new ResponseEntity<>(new ServerResponseDTO(true, "Added to cart"), HttpStatus.CREATED);
            }
        }
        cartRepo.save(newCartItem);
        return new ResponseEntity<>(new ServerResponseDTO(true, "Added to cart"), HttpStatus.CREATED);
    }

    public ResponseEntity<ServerResponseDTO> removeFromCart(Long cartId, Long userId) {
        if(userRepo.findById(userId).isEmpty()){
            throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("USER_ID_NOT_FOUND"));
        }
        Optional<CartEntity> cartItem = cartRepo.findById(cartId);
        CartEntity cartEntity = cartItem.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("CART_ITEM_NOT_FOUND")));
        if(!cartEntity.getUserEntity().getId().equals(userId)) {
            return new ResponseEntity<>(new ServerResponseDTO(false, "User doesn't have this item"), HttpStatus.NOT_ACCEPTABLE);
        }
        cartRepo.delete(cartEntity);
        return new ResponseEntity<>(new ServerResponseDTO(true, "Item deleted"), HttpStatus.CREATED);
    }

    public ResponseEntity<ServerResponseDTO> updateItemQuantity(Long cartId, int quantity) {
        Optional<CartEntity> cartItem = cartRepo.findById(cartId);
        CartEntity cartEntity = cartItem.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("CART_ITEM_NOT_FOUND")));
        cartEntity.setQuantity(quantity);
        cartRepo.save(cartEntity);
        return new ResponseEntity<>(new ServerResponseDTO(true, "Quantity edited"), HttpStatus.CREATED);
    }

}
