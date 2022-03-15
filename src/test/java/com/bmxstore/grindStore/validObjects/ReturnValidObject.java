package com.bmxstore.grindStore.validObjects;
import com.bmxstore.grindStore.db.Entity.CartEntity;
import com.bmxstore.grindStore.db.Entity.CategoryEntity;
import com.bmxstore.grindStore.db.Entity.ProductEntity;
import com.bmxstore.grindStore.db.Entity.UserEntity;
import com.bmxstore.grindStore.dto.Enums.Color;
import com.bmxstore.grindStore.dto.Enums.Role;
import com.bmxstore.grindStore.dto.Enums.UserStatus;
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
                new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>()));
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
