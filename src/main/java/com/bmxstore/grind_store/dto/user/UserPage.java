package com.bmxstore.grind_store.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
public class UserPage {
    private int pageNumber;
    private int pageSize;
    private Sort.Direction sortDirection;
    private String sortBy;
}
