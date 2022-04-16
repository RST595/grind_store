package com.bmxstore.grind_store.service.user;

import com.bmxstore.grind_store.database.entity.user.UserEntity;
import com.bmxstore.grind_store.database.repository.UserCriteriaRepo;
import com.bmxstore.grind_store.database.repository.UserRepo;
import com.bmxstore.grind_store.dto.user.*;
import com.bmxstore.grind_store.ex_handler.ErrorMessage;
import com.bmxstore.grind_store.ex_handler.ServiceError;
import com.bmxstore.grind_store.response_api.ResponseApi;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.bmxstore.grind_store.database.entity.user.UserRole.ADMIN;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MSG = "user with email %s not found";
    private static final ErrorMessage USER_NOT_EXIST_MSG = ErrorMessage.valueOf("USER_NOT_EXIST");

    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserCriteriaRepo clientCriteriaRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                                    String.format(USER_NOT_FOUND_MSG, email)));
    }

    public Set<UserResponse> getAllUsers() {
        Set<UserResponse> allUsers = new HashSet<>();
        for (UserEntity user : userRepo.findAll()) {
            UserResponse userResponse = objectMapper.convertValue(user, UserResponse.class);
            userResponse.setOrders(user.getOrders());
            allUsers.add(userResponse);
        }
        return allUsers;
    }

    public Page<UserResponse> getUserWithSortingANdFiltering(UserPage userPage,
                                                          UserSearchCriteria userSearchCriteria) {
        return clientCriteriaRepo.findAllWithFilters(userPage, userSearchCriteria);
    }

    public ResponseEntity<ResponseApi> addUser(UserRequest newUser) {

        //TODO: add request check in separate class
        if(newUser.getEmail().replace(" ", "").isEmpty() ||
                newUser.getPassword().replace(" ", "").isEmpty()){
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, USER_NOT_EXIST_MSG);
        }
        UserEntity userEntity = objectMapper.convertValue(newUser, UserEntity.class);
        for (UserEntity user : userRepo.findAll()) {
            if (user.getEmail().equals(newUser.getEmail()) && user.isEnabled()) {
                throw new ServiceError(HttpStatus.CONFLICT, ErrorMessage.valueOf("DUPLICATED_EMAIL"));
            }
        }
        String encodePassword = bCryptPasswordEncoder.encode(userEntity.getPassword());
        userEntity.setPassword(encodePassword);
        userRepo.save(userEntity);
        return new ResponseEntity<>(new ResponseApi(true, "user added"), HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseApi> addAdmin(AdminRequest newAdmin) {

        if(newAdmin.getEmail().replace(" ", "").isEmpty() ||
                newAdmin.getPassword().replace(" ", "").isEmpty()){
            throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, USER_NOT_EXIST_MSG);
        }
        UserEntity userEntity = objectMapper.convertValue(newAdmin, UserEntity.class);
        for (UserEntity user : userRepo.findAll()) {
            if (user.getEmail().equals(newAdmin.getEmail()) && user.isEnabled()) {
                throw new ServiceError(HttpStatus.CONFLICT, ErrorMessage.valueOf("DUPLICATED_EMAIL"));
            }
        }
        userEntity.setUserRole(ADMIN);
        userEntity.setAddress("INTERNAL");
        String encodePassword = bCryptPasswordEncoder.encode(userEntity.getPassword());
        userEntity.setPassword(encodePassword);
        userRepo.save(userEntity);
        return new ResponseEntity<>(new ResponseApi(true, "user added"), HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseApi> updateUser(UserRequest updatedUser, Long userId) throws JsonMappingException {
        Optional<UserEntity> userById = userRepo.findById(userId);
        UserEntity oldUser = userById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, USER_NOT_EXIST_MSG));
        UserEntity newUser = objectMapper.convertValue(updatedUser, UserEntity.class);
        newUser.setOrders(oldUser.getOrders());
        oldUser = objectMapper.updateValue(oldUser, newUser);
        userRepo.save(oldUser);
        return new ResponseEntity<>(new ResponseApi(true, "user updated"), HttpStatus.OK);
    }

    public ResponseEntity<ResponseApi> deleteUser(Long userId) {
        for(UserEntity user : userRepo.findAll()){
            if(user.getId().equals(userId)){
                user.setEnabled(false);
                userRepo.save(user);
                return new ResponseEntity<>(new ResponseApi(true, "User was deleted"), HttpStatus.OK);
            }
        }
        throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("NOT_FOUND"));
    }


}
