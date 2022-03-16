package com.bmxstore.grind_store.db.repository;

import com.bmxstore.grind_store.db.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepo extends JpaRepository<CartEntity, Long> {
}
