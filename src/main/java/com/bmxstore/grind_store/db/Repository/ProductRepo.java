package com.bmxstore.grind_store.db.Repository;

import com.bmxstore.grind_store.db.Entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    void deleteById(Long id);
}
