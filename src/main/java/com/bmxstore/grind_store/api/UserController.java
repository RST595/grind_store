package com.bmxstore.grind_store.api;

import com.bmxstore.grind_store.response_api.ResponseApi;
import com.bmxstore.grind_store.service.user.UserService;
import com.bmxstore.grind_store.dto.user.UserRequest;
import com.bmxstore.grind_store.dto.user.UserResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@Tag(name = "Users", description = "Show, add, update or delete user.")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Show all registered users")
    @GetMapping("/list")
    public Set<UserResponse> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @Operation(summary = "Sign up new user")
    @PostMapping("/add")
    public ResponseEntity<ResponseApi> addUser(@RequestBody UserRequest newUser) {
        return this.userService.addUser(newUser);
    }

    @Operation(summary = "Update user information")
    @PostMapping("/update")
    public ResponseEntity<ResponseApi> updateCategory(@RequestBody UserRequest updatedUser,
                                                      @RequestParam Long userId) throws JsonMappingException {
        return this.userService.updateUser(updatedUser, userId);
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ResponseApi> deleteUser(@PathVariable Long userId) {
        return this.userService.deleteUser(userId);
    }
}
