package com.bmxstore.grind_store.api_controller;

import com.bmxstore.grind_store.service.user.user_filtering.UserPage;
import com.bmxstore.grind_store.service.user.user_filtering.UserSearchCriteria;
import com.bmxstore.grind_store.dto.ServerResponseDTO;
import com.bmxstore.grind_store.service.user.UserService;
import com.bmxstore.grind_store.dto.user.UserRequest;
import com.bmxstore.grind_store.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@Tag(name = "Users", description = "Show, add, update or delete user.")
@SecurityRequirement(name = "swagger")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Show all registered users")
    @GetMapping("/list")
    public Set<UserResponse> getAllUsers() {
        return this.userService.getAllUsers();
    }

    @Operation(summary = "Show all registered users with filer and sorting")
    @GetMapping("/search")
    public Page<UserResponse> getUsersPageFilter(
            @RequestParam (value = "page number", required = false, defaultValue = "0") int pageNumber,
            @RequestParam (value = "page size", required = false, defaultValue = "10") int pageSize,
            @RequestParam (value = "Sort direction", required = false) Direction sortDirection,
            @RequestParam (value = "sort by", required = false, defaultValue = "lastName") String sortBy,
            @RequestParam (value = "Firs name", required = false) String firstName,
            @RequestParam (value = "Last name", required = false) String lastName,
            @RequestParam (value = "e-mail", required = false) String email,
            @RequestParam (value = "Address", required = false) String address) {
        UserPage userPage = new UserPage(pageNumber, pageSize, sortDirection, sortBy);
        UserSearchCriteria userSearchCriteria = new UserSearchCriteria(firstName, lastName, email, address);
        return userService.getUserWithSortingANdFiltering(userPage, userSearchCriteria);
    }

    @Operation(summary = "Sign up new user")
    @PostMapping("/add")
    public ResponseEntity<ServerResponseDTO> addUser(@RequestBody UserRequest newUser) {
        return this.userService.addUser(newUser);
    }

    @Operation(summary = "Update user information")
    @PostMapping("/update")
    public ResponseEntity<ServerResponseDTO> updateCategory(@RequestBody UserRequest updatedUser,
                                                            @RequestParam Long userId) {
        return this.userService.updateUser(updatedUser, userId);
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ServerResponseDTO> deleteUser(@PathVariable Long userId) {
        return this.userService.deleteUser(userId);
    }
}
