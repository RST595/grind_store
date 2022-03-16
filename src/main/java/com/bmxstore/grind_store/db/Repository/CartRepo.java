package com.bmxstore.grind_store.db.Repository;

import com.bmxstore.grind_store.db.Entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepo extends JpaRepository<CartEntity, Long> {
}
