package com.bmxstore.grindStore.FeignClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllCurrencies {
    Currency usd;
    Currency eur;
}
