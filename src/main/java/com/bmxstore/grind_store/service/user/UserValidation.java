package com.bmxstore.grind_store.service.user;

import com.bmxstore.grind_store.data.entity.user.UserEntity;
import com.bmxstore.grind_store.data.repository.UserRepo;
import com.bmxstore.grind_store.dto.user.UserRequest;
import com.bmxstore.grind_store.exception_handler.ErrorMessage;
import com.bmxstore.grind_store.exception_handler.ServiceError;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidation {

    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;
    private static final ErrorMessage USER_NOT_EXIST_MSG = ErrorMessage.valueOf("USER_NOT_EXIST");

    //TODO: add request check in separate class
    boolean validateUserRequest(UserRequest newUser){
        if(newUser.getEmail().replace(" ", "").isEmpty() ||
                newUser.getPassword().replace(" ", "").isEmpty()){
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, USER_NOT_EXIST_MSG);
        }
        for (UserEntity user : userRepo.findAll()) {
            if (user.getEmail().equals(newUser.getEmail()) && user.isEnabled()) {
                throw new ServiceError(HttpStatus.CONFLICT, ErrorMessage.valueOf("DUPLICATED_EMAIL"));
            }
        }
        return true;
    }
}
