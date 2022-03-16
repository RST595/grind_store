package com.bmxstore.grind_store.db.Repository;

import com.bmxstore.grind_store.db.Entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItemEntity, Long> {
}
