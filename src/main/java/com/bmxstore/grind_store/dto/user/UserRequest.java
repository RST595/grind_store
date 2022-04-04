package com.bmxstore.grind_store.dto.user;

import com.bmxstore.grind_store.db.entity.user.UserRole;
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
    private UserRole userRole;
    private String password;
}
