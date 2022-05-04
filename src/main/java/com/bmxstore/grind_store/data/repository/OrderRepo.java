package com.bmxstore.grind_store.data.repository;

import com.bmxstore.grind_store.data.entity.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserEntityId(Long userId);
}
