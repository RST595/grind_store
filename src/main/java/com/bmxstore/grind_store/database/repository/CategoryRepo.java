package com.bmxstore.grind_store.database.repository;

import com.bmxstore.grind_store.database.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<CategoryEntity, Long> {
    void deleteById(Long id);
    CategoryEntity findByTitle(String title);
}