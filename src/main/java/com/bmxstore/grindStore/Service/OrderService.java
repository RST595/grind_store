package com.bmxstore.grindStore.Service;

import com.bmxstore.grindStore.ExHandler.ErrorMessage;
import com.bmxstore.grindStore.ExHandler.ServiceError;
import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.db.Entity.*;
import com.bmxstore.grindStore.db.Repository.*;
import com.bmxstore.grindStore.dto.Enums.OrderStatus;
import com.bmxstore.grindStore.dto.Order.PaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final CurrencyService currencyService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CartRepo cartRepo;

    @Value("${variables.currency}")
    private String currency;

//TODO Fix order_items save, add new Feign client;
    @SneakyThrows
    public ResponseEntity<ResponseApi> createOrder(Long userId) {
        Optional<UserEntity> user = userRepo.findById(userId);
        UserEntity userEntity = user.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.USER_ID_NOT_FOUND));
        OrderEntity order = new OrderEntity();
        order.setStatus(OrderStatus.NEW);
        order.setDeliveryAddress(userEntity.getAddress());
        order.setCreatedDate(LocalDate.now());
        order.setUserEntity(userEntity);
        for(CartEntity cart : cartRepo.findAll()){
            if(cart.getUserEntity().getId().equals(userId)){
                OrderItemEntity orderItem = new OrderItemEntity();
                orderItem.setProductEntity(cart.getProductEntity());
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setPrice(cart.getProductEntity().getPrice()*cart.getQuantity());
                orderItem.setOrderEntity(order);
                order.setOrderItems(Arrays.asList(orderItem));
                if(order.getTotalPrice() == null){
                    order.setTotalPrice(orderItem.getPrice());
                } else {
                    order.setTotalPrice(order.getTotalPrice() + orderItem.getPrice());
                }
                //cartRepo.deleteById(cart.getId());
            }
        }
        orderRepo.save(order);
        return new ResponseEntity<>(new ResponseApi(true, "Order Created"), HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseApi> payForOrder(Long orderId, PaymentRequest card) {
        LocalDate now = LocalDate.now();
        LocalDate cardExp = LocalDate.of(card.getExpYear(), card.getExpMonth(), 1);
        Optional<OrderEntity> orderById = orderRepo.findById(orderId);
        OrderEntity order = orderById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.ORDER_NOT_FOUND));
        if(!order.getStatus().equals(OrderStatus.NEW) && !order.getStatus().equals(OrderStatus.PAYMENT_FAILED)) {
            return new ResponseEntity<>(new ResponseApi(false, "Order payed/canceled"), HttpStatus.OK);
        }
        if(card.getCardId() != null && ChronoUnit.YEARS.between(now, cardExp) >= 0 &&
                ChronoUnit.MONTHS.between(now, cardExp) >= 0){
            order.setStatus(OrderStatus.PAID);
            orderRepo.save(order);
            return new ResponseEntity<>(new ResponseApi(true, "Order has been paid"), HttpStatus.OK);
        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            orderRepo.save(order);
            return new ResponseEntity<>(new ResponseApi(false, "Payment fail"), HttpStatus.NOT_ACCEPTABLE);
        }

    }

    public ResponseEntity<ResponseApi> changeOrderStatus(OrderStatus status, Long orderId) {
        Optional<OrderEntity> orderById = orderRepo.findById(orderId);
        OrderEntity order = orderById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.ORDER_NOT_FOUND));
        order.setStatus(status);
        orderRepo.save(order);
        return new ResponseEntity<>(new ResponseApi(true, "Order " + status), HttpStatus.OK);
    }
}