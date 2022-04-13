package com.bmxstore.grind_store.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSearchCriteria {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
}
