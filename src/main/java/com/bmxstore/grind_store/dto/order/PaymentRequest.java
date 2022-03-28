package com.bmxstore.grind_store.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String cardId;
    private int expYear;
    private int expMonth;
    private String cvv;
    private String cardHolder;
}
