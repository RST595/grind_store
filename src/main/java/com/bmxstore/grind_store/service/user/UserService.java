package com.bmxstore.grind_store.service.user;

import com.bmxstore.grind_store.data.entity.user.UserEntity;
import com.bmxstore.grind_store.service.user.user_filtering.UserCriteriaRepo;
import com.bmxstore.grind_store.data.repository.UserRepo;
import com.bmxstore.grind_store.dto.user.*;
import com.bmxstore.grind_store.exception_handler.ErrorMessage;
import com.bmxstore.grind_store.exception_handler.ServiceError;
import com.bmxstore.grind_store.dto.ServerResponseDTO;
import com.bmxstore.grind_store.service.user.user_filtering.UserPage;
import com.bmxstore.grind_store.service.user.user_filtering.UserSearchCriteria;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

import static com.bmxstore.grind_store.data.entity.user.UserRole.ADMIN;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    @Value("${variables.keyWord}")
    private String keyWord;

    private static final String USER_NOT_FOUND_MSG = "user with email %s not found";
    private static final ErrorMessage USER_NOT_EXIST_MSG = ErrorMessage.valueOf("USER_NOT_EXIST");

    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserCriteriaRepo clientCriteriaRepo;
    private final UserValidation userValidation;

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

    public ResponseEntity<ServerResponseDTO> addUser(UserRequest newUser) {
        if(userValidation.validateUserRequest(newUser)){
            UserEntity userEntity = objectMapper.convertValue(newUser, UserEntity.class);
            String encodePassword = bCryptPasswordEncoder.encode(userEntity.getPassword());
            userEntity.setPassword(encodePassword);
            userRepo.save(userEntity);
            return new ResponseEntity<>(new ServerResponseDTO(true, "user added"), HttpStatus.CREATED);
        }
        throw new ServiceError(HttpStatus.NOT_ACCEPTABLE, USER_NOT_EXIST_MSG);
    }

    public String addAdmin(AdminRequest admin) {
        if(!admin.getKeyWord().equals(keyWord)){
            return "registration_error";
        }
        UserRequest adminRequest = objectMapper.convertValue(admin, UserRequest.class);
        adminRequest.setAddress("INTERNAL");
        adminRequest.setUserRole(ADMIN);
        if(userValidation.validateUserRequest(adminRequest)){
            UserEntity userEntity = objectMapper.convertValue(adminRequest, UserEntity.class);
            String encodePassword = bCryptPasswordEncoder.encode(userEntity.getPassword());
            userEntity.setPassword(encodePassword);
            userRepo.save(userEntity);
            return "redirect:/login";
        }
        return "registration_error";
    }

    public ResponseEntity<ServerResponseDTO> updateUser(UserRequest updatedUser, Long userId) {
        Optional<UserEntity> userById = userRepo.findById(userId);
        UserEntity oldUser = userById.orElseThrow(() -> new ServiceError(HttpStatus.NOT_FOUND, USER_NOT_EXIST_MSG));
        UserEntity newUser = objectMapper.convertValue(updatedUser, UserEntity.class);
        newUser.setOrders(oldUser.getOrders());
        try {
            oldUser = objectMapper.updateValue(oldUser, newUser);
        }catch (JsonMappingException e){
            System.out.println(e);
            throw new ServiceError(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.valueOf("SERVER_ERROR"));
        }
        userRepo.save(oldUser);
        return new ResponseEntity<>(new ServerResponseDTO(true, "user updated"), HttpStatus.OK);
    }

    public ResponseEntity<ServerResponseDTO> deleteUser(Long userId) {
        for(UserEntity user : userRepo.findAll()){
            if(user.getId().equals(userId)){
                user.setEnabled(false);
                userRepo.save(user);
                return new ResponseEntity<>(new ServerResponseDTO(true, "User was deleted"), HttpStatus.OK);
            }
        }
        throw new ServiceError(HttpStatus.NOT_FOUND, ErrorMessage.valueOf("NOT_FOUND"));
    }


}
