package com.bmxstore.grind_store.service.user.user_filtering;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientSearchCriteria {
    private String firstName;
    private String lastName;
    private String email;
    private String address;
}
