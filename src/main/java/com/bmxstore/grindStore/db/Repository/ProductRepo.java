package com.bmxstore.grindStore.db.Repository;

import com.bmxstore.grindStore.db.Entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    void deleteById(Long id);
}
