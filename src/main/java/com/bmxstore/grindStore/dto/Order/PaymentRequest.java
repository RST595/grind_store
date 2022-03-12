package com.bmxstore.grindStore.dto.Order;

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
