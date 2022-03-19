package com.bmxstore.grind_store.service;

import com.bmxstore.grind_store.ex_handler.ErrorMessage;
import com.bmxstore.grind_store.ex_handler.ServiceError;
import com.bmxstore.grind_store.response_api.ResponseApi;
import com.bmxstore.grind_store.db.entity.UserEntity;
import com.bmxstore.grind_store.db.repository.UserRepo;
import com.bmxstore.grind_store.dto.enums.UserStatus;
import com.bmxstore.grind_store.dto.user.UserRequest;
import com.bmxstore.grind_store.dto.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;

    @Autowired
    private ConfigurationService configurationService;

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
        configurationService.jsonConf();
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

    //FIXed
    // TODO: 16.03.2022 also here and all places
    public ResponseEntity<ResponseApi> updateUser(UserRequest updatedUser, Long userId) throws JsonMappingException {
        Optional<UserEntity> userById = userRepo.findById(userId);
        configurationService.jsonConf();
        UserEntity oldUser = userById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("USER_NOT_EXIST")));
        UserEntity newUser = objectMapper.convertValue(updatedUser, UserEntity.class);
        newUser.setOrders(oldUser.getOrders());
        oldUser = objectMapper.updateValue(oldUser, newUser);
        userRepo.save(oldUser);
        return new ResponseEntity<>(new ResponseApi(true, "user updated"), HttpStatus.OK);
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
