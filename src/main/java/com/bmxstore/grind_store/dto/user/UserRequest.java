package com.bmxstore.grind_store.dto.user;

import com.bmxstore.grind_store.dto.enums.Role;
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
