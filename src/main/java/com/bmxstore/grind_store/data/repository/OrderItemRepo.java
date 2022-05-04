package com.bmxstore.grind_store.data.repository;

import com.bmxstore.grind_store.data.entity.order.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItemEntity, Long> {
}
