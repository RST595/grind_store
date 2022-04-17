package com.bmxstore.grind_store.valid_object;
import com.bmxstore.grind_store.data.entity.CartEntity;
import com.bmxstore.grind_store.data.entity.CategoryEntity;
import com.bmxstore.grind_store.data.entity.product.ProductEntity;
import com.bmxstore.grind_store.data.entity.user.UserEntity;
import com.bmxstore.grind_store.data.entity.product.ProductColor;

import static com.bmxstore.grind_store.data.entity.user.UserRole.USER;


public class ReturnValidObject {

    public static final int quantity = 5;

    public static UserEntity getValidUser(){
        return new UserEntity("Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", USER, "12345");
    }

    public static CategoryEntity getValidCategory(){
        return new CategoryEntity("stem", "stem.jpg");
    }

    public static ProductEntity getValidProduct(){
        return  new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", ProductColor.BLACK,
                new CategoryEntity());
    }

    public static CartEntity getValidCart(){
        return new CartEntity(new ProductEntity(1L, "Odyssey Elementary V3", "PCODE123",
                "stem.jpg", 5000.0, 250.0, "To fix bar", ProductColor.BLACK,
                new CategoryEntity("stem", "stem.jpg")),
                ReturnValidObject.quantity,
                new UserEntity("Ivan", "Ivanov", "Saint Petersburg",
                        "ivanov@mail.ru", USER, "12345"));
    }

}
