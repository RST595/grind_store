package com.bmxstore.grindStore.web;

import com.bmxstore.grindStore.ResponseApi.ResponseApi;
import com.bmxstore.grindStore.Service.UserService;
import com.bmxstore.grindStore.dto.User.UserRequest;
import com.bmxstore.grindStore.dto.User.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Set<UserResponse> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseApi> addUser(@RequestBody UserRequest newUser) {
        return this.userService.addUser(newUser);
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseApi> updateCategory(@RequestBody UserRequest updatedUser,
                                                      @RequestParam Long userId) {
        return this.userService.updateUser(updatedUser, userId);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ResponseApi> deleteUser(@PathVariable Long userId) {
        return this.userService.deleteUser(userId);
    }
}
