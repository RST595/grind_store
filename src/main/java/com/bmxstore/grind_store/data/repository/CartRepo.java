package com.bmxstore.grind_store.data.repository;

import com.bmxstore.grind_store.data.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepo extends JpaRepository<CartEntity, Long> {
}
