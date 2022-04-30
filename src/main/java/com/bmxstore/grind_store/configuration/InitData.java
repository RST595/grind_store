package com.bmxstore.grind_store.configuration;

import com.bmxstore.grind_store.data.entity.CartEntity;
import com.bmxstore.grind_store.data.entity.CategoryEntity;
import com.bmxstore.grind_store.data.entity.product.ProductColor;
import com.bmxstore.grind_store.data.entity.product.ProductEntity;
import com.bmxstore.grind_store.data.entity.user.UserEntity;
import com.bmxstore.grind_store.data.entity.user.UserRole;
import com.bmxstore.grind_store.data.repository.CartRepo;
import com.bmxstore.grind_store.data.repository.CategoryRepo;
import com.bmxstore.grind_store.data.repository.ProductRepo;
import com.bmxstore.grind_store.data.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@AllArgsConstructor
public class InitData {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final CartRepo cartRepo;

//    @Bean
    public void initDB(){
        userRepo.saveAll(IntStream.rangeClosed(1, 50)
                .mapToObj(i -> new UserEntity("Firstname" + i, "Lastname" + i, "Address" + i,
                        "email" + i, UserRole.USER, bCryptPasswordEncoder.encode("string" + i)))
                .collect(Collectors.toList()));
        categoryRepo.saveAll(IntStream.rangeClosed(1, 50)
                .mapToObj(i -> new CategoryEntity("Title#" + i, "Picture#" + i))
                .collect(Collectors.toList()));
        for(int i = 1; i <=50; i++) {
            ProductEntity product = new ProductEntity("ProductName#" + i, "PRCD" + i,
                    "ProductImage" + i, 1000.0 + i, 100.0 + i,
                    "Decription" + i, ProductColor.BLACK);
            product.setCategoryEntity(categoryRepo.getById((long) i));
            productRepo.save(product);
            Optional<UserEntity> userById = userRepo.findById((long) i);
            UserEntity user = userById.orElse(new UserEntity("Firstname" + i, "Lastname" + i, "Address" + i,
                            "email" + i, UserRole.USER, bCryptPasswordEncoder.encode("string" + i)));
            Optional<ProductEntity> productById = productRepo.findById((long) i);
            ProductEntity product1 = productById.orElse(new ProductEntity("ProductName#" + i, "PRCD" + i,
                    "ProductImage" + i, 1000.0 + i, 100.0 + i,
                    "Decription" + i, ProductColor.BLACK));
            CartEntity cartItem = new CartEntity(product1, i, user);
            cartRepo.save(cartItem);
        }
    }
}
