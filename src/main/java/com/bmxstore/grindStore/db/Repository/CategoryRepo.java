package com.bmxstore.grindStore.db.Repository;

import com.bmxstore.grindStore.db.Entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<CategoryEntity, Long> {
    void deleteById(Long id);
    CategoryEntity findByTitle(String title);
}
