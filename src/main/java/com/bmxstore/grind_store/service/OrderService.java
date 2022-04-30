package com.bmxstore.grind_store.service;

import com.bmxstore.grind_store.data.entity.order.OrderEntity;
import com.bmxstore.grind_store.data.entity.order.OrderItemEntity;
import com.bmxstore.grind_store.data.entity.user.UserEntity;
import com.bmxstore.grind_store.dto.order.OrderResponse;
import com.bmxstore.grind_store.exception_handler.ErrorMessage;
import com.bmxstore.grind_store.exception_handler.ServiceError;
import com.bmxstore.grind_store.dto.ServerResponseDTO;
import com.bmxstore.grind_store.data.entity.*;
import com.bmxstore.grind_store.data.repository.*;
import com.bmxstore.grind_store.data.entity.order.OrderStatus;
import com.bmxstore.grind_store.dto.order.PaymentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final CurrencyService currencyService;
    private final ObjectMapper objectMapper;

    private final UserRepo userRepo;
    private final CartRepo cartRepo;

    @Value("${variables.currencyTo}")
    private String currencyTo;

    @SneakyThrows
    public ResponseEntity<ServerResponseDTO> createOrder(Long userId) {
        Double rate = currencyService.getCurrencyRate(currencyTo);
        Optional<UserEntity> user = userRepo.findById(userId);
        UserEntity userEntity = user.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("USER_ID_NOT_FOUND")));
        OrderEntity order = new OrderEntity();
        List<OrderItemEntity> userOrderItems = new ArrayList<>();
        Double totalOrderPrice = 0.0;
        order.setStatus(OrderStatus.NEW);
        order.setDeliveryAddress(userEntity.getAddress());
        order.setCreatedDate(LocalDate.now());
        order.setUserEntity(userEntity);
        for(CartEntity cart : cartRepo.findAll()){
            if(cart.getUserEntity().equals(userEntity)){
                OrderItemEntity orderItem = new OrderItemEntity();
                orderItem.setProductEntity(cart.getProductEntity());
                orderItem.setQuantity(cart.getQuantity());
                orderItem.setPrice(cart.getProductEntity().getPrice() * cart.getQuantity() * rate);
                orderItem.setOrderEntity(order);
                userOrderItems.add(orderItem);
                totalOrderPrice += orderItem.getPrice();
                cartRepo.deleteById(cart.getId());
            }
        }
        if(userOrderItems.isEmpty()){
            throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("CART_ITEM_NOT_FOUND"));
        }
        order.setTotalPrice(totalOrderPrice);
        order.setOrderItems(userOrderItems);
        orderRepo.save(order);
        return new ResponseEntity<>(new ServerResponseDTO(true, "Order Created"), HttpStatus.CREATED);
    }

    public ResponseEntity<ServerResponseDTO> payForOrder(Long orderId, PaymentRequest card) {
        LocalDate now = LocalDate.now();
        LocalDate cardExp = LocalDate.of(card.getExpYear(), card.getExpMonth(), 1);
        Optional<OrderEntity> orderById = orderRepo.findById(orderId);
        OrderEntity order = orderById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("ORDER_NOT_FOUND")));
        if(!order.getStatus().equals(OrderStatus.NEW) && !order.getStatus().equals(OrderStatus.PAYMENT_FAILED)) {
            return new ResponseEntity<>(new ServerResponseDTO(false, "Order payed/canceled"), HttpStatus.OK);
        }
        if(card.getCardId() != null && ChronoUnit.YEARS.between(now, cardExp) >= 0 &&
                ChronoUnit.MONTHS.between(now, cardExp) >= 0){
            order.setStatus(OrderStatus.PAID);
            orderRepo.save(order);
            return new ResponseEntity<>(new ServerResponseDTO(true, "Order has been paid"), HttpStatus.OK);
        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            orderRepo.save(order);
            return new ResponseEntity<>(new ServerResponseDTO(false, "Payment fail"), HttpStatus.NOT_ACCEPTABLE);
        }

    }

    public ResponseEntity<ServerResponseDTO> changeOrderStatus(OrderStatus status, Long orderId) {
        Optional<OrderEntity> orderById = orderRepo.findById(orderId);
        OrderEntity order = orderById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("ORDER_NOT_FOUND")));
        order.setStatus(status);
        orderRepo.save(order);
        return new ResponseEntity<>(new ServerResponseDTO(true, "Order " + status), HttpStatus.OK);
    }

    public List<OrderResponse> getListOfUserOrders(Long userId){
        List<OrderResponse> listOfOrdersResponse = new ArrayList<>();
        Optional<UserEntity> userById = userRepo.findById(userId);
        UserEntity user = userById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("USER_NOT_EXIST")));
        List<OrderEntity> listOfOrdersEntity = user.getOrders();
        for(OrderEntity order : listOfOrdersEntity){
            OrderResponse orderResponse = objectMapper.convertValue(order, OrderResponse.class);
            listOfOrdersResponse.add(orderResponse);
        }
        return listOfOrdersResponse;
    }
}
