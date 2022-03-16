package com.bmxstore.grindStore.Service;

import com.bmxstore.grindStore.ExHandler.ErrorMessage;
import com.bmxstore.grindStore.ExHandler.ServiceError;
import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.db.Entity.UserEntity;
import com.bmxstore.grindStore.db.Repository.UserRepo;
import com.bmxstore.grindStore.dto.Enums.Role;
import com.bmxstore.grindStore.dto.Enums.UserStatus;
import com.bmxstore.grindStore.dto.User.UserRequest;
import com.bmxstore.grindStore.dto.User.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    @Autowired
    ObjectMapper objectMapper;

    public Set<UserResponse> getAllUsers() {
        Set<UserResponse> allUsers = new HashSet<>();
        for (UserEntity user : userRepo.findAll()) {
            UserResponse userResponse = objectMapper.convertValue(user, UserResponse.class);
            userResponse.setOrders(user.getOrders());
            allUsers.add(userResponse);
        }
        return allUsers;
    }

    public ResponseEntity<ResponseApi> addUser(UserRequest newUser) {
        if(newUser.getEmail().replace(" ", "").isEmpty() ||
                newUser.getPassword().replace(" ", "").isEmpty()){
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, ErrorMessage.valueOf("USER_NOT_EXIST"));
        }
        UserEntity userEntity = objectMapper.convertValue(newUser, UserEntity.class);
        for (UserEntity user : userRepo.findAll()) {
            if (user.getEmail().equals(newUser.getEmail()) && user.getStatus().equals(UserStatus.ACTIVE)) {
                throw new ServiceError(HttpStatus.CONFLICT, ErrorMessage.valueOf("DUPLICATED_EMAIL"));
            }
        }
        userEntity.setStatus(UserStatus.ACTIVE);
        userRepo.save(userEntity);
        return new ResponseEntity<>(new ResponseApi(true, "user added"), HttpStatus.CREATED);
    }

    // TODO: 16.03.2022 also here and all places
    public ResponseEntity<ResponseApi> updateUser(UserRequest updatedUser, Long userId) {
        for (UserEntity user : userRepo.findAll()) {
            if(user.getId().equals(userId) && user.getStatus().equals(UserStatus.ACTIVE)) {
                if(!updatedUser.getFirstName().replace(" ", "").isEmpty()) {
                    user.setFirstName(updatedUser.getFirstName());
                }
                if(!updatedUser.getLastName().replace(" ", "").isEmpty()) {
                    user.setLastName(updatedUser.getLastName());
                }
                if(!updatedUser.getAddress().replace(" ", "").isEmpty()) {
                    user.setAddress(updatedUser.getAddress());
                }
                if(!updatedUser.getEmail().replace(" ", "").isEmpty()) {
                    user.setEmail((updatedUser.getEmail()));
                }
                if(!updatedUser.getPassword().replace(" ", "").isEmpty()) {
                    user.setPassword(updatedUser.getPassword());
                }
                if(updatedUser.getRole().equals(Role.USER) || updatedUser.getRole().equals(Role.ADMIN)){
                    user.setRole(updatedUser.getRole());
                }
                userRepo.save(user);
                return new ResponseEntity<>(new ResponseApi(true, "user updated"), HttpStatus.OK);
            }
        }
        throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("USER_NOT_EXIST"));
    }

    public ResponseEntity<ResponseApi> deleteUser(Long userId) {
        for(UserEntity user : userRepo.findAll()){
            if(user.getId().equals(userId)){
                user.setStatus(UserStatus.WAS_DROP);
                userRepo.save(user);
                return new ResponseEntity<>(new ResponseApi(true, "deleted deleted"), HttpStatus.OK);
            }
        }
        throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("NOT_FOUND"));
    }

}
