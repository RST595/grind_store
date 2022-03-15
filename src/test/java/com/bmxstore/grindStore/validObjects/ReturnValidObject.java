package com.bmxstore.grindStore.validObjects;
import com.bmxstore.grindStore.db.Entity.CategoryEntity;
import com.bmxstore.grindStore.db.Entity.UserEntity;
import com.bmxstore.grindStore.dto.Enums.Role;
import com.bmxstore.grindStore.dto.Enums.UserStatus;
import java.util.ArrayList;


public class ReturnValidObject {

    public static UserEntity getValidUser(){
        return new UserEntity(1L, "Ivan", "Ivanov", "Saint Petersburg",
                "ivanov@mail.ru", Role.USER, UserStatus.ACTIVE, "12345", new ArrayList<>());
    }

    public static CategoryEntity getValidCategory(){
        return new CategoryEntity(1L, "stem", "To fix bar", "stem.jpg", new ArrayList<>());
    }

}
