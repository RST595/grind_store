package com.bmxstore.grind_store.data.repository;

import com.bmxstore.grind_store.data.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    void deleteById(Long id);
}
