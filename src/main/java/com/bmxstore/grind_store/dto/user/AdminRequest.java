package com.bmxstore.grind_store.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRequest {
    private String firstName;
    private String lastName;
    private String keyWord;
    private String email;
    private String password;
    private String confirmPassword;
}