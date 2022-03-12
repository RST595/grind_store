package com.bmxstore.grindStore.dto.User;

import com.bmxstore.grindStore.dto.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private Role role;
    private String password;
}
