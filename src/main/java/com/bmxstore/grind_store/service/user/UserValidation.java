package com.bmxstore.grind_store.service.user;

import com.bmxstore.grind_store.data.entity.user.UserEntity;
import com.bmxstore.grind_store.data.repository.UserRepo;
import com.bmxstore.grind_store.dto.user.UserRequest;
import com.bmxstore.grind_store.exception_handler.ErrorMessage;
import com.bmxstore.grind_store.exception_handler.ServiceError;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidation {

    private final UserRepo userRepo;

    boolean validateUserRequest(UserRequest newUser){
        if(!EmailValidator.getInstance().isValid(newUser.getEmail())) {
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("WRONG_EMAIL"));
        }
        if(!newUser.getPassword().equals(newUser.getConfirmPassword())){
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("USER_PASSWORD_FAIL"));
        }
        for (UserEntity user : userRepo.findAll()) {
            if (user.getEmail().equals(newUser.getEmail()) && user.isEnabled()) {
                throw new ServiceError(HttpStatus.CONFLICT, ErrorMessage.valueOf("DUPLICATED_EMAIL"));
            }
        }
        return true;
    }
}
