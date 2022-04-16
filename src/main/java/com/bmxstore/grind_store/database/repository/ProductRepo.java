package com.bmxstore.grind_store.database.repository;

import com.bmxstore.grind_store.database.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    void deleteById(Long id);
}
