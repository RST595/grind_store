package com.bmxstore.grind_store.valid_object;
import com.bmxstore.grind_store.db.entity.CartEntity;
import com.bmxstore.grind_store.db.entity.CategoryEntity;
import com.bmxstore.grind_store.db.entity.ProductEntity;
import com.bmxstore.grind_store.db.entity.UserEntity;
import com.bmxstore.grind_store.dto.enums.Color;
import com.bmxstore.grind_store.dto.enums.Role;
import com.bmxstore.grind_store.dto.enums.UserStatus;
import java.util.ArrayList;


public class ReturnValidObject {

    public static final int quantity = 5;

    public static UserEntity getValidUser(){
        return new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>());
    }

    public static CategoryEntity getValidCategory(){
        return new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>());
    }

    public static ProductEntity getValidProduct(){
        return  new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                new CategoryEntity());
    }

    public static CartEntity getValidCart(){
        return new CartEntity(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", Color.BLACK,
                new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>())),
                ReturnValidObject.quantity,
                new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                        "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>()));
    }

}
